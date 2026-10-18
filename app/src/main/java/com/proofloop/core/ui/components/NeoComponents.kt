package com.proofloop.core.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proofloop.core.ui.theme.*

// =========================================================================
// 1. BESPOKE NEO-BRUTALIST SPINNERS & ANIMATED INDICATORS
// =========================================================================

/**
 * High-impact Neo-Brutalist rotating geometric square spinner with inner pulsing diamond.
 */
@Composable
fun NeoSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = ProofVoltCitron
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinnerTransition")
    
    // Outer square fast rotation
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinnerAngle"
    )

    // Inner counter-rotation
    val innerAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerAngle"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = angle }
            .border(2.dp, ProofBorder, RoundedCornerShape(4.dp))
            .background(color, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Inner contrast diamond
        Box(
            modifier = Modifier
                .size(size * 0.45f)
                .graphicsLayer { rotationZ = innerAngle }
                .background(ProofObsidianInk, RoundedCornerShape(2.dp))
        )
    }
}

/**
 * Multi-color cycling geometric spinner for prominent loading states.
 */
@Composable
fun NeoGeometricSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "geoSpinnerTransition")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "geoRotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "geoScale"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
            }
            .border(2.5.dp, ProofBorder, RoundedCornerShape(6.dp))
            .background(ProofVoltCitron, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.5f)
                .rotate(45f)
                .background(ProofKleinBlue, RoundedCornerShape(2.dp))
                .border(1.5.dp, ProofWhite, RoundedCornerShape(2.dp))
        )
    }
}

/**
 * Neo-Brutalist Loading Indicator with animated message and pulsating dots.
 */
@Composable
fun NeoLoadingIndicator(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dotsTransition")
    val dotCount by infiniteTransition.animateValue(
        initialValue = 1,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotCount"
    )
    val dots = ".".repeat(dotCount.coerceIn(1, 3))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NeoGeometricSpinner(size = 36.dp)
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = ProofObsidianInk,
            border = BorderStroke(1.dp, ProofVoltCitron)
        ) {
            Text(
                text = "${message.uppercase()}$dots",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = ProofVoltCitron,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Animated real-time pulse radar indicator for live sync and active sessions.
 */
@Composable
fun NeoPulseIndicator(
    color: Color = ProofSignalEmerald,
    size: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier.size(size * 1.6f),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring
        Box(
            modifier = Modifier
                .size(size * scale)
                .graphicsLayer { this.alpha = alpha }
                .background(color.copy(alpha = 0.4f), CircleShape)
        )
        // Solid center dot with border
        Box(
            modifier = Modifier
                .size(size)
                .background(color, CircleShape)
                .border(1.dp, ProofBorder, CircleShape)
        )
    }
}

// =========================================================================
// 2. ELEVATED NEO-BRUTALIST CARDS (WITH ACCENT RIBS & STAMPS)
// =========================================================================

/**
 * High-end Neo-Brutalist Card with crisp obsidian border, unblurred drop shadow,
 * tactile spring depression on click, and optional top accent stripe or stamp badge.
 * Optimized for compact, high-density minimalist layouts.
 */
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = ProofWhite,
    borderColor: Color = ProofBorder,
    shadowColor: Color = ProofObsidianInk,
    shadowOffset: Dp = 2.5.dp,
    cornerRadius: Dp = 8.dp,
    accentColor: Color? = null,
    stampText: String? = null,
    contentPadding: Dp = 10.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile 3D press depression
    val translation by animateDpAsState(
        targetValue = if (isPressed && onClick != null) 2.dp else 0.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "cardPressTranslation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = shadowOffset, end = shadowOffset)
    ) {
        // Hard unblurred offset shadow behind
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor, shape)
        )

        // Foreground tactile card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = translation, y = translation)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                    } else Modifier
                ),
            shape = shape,
            color = backgroundColor,
            border = BorderStroke(1.5.dp, borderColor)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Optional top accent stripe
                if (accentColor != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(accentColor)
                    )
                }
                
                // Content with compact padding & optional stamp badge
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding)
                ) {
                    if (stampText != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = ProofObsidianInk,
                                border = BorderStroke(1.dp, ProofVoltCitron)
                            ) {
                                Text(
                                    text = stampText.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 8.5.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = ProofVoltCitron,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    content()
                }
            }
        }
    }
}

