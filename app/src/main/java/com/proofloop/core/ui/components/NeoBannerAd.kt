package com.proofloop.core.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.proofloop.core.ui.theme.*

// Official Google AdMob sample banner ad unit ID for Android testing
const val ADMOB_TEST_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"

/**
 * Neo-Brutalist Test Banner Ad component for proof of monetization to hackathon judges.
 * Encapsulates Google AdMob AdView with an offline/demo fallback banner.
 */
@Composable
fun NeoBannerAd(
    modifier: Modifier = Modifier,
    adTitle: String = "SPONSORED PLACEMENT"
) {
    var adLoaded by remember { mutableStateOf(false) }
    var adFailedToLoad by remember { mutableStateOf(false) }

    NeoCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = ProofWhite,
        borderColor = ProofBorder,
        shadowOffset = 3.dp
    ) {
        // Monetization Micro-Header Tag
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(ProofAcidLime, RoundedCornerShape(3.dp))
                        .border(1.5.dp, ProofBorder, RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = ProofInk
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = adTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofTextMuted
                )
            }

            Text(
                text = "MONETIZATION DEMO",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                ),
                color = ProofCobalt
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ad Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            // Live AdMob Test Banner View
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                factory = { context ->
                    AdView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        setAdSize(AdSize.BANNER)
                        adUnitId = ADMOB_TEST_BANNER_ID
                        adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                adLoaded = true
                                adFailedToLoad = false
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                adLoaded = false
                                adFailedToLoad = true
                            }
                        }
                        try {
                            loadAd(AdRequest.Builder().build())
                        } catch (_: Throwable) {
                            adFailedToLoad = true
                        }
                    }
                }
            )

            // Offline / Demo Fallback Card (shown when network is restricted or test ads fail to fetch)
            if (!adLoaded) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, ProofBorder, RoundedCornerShape(6.dp)),
                    color = ProofLavender,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ProofCobalt, RoundedCornerShape(6.dp))
                                .border(1.5.dp, ProofBorder, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ProofWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Campus Partner: Cloud & AI Fellowship",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                ),
                                color = ProofInk
                            )
                            Text(
                                text = "Free $300 student credits + 1-on-1 alumni office hours.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp
                                ),
                                color = ProofTextMuted
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ProofInk,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
