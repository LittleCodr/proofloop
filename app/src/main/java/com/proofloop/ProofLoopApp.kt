package com.proofloop

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.proofloop.core.analytics.AnalyticsManager
import com.proofloop.data.repository.IProofLoopRepository
import com.proofloop.data.repository.ProofLoopRepository

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
    }

    companion object {
        lateinit var instance: ProofLoopApp
            private set
    }
}