// =========================================================================
// 3. MECHANICAL 3D BUTTONS WITH SPRING PHYSICS
// =========================================================================

/**
 * Neo-Brutalist Mechanical Button.
 * Physically translates down and right into its shadow on press using snappy spring bounce physics.
 * Compact 40dp height with crisp 12sp typography.
 */
@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ProofVoltCitron,
    contentColor: Color = ProofObsidianInk,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    shadowOffset: Dp = 2.5.dp,
    cornerRadius: Dp = 6.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Mechanical sink translation
    val translation by animateDpAsState(
        targetValue = if (isPressed && enabled) shadowOffset else 0.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "buttonTranslation"
    )

    val currentShadowOffset by animateDpAsState(
        targetValue = if (isPressed || !enabled) 0.dp else shadowOffset,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "buttonShadow"
    )

    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .padding(bottom = shadowOffset, end = shadowOffset)
    ) {
        // Shadow (collapses smoothly when pressed)
        if (enabled && currentShadowOffset > 0.dp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(ProofObsidianInk, shape)
            )
        }

        // Foreground surface
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = translation, y = translation)
                .then(
                    if (enabled && !isLoading) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClick
                        )
                    } else Modifier
                ),
            shape = shape,
            color = if (enabled) backgroundColor else ProofSurfaceMuted,
            border = BorderStroke(1.5.dp, if (enabled) ProofBorder else Color.Gray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    NeoSpinner(size = 16.dp, color = contentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PROCESSING...",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp
                        ),
                        color = contentColor
                    )
                } else {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (enabled) contentColor else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = text.uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (enabled) contentColor else Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * Stark Outlined Neo-Brutalist Button.
 */
@Composable
fun NeoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ProofWhite,
    contentColor: Color = ProofObsidianInk,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shadowOffset: Dp = 2.5.dp
) {
    NeoButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        icon = icon,
        enabled = enabled,
        shadowOffset = shadowOffset
    )
}

/**
 * High-voltage Cyber Citron CTA button shortcut.
 */
@Composable
fun NeoVoltButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false
) {
    NeoButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        backgroundColor = ProofVoltCitron,
        contentColor = ProofObsidianInk,
        icon = icon,
        isLoading = isLoading
    )
}

// =========================================================================
// 4. HIGH CONTRAST TEXT FIELD
// =========================================================================

/**
 * Neo-Brutalist Input Field with solid 2dp black border and error micro-tag.
 */
@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 4,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            ),
            color = ProofObsidianInk,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(text = placeholder, color = ProofCharcoalMuted)
                }
            },
            singleLine = singleLine,
            maxLines = maxLines,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            leadingIcon = leadingIcon,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ProofWhite,
                unfocusedContainerColor = ProofWhite,
                focusedBorderColor = ProofKleinBlue,
                unfocusedBorderColor = ProofBorder,
                errorBorderColor = ProofSolarTangerine,
                focusedTextColor = ProofObsidianInk,
                unfocusedTextColor = ProofObsidianInk,
                cursorColor = ProofKleinBlue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, if (isError) ProofSolarTangerine else ProofBorder, RoundedCornerShape(8.dp))
        )
        if (isError && errorMessage != null) {
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ProofSolarTangerine,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofSolarTangerine
                )
            }
        }
    }
}

// =========================================================================
// 5. STATUS BADGES, TAGS & VERIFICATION SEALS
// =========================================================================

/**
 * Neo-Brutalist Tag Pill.
 */
@Composable
fun NeoTag(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ProofDigitalLilac,
    contentColor: Color = ProofObsidianInk,
    borderColor: Color = ProofBorder
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor,
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Status Badge for Lifecycle states with pop-in animated colors and icons.
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (targetBg, targetFg, label) = when (status.uppercase()) {
        "OPEN" -> Triple(ProofPaper, ProofObsidianInk, "OPEN MISSION")
        "PENDING" -> Triple(ProofDigitalLilac, ProofObsidianInk, "PENDING")
        "ACCEPTED" -> Triple(ProofVoltCitron, ProofObsidianInk, "ACCEPTED")
        "DECLINED" -> Triple(ProofSolarTangerine, ProofWhite, "DECLINED")
        "COMPLETED" -> Triple(ProofSignalEmerald, ProofObsidianInk, "ACTION REPORTED")
        "CONFIRMED" -> Triple(ProofSignalEmerald, ProofObsidianInk, "VERIFIED CLOSED")
        else -> Triple(ProofSurfaceMuted, ProofObsidianInk, status.uppercase())
    }

    val animatedBg by animateColorAsState(targetValue = targetBg, animationSpec = tween(300), label = "badgeBg")
    val animatedFg by animateColorAsState(targetValue = targetFg, animationSpec = tween(300), label = "badgeFg")

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = animatedBg,
        border = BorderStroke(1.5.dp, ProofBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (status.uppercase() in listOf("COMPLETED", "CONFIRMED")) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = animatedFg,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = animatedFg
            )
        }
    }
}

