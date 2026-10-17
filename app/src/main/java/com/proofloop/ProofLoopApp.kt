package com.proofloop

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.proofloop.core.analytics.AnalyticsManager
import com.proofloop.data.repository.IProofLoopRepository
import com.proofloop.data.repository.ProofLoopRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProofLoopApp : Application() {

    lateinit var repository: IProofLoopRepository
        private set

    lateinit var analytics: AnalyticsManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        analytics = AnalyticsManager(this)
        repository = ProofLoopRepository(analytics = analytics)

        // Initialize Google Mobile Ads SDK for monetization demo
        try {
            MobileAds.initialize(this) {}
        } catch (_: Throwable) {}

        // Initialize system notification channels for mission updates
        com.proofloop.core.notification.ProofLoopNotificationManager.createNotificationChannels(this)

        // Retrieve and sync device FCM token for push notifications
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    if (!token.isNullOrBlank()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            repository.updateFcmToken(token)
                        }
                    }
                }
        } catch (_: Throwable) {}
    }

    companion object {
        lateinit var instance: ProofLoopApp
            private set
    }
}
