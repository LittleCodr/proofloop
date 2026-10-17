package com.proofloop.core.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.proofloop.ProofLoopApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ProofLoopMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            try {
                ProofLoopApp.instance.repository.updateFcmToken(token)
            } catch (_: Exception) {
                // Ignore sync failures when user is logged out or offline
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "ProofLoop Mission Update"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "You have a new update in your mentorship workspace."

        val connectionId = remoteMessage.data["connectionId"]
        val requestId = remoteMessage.data["requestId"]

        ProofLoopNotificationManager.showMissionNotification(
            context = applicationContext,
            title = title,
            body = body,
            connectionId = connectionId,
            requestId = requestId
        )
    }
}