/**
 * Segmented Capacity Battery Meter showing active mentor slots (e.g. 2 / 3).
 */
@Composable
fun NeoSegmentedCapacityMeter(
    activeCount: Int,
    maxCount: Int = 3,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = ProofObsidianInk,
        border = BorderStroke(1.5.dp, ProofBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CAPACITY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp
                ),
                color = ProofWhite
            )
            Spacer(modifier = Modifier.width(6.dp))

            // Segmented battery blocks
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                for (i in 1..maxCount) {
                    val isFilled = i <= activeCount
                    val isFullCapacity = activeCount >= maxCount
                    val blockColor = when {
                        !isFilled -> ProofSurfaceMuted.copy(alpha = 0.4f)
                        isFullCapacity -> ProofSolarTangerine
                        activeCount == maxCount - 1 -> ProofVoltCitron
                        else -> ProofSignalEmerald
                    }
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .height(14.dp)
                            .background(blockColor, RoundedCornerShape(2.dp))
                            .border(1.dp, ProofWhite.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$activeCount/$maxCount",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                ),
                color = if (activeCount >= maxCount) ProofSolarTangerine else ProofVoltCitron
            )
        }
    }
}

/**
 * Verification Seal Stamp for closed loops.
 */
@Composable
fun NeoVerificationSeal(
    text: String = "2-PARTY AUDITED",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = ProofSignalEmerald,
        border = BorderStroke(2.dp, ProofObsidianInk)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = ProofObsidianInk,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                color = ProofObsidianInk
            )
        }
    }
}

// =========================================================================
// 6. 5-STAGE WORKFLOW HERO (COMPACT CONNECTED CIRCUIT TIMELINE)
// =========================================================================

/**
 * 5-stage Workflow Hero Graphic with connected circuit timeline and animated pulse:
 * REQUEST → MATCH → CONNECT → ACT → CONFIRM.
 * Compact 36-40dp design for high-density minimalist layouts.
 */
@Composable
fun WorkflowHero(
    currentStep: Int, // 1 to 5
    modifier: Modifier = Modifier
) {
    val steps = listOf("REQ", "MATCH", "CONN", "ACT", "CONF")

    NeoCard(
        modifier = modifier,
        backgroundColor = ProofWhite,
        shadowOffset = 2.dp,
        cornerRadius = 6.dp,
        accentColor = ProofKleinBlue,
        contentPadding = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NeoPulseIndicator(size = 7.dp, color = ProofKleinBlue)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "5-STAGE CLOSED PROTOCOL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofCharcoalMuted
                )
            }
            Text(
                text = "STAGE 0$currentStep/05",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.5.sp
                ),
                color = ProofKleinBlue
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, stepName ->
                val stepNum = index + 1
                val isCurrent = stepNum == currentStep
                val isPassed = stepNum < currentStep

                val (targetBg, targetFg) = when {
                    isCurrent -> Pair(ProofVoltCitron, ProofObsidianInk)
                    isPassed -> Pair(ProofSignalEmerald, ProofObsidianInk)
                    else -> Pair(ProofPaperLight, ProofCharcoalMuted)
                }

                val animatedBg by animateColorAsState(targetValue = targetBg, animationSpec = tween(250), label = "stepBg")
                val animatedFg by animateColorAsState(targetValue = targetFg, animationSpec = tween(250), label = "stepFg")

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = animatedBg,
                    border = BorderStroke(1.dp, if (isCurrent) ProofObsidianInk else ProofBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPassed) "✓" else "0$stepNum",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 8.sp
                            ),
                            color = animatedFg
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = stepName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 7.5.sp,
                                letterSpacing = (-0.2).sp
                            ),
                            color = animatedFg,
                            maxLines = 1
                        )
                    }
                }

                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(1.5.dp)
                            .background(ProofBorder)
                    )
                }
            }
        }
    }
}

