package com.proofloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.proofloop.core.ui.theme.ProofLoopTheme
import com.proofloop.core.ui.theme.ProofPaper
import com.proofloop.navigation.ProofLoopNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProofLoopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ProofPaper
                ) {
                    ProofLoopNavHost()
                }
            }
        }
    }
}

