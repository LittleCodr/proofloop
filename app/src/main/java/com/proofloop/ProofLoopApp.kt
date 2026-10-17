package com.proofloop

import android.app.Application
import com.proofloop.data.repository.IProofLoopRepository
import com.proofloop.data.repository.ProofLoopRepository

class ProofLoopApp : Application() {

    lateinit var repository: IProofLoopRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = ProofLoopRepository()
    }

    companion object {
        lateinit var instance: ProofLoopApp
            private set
    }
}