// =========================================================================
// 7. TOP APP BAR & SLEEK BOTTOM NAVIGATION DOCK
// =========================================================================

/**
 * Top App Bar with official logo, role pill, Impact Ledger link, and sign out button.
 * Compact and crisp.
 */
@Composable
fun NeoTopAppBar(
    title: String = "PROOFLOOP",
    subtitle: String? = null,
    roleTag: String? = null,
    onOpenImpactLedger: (() -> Unit)? = null,
    onSignOut: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ProofWhite,
        border = BorderStroke(1.5.dp, ProofBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = com.proofloop.R.drawable.proofloop_logo),
                        contentDescription = "ProofLoop Logo",
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .border(1.5.dp, ProofBorder, RoundedCornerShape(5.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = ProofObsidianInk
                    )
                    if (roleTag != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        NeoTag(
                            text = roleTag.uppercase(),
                            backgroundColor = when (roleTag.uppercase()) {
                                "ALUMNI" -> ProofSignalEmerald
                                "FOUNDER" -> ProofVoltCitron
                                "FACULTY" -> ProofDigitalLilac
                                "RECRUITER" -> ProofSolarTangerine
                                else -> ProofKleinBlue
                            },
                            contentColor = if (roleTag.uppercase() in listOf("ALUMNI", "FOUNDER")) ProofObsidianInk else ProofWhite
                        )
                    }
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ProofCharcoalMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onOpenImpactLedger != null) {
                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = ProofVoltCitron,
                        border = BorderStroke(1.5.dp, ProofBorder),
                        modifier = Modifier
                            .clickable(onClick = onOpenImpactLedger)
                            .padding(end = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = ProofObsidianInk,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "LEDGER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = ProofObsidianInk
                            )
                        }
                    }
                }

                if (onSignOut != null) {
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .size(30.dp)
                            .background(ProofPaper, RoundedCornerShape(5.dp))
                            .border(1.5.dp, ProofBorder, RoundedCornerShape(5.dp))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign Out",
                            tint = ProofObsidianInk,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Floating Neo-Brutalist Bottom Navigation Dock for fluid screen transitions.
 * Compact and tactile.
 */
@Composable
fun NeoBottomNavBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    pendingCount: Int = 0,
    acceptedCount: Int = 0
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        color = ProofObsidianInk,
        border = BorderStroke(1.5.dp, ProofVoltCitron),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Hub / Missions
            NeoNavTabItem(
                label = "HUB",
                icon = Icons.Default.RocketLaunch,
                isSelected = selectedTab == "HUB",
                onClick = { onTabSelected("HUB") }
            )

            // Tab 2: Requests / Inbox
            NeoNavTabItem(
                label = "INBOX",
                icon = Icons.Default.Mail,
                isSelected = selectedTab == "INBOX",
                badgeCount = pendingCount,
                onClick = { onTabSelected("INBOX") }
            )

            // Tab 3: Workspace
            NeoNavTabItem(
                label = "WORKSPACE",
                icon = Icons.Default.Handshake,
                isSelected = selectedTab == "WORKSPACE",
                badgeCount = acceptedCount,
                onClick = { onTabSelected("WORKSPACE") }
            )

            // Tab 4: Impact Ledger
            NeoNavTabItem(
                label = "LEDGER",
                icon = Icons.Default.Analytics,
                isSelected = selectedTab == "LEDGER",
                onClick = { onTabSelected("LEDGER") }
            )
        }
    }
}

@Composable
private fun NeoNavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) ProofVoltCitron else Color.Transparent,
        border = if (isSelected) BorderStroke(1.dp, ProofWhite) else null,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) ProofObsidianInk else ProofWhite,
                    modifier = Modifier.size(16.dp)
                )
                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-3).dp)
                            .size(7.dp)
                            .background(ProofSolarTangerine, CircleShape)
                    )
                }
            }
            if (isSelected) {
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    ),
                    color = ProofObsidianInk
                )
            }
        }
    }
}

// =========================================================================
// 8. TIMELINE & EMPTY STATE COMPONENTS
// =========================================================================

