import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";

/**
 * Triggers when a new connection request is created.
 * Dispatches an FCM heads-up push notification to the assigned alumnus.
 */
export const onConnectionCreated = functions.firestore
  .document("connections/{connectionId}")
  .onCreate(async (snap, context) => {
    const data = snap.data();
    if (!data) return;

    const connectionId = context.params.connectionId;
    const { studentName, requestTitle, alumniId } = data;

    if (!alumniId) return;

    try {
      const alumniDoc = await admin.firestore().collection("users").doc(alumniId).get();
      const fcmToken = alumniDoc.data()?.fcmToken;

      if (!fcmToken) {
        console.log(`[FCM] Alumnus ${alumniId} has no registered FCM token.`);
        return;
      }

      const message: admin.messaging.Message = {
        token: fcmToken,
        notification: {
          title: "New Loop Mission Request",
          body: `${studentName || "A student"} requested guidance: "${requestTitle || "Micro-mentorship"}"`,
        },
        data: {
          connectionId: connectionId,
          type: "NEW_REQUEST",
        },
        android: {
          priority: "high",
          notification: {
            channelId: "proofloop_missions",
            color: "#15121F",
          },
        },
      };

      await admin.messaging().send(message);
      console.log(`[FCM] Sent mission request notification to ${alumniId}`);
    } catch (error) {
      console.error(`[FCM Error] Failed sending request notification:`, error);
    }
  });

/**
 * Triggers when an alumnus accepts or declines a connection request.
 * Notifies the requesting student.
 */
export const onConnectionUpdated = functions.firestore
  .document("connections/{connectionId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();

    if (!before || !after) return;
    if (before.status === after.status) return;

    const connectionId = context.params.connectionId;
    const { studentId, alumniName } = after;

    if (!studentId) return;

    try {
      const studentDoc = await admin.firestore().collection("users").doc(studentId).get();
      const fcmToken = studentDoc.data()?.fcmToken;

      if (!fcmToken) return;

      let title = "ProofLoop Update";
      let body = "Your connection status has changed.";

      if (after.status === "ACCEPTED") {
        title = "Mission Accepted!";
        body = `${alumniName || "Your mentor"} accepted your request. Tap to open workspace and set your next action.`;
      } else if (after.status === "DECLINED") {
        title = "Mission Request Update";
        body = `${alumniName || "Mentor"} is currently at capacity and declined your request. Try another match.`;
      } else if (after.status === "COMPLETED") {
        title = "Loop Closed!";
        body = `Your mentorship mission with ${alumniName || "your mentor"} has been completed and confirmed.`;
      }

      const message: admin.messaging.Message = {
        token: fcmToken,
        notification: { title, body },
        data: {
          connectionId: connectionId,
          status: after.status,
          type: "STATUS_UPDATE",
        },
        android: {
          priority: "high",
          notification: {
            channelId: "proofloop_missions",
            color: "#15121F",
          },
        },
      };

      await admin.messaging().send(message);
      console.log(`[FCM] Sent status change (${after.status}) to student ${studentId}`);
    } catch (error) {
      console.error(`[FCM Error] Failed sending status update:`, error);
    }
  });

/**
 * Triggers on outcomes/{outcomeId} to handle two-step verification notifications:
 * - Student reports completion -> Alumnus notified to verify.
 * - Alumnus confirms outcome -> Student notified that loop is closed.
 */
export const onOutcomeUpdated = functions.firestore
  .document("outcomes/{outcomeId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();

    if (!before || !after) return;

    const { connectionId, studentId, alumniId, actionDescription } = after;

    // 1. Student newly marked complete
    if (!before.studentCompleted && after.studentCompleted) {
      try {
        const alumniDoc = await admin.firestore().collection("users").doc(alumniId).get();
        const fcmToken = alumniDoc.data()?.fcmToken;

        if (fcmToken) {
          await admin.messaging().send({
            token: fcmToken,
            notification: {
              title: "Action Reported Complete",
              body: `Student reported completion of: "${actionDescription || "Agreed task"}". Tap to review and confirm.`,
            },
            data: { connectionId, type: "STUDENT_COMPLETED" },
            android: {
              priority: "high",
              notification: { channelId: "proofloop_missions", color: "#E5B8FF" },
            },
          });
        }
      } catch (err) {
        console.error("[FCM Error] Failed notifying alumnus of completion:", err);
      }
    }

    // 2. Alumnus newly confirmed outcome
    if (!before.alumniConfirmed && after.alumniConfirmed) {
      try {
        const studentDoc = await admin.firestore().collection("users").doc(studentId).get();
        const fcmToken = studentDoc.data()?.fcmToken;

        if (fcmToken) {
          await admin.messaging().send({
            token: fcmToken,
            notification: {
              title: "Outcome Confirmed!",
              body: `Your mentor independently confirmed your action. Loop is officially closed!`,
            },
            data: { connectionId, type: "ALUMNI_CONFIRMED" },
            android: {
              priority: "high",
              notification: { channelId: "proofloop_missions", color: "#B9F5D0" },
            },
          });
        }
      } catch (err) {
        console.error("[FCM Error] Failed notifying student of confirmation:", err);
      }
    }
  });
