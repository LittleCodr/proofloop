package com.proofloop.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsManager(context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics? = try {
        FirebaseAnalytics.getInstance(context)
    } catch (_: Throwable) {
        null
    }

    fun setUserId(userId: String?) {
        try {
            firebaseAnalytics?.setUserId(userId)
        } catch (_: Throwable) {}
    }

    fun setUserRole(role: String) {
        try {
            firebaseAnalytics?.setUserProperty("user_role", role)
        } catch (_: Throwable) {}
    }

    fun logLogin(method: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, method)
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        } catch (_: Throwable) {}
    }

    fun logSignUp(method: String, role: String) {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, method)
                putString("role", role)
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        } catch (_: Throwable) {}
    }

    fun logCreateHelpRequest(requestId: String, category: String, durationMinutes: Int) {
        try {
            val bundle = Bundle().apply {
                putString("request_id", requestId)
                putString("category", category)
                putInt("duration_minutes", durationMinutes)
            }
            firebaseAnalytics?.logEvent("create_help_request", bundle)
        } catch (_: Throwable) {}
    }

    fun logSendConnectionRequest(requestId: String, alumniId: String) {
        try {
            val bundle = Bundle().apply {
                putString("request_id", requestId)
                putString("alumni_id", alumniId)
            }
            firebaseAnalytics?.logEvent("send_connection_request", bundle)
        } catch (_: Throwable) {}
    }

    fun logRespondConnection(connectionId: String, accepted: Boolean) {
        try {
            val bundle = Bundle().apply {
                putString("connection_id", connectionId)
                putBoolean("accepted", accepted)
            }
            firebaseAnalytics?.logEvent("respond_connection", bundle)
        } catch (_: Throwable) {}
    }

    fun logUpdateAgreedAction(connectionId: String) {
        try {
            val bundle = Bundle().apply {
                putString("connection_id", connectionId)
            }
            firebaseAnalytics?.logEvent("update_agreed_action", bundle)
        } catch (_: Throwable) {}
    }

    fun logStudentCompletion(connectionId: String) {
        try {
            val bundle = Bundle().apply {
                putString("connection_id", connectionId)
            }
            firebaseAnalytics?.logEvent("student_complete_action", bundle)
        } catch (_: Throwable) {}
    }

    fun logAlumniConfirmation(connectionId: String) {
        try {
            val bundle = Bundle().apply {
                putString("connection_id", connectionId)
            }
            firebaseAnalytics?.logEvent("alumni_confirm_outcome", bundle)
        } catch (_: Throwable) {}
    }
}
