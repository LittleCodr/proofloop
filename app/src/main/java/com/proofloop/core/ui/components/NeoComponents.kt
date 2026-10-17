package com.proofloop.core.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proofloop.core.ui.theme.*

/**
 * Neo-Brutalist rotating square spinner.
 */
@Composable
fun NeoSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    color: Color = ProofAcidLime
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinnerTransition")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinnerAngle"
    )

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = angle }
            .border(2.dp, ProofBorder, RoundedCornerShape(3.dp))
            .background(color, RoundedCornerShape(3.dp))
    )
}

/**
 * Neo-Brutalist Loading Indicator with message.
 */
@Composable
fun NeoLoadingIndicator(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NeoSpinner(size = 32.dp, color = ProofAcidLime)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = message.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = ProofInk
        )
    }
}

/**
 * Neo-Brutalist Card with crisp dark border, hard offset shadow, and tactile click depression.
 */
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = ProofWhite,
    borderColor: Color = ProofBorder,
    shadowColor: Color = ProofInk,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 10.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val translation by animateDpAsState(
        targetValue = if (isPressed && onClick != null) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardPressTranslation"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = shadowOffset, end = shadowOffset)
    ) {
        // Hard offset shadow behind
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor, shape)
        )
        // Foreground card with press elevation
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
            border = BorderStroke(2.dp, borderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = content
            )
        }
    }
}

/**
 * Neo-Brutalist Button with hard shadow and mechanical depression into the shadow on press.
 */
@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ProofCobalt,
    contentColor: Color = ProofWhite,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    shadowOffset: Dp = 3.dp,
    cornerRadius: Dp = 8.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val currentShadowOffset by animateDpAsState(
        targetValue = if (isPressed || !enabled) 0.dp else shadowOffset,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonOffset"
    )

    val translation by animateDpAsState(
        targetValue = if (isPressed && enabled) shadowOffset else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonTranslation"
    )

    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .padding(bottom = shadowOffset, end = shadowOffset)
    ) {
        // Shadow (only visible if enabled and not pressed)
        if (enabled && currentShadowOffset > 0.dp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(ProofInk, shape)
            )
        }

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
            border = BorderStroke(2.dp, if (enabled) ProofBorder else Color.Gray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    NeoSpinner(size = 18.dp, color = contentColor)
                    Spacer(modifier = Modifier.width(10.dp))
                } else if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) contentColor else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isLoading) "PROCESSING..." else text.uppercase(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    ),
                    color = if (enabled) contentColor else Color.Gray
                )
            }
        }
    }
}

/**
 * Neo-Brutalist Outlined / Secondary Button.
 */
@Composable
fun NeoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ProofWhite,
    contentColor: Color = ProofInk,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shadowOffset: Dp = 3.dp
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
 * Neo-Brutalist TextField with high contrast 2dp border.
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
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = ProofInk,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(text = placeholder, color = ProofTextMuted)
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
                focusedBorderColor = ProofInk,
                unfocusedBorderColor = ProofBorder,
                errorBorderColor = ProofCoral,
                focusedTextColor = ProofInk,
                unfocusedTextColor = ProofInk,
                cursorColor = ProofCobalt
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, if (isError) ProofCoral else ProofBorder, RoundedCornerShape(8.dp))
        )
        if (isError && errorMessage != null) {
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = ProofCoral,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofCoral
                )
            }
        }
    }
}

/**
 * Neo-Brutalist Tag Badge.
 */
@Composable
fun NeoTag(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ProofLavender,
    contentColor: Color = ProofInk,
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
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Status Badge for Lifecycle states with pop-in scale animation.
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (targetBg, targetFg, label) = when (status.uppercase()) {
        "OPEN" -> Triple(ProofAcidLime, ProofInk, "OPEN")
        "PENDING" -> Triple(ProofLavender, ProofInk, "PENDING")
        "ACCEPTED" -> Triple(ProofCobalt, ProofWhite, "ACCEPTED")
        "DECLINED" -> Triple(ProofCoral, ProofWhite, "DECLINED")
        "COMPLETED" -> Triple(ProofAcidLime, ProofInk, "ACTION COMPLETED")
        "CONFIRMED" -> Triple(ProofInk, ProofAcidLime, "CONFIRMED OUTCOME")
        else -> Triple(ProofSurfaceMuted, ProofInk, status.uppercase())
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
 * The 5-stage Workflow Hero Graphic with animated color and bounce transitions:
 * REQUEST → MATCH → CONNECT → ACT → CONFIRM
 */
@Composable
fun WorkflowHero(
    currentStep: Int, // 1 to 5
    modifier: Modifier = Modifier
) {
    val steps = listOf("REQUEST", "MATCH", "CONNECT", "ACT", "CONFIRM")

    NeoCard(
        modifier = modifier,
        backgroundColor = ProofPaper,
        shadowOffset = 2.dp,
        cornerRadius = 8.dp
    ) {
        Text(
            text = "PROOFLOOP WORKFLOW",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = ProofTextMuted
        )
        Spacer(modifier = Modifier.height(10.dp))
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
                    isCurrent -> Pair(ProofCobalt, ProofWhite)
                    isPassed -> Pair(ProofAcidLime, ProofInk)
                    else -> Pair(ProofWhite, ProofInk)
                }

                val animatedBg by animateColorAsState(targetValue = targetBg, animationSpec = tween(350), label = "stepBg")
                val animatedFg by animateColorAsState(targetValue = targetFg, animationSpec = tween(350), label = "stepFg")
                val stepScale by animateFloatAsState(
                    targetValue = if (isCurrent) 1.05f else 1.0f,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "stepScale"
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = animatedBg,
                    border = BorderStroke(1.5.dp, ProofBorder),
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            scaleX = stepScale
                            scaleY = stepScale
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$stepNum",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            ),
                            color = animatedFg
                        )
                        Text(
                            text = stepName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                letterSpacing = 0.sp
                            ),
                            color = animatedFg,
                            maxLines = 1
                        )
                    }
                }

                if (index < steps.size - 1) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier
                            .size(10.dp)
                            .padding(horizontal = 1.dp),
                        tint = ProofInk
                    )
                }
            }
        }
    }
}

/**
 * Top App Bar with brand logo, role pill, and sign out button.
 */
@Composable
fun NeoTopAppBar(
    title: String = "PROOFLOOP",
    subtitle: String? = null,
    roleTag: String? = null,
    onSignOut: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = ProofPaper,
        border = BorderStroke(1.dp, ProofBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = com.proofloop.R.drawable.proofloop_logo),
                        contentDescription = "ProofLoop Logo",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = ProofInk
                    )
                    if (roleTag != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        NeoTag(
                            text = roleTag.uppercase(),
                            backgroundColor = if (roleTag.equals("ALUMNI", ignoreCase = true)) ProofAcidLime else ProofLavender
                        )
                    }
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = ProofTextMuted
                    )
                }
            }

            if (onSignOut != null) {
                IconButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .size(36.dp)
                        .background(ProofWhite, RoundedCornerShape(6.dp))
                        .border(1.5.dp, ProofBorder, RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Sign Out",
                        tint = ProofInk,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Meaningful Empty State card (per PRD Appendix A).
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
        shadowOffset = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = ProofInk
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = ProofTextMuted,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(16.dp))
                NeoButton(
                    text = actionText,
                    onClick = onAction,
                    backgroundColor = ProofCobalt
                )
            }
        }
    }
}