/**
 * 5-Stage Mission-to-Outcome Timeline Card with connected nodes and deliverable card.
 */
@Composable
fun MissionToOutcomeTimelineCard(
    currentStep: Int,
    actionDescription: String,
    dueDate: String,
    ownerName: String,
    isStudentCompleted: Boolean,
    isAlumniConfirmed: Boolean,
    modifier: Modifier = Modifier
) {
    val stages = listOf(
        Triple("REQUEST", "Scoped Mission", "COMPLETED"),
        Triple("MATCH", "Explainable Match", "COMPLETED"),
        Triple("CONNECT", "Mentor Decision", if (currentStep >= 3) "ACCEPTED" else "PENDING"),
        Triple("ACT", "Agreed Follow-through", if (isStudentCompleted) "REPORTED" else if (currentStep >= 4) "IN PROGRESS" else "PENDING"),
        Triple("CONFIRM", "Independent Verification", if (isAlumniConfirmed) "CONFIRMED" else "PENDING")
    )

    NeoCard(
        modifier = modifier,
        backgroundColor = ProofWhite,
        shadowOffset = 2.5.dp,
        cornerRadius = 8.dp,
        accentColor = if (isAlumniConfirmed) ProofSignalEmerald else ProofVoltCitron,
        stampText = if (isAlumniConfirmed) "LOOP FULLY CLOSED" else "ACTIVE COMMITMENT",
        contentPadding = 10.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MISSION-TO-OUTCOME TIMELINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = ProofObsidianInk
                )
                Text(
                    text = "5-Stage Verifiable Delivery Funnel",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = ProofCharcoalMuted
                )
            }
            if (isAlumniConfirmed) {
                NeoVerificationSeal(text = "VERIFIED")
            } else if (isStudentCompleted) {
                StatusBadge(status = "COMPLETED")
            } else {
                StatusBadge(status = "IN PROGRESS")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5 Stage Connected Nodes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            stages.forEachIndexed { index, (name, _, status) ->
                val stageNum = index + 1
                val isDone = status in listOf("COMPLETED", "ACCEPTED", "REPORTED", "CONFIRMED")
                val isCurrent = stageNum == currentStep

                val (nodeBg, nodeFg) = when {
                    status == "CONFIRMED" -> Pair(ProofSignalEmerald, ProofObsidianInk)
                    status == "REPORTED" -> Pair(ProofSignalEmerald, ProofObsidianInk)
                    isDone -> Pair(ProofSignalEmerald, ProofObsidianInk)
                    isCurrent -> Pair(ProofVoltCitron, ProofObsidianInk)
                    else -> Pair(ProofPaperLight, ProofCharcoalMuted)
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = nodeBg,
                    border = BorderStroke(1.dp, ProofBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isDone && !isCurrent) "✓" else "0$stageNum",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 8.5.sp
                            ),
                            color = nodeFg
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 7.5.sp
                            ),
                            color = nodeFg,
                            maxLines = 1
                        )
                    }
                }

                if (index < stages.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(1.5.dp)
                            .background(ProofBorder)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = ProofBorder, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Commitment Specification Box
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = ProofPaperLight,
            border = BorderStroke(1.dp, ProofBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "NEXT ACTION COMMITMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp
                    ),
                    color = ProofCharcoalMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = actionDescription.ifBlank { "Action defined and agreed during session" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    color = ProofObsidianInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = ProofObsidianInk,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Owner: ${ownerName.ifBlank { "Student" }}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                            color = ProofObsidianInk
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ProofObsidianInk,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Due: ${dueDate.ifBlank { "Within 7 days" }}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 11.sp),
                            color = ProofObsidianInk
                        )
                    }
                }
            }
        }
    }
}

/**
 * Meaningful Empty State Card.
 */
@Composable
fun EmptyStateCard(
    title: String,
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    NeoCard(
        modifier = modifier,
        backgroundColor = ProofWhite,
        shadowOffset = 2.5.dp,
        contentPadding = 12.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(ProofVoltCitron, RoundedCornerShape(8.dp))
                    .border(1.5.dp, ProofBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = ProofObsidianInk,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = ProofObsidianInk
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = ProofCharcoalMuted,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(10.dp))
                NeoVoltButton(
                    text = actionText,
                    onClick = onAction
                )
            }
        }
    }
}
