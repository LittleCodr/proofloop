package com.proofloop.feature.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.proofloop.core.ui.components.*
import com.proofloop.core.ui.theme.*
import com.proofloop.data.model.UserRole

// Web client ID from google-services.json (client_type: 3)
private const val GOOGLE_WEB_CLIENT_ID = "126227877096-3f86uaian7kg6mgj96r935d1rj71s065.apps.googleusercontent.com"

@Composable
fun AuthScreen(
    onAuthSuccess: (role: String) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("IIIT Delhi") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

    // Google Sign-In Client & Activity Result Launcher
    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GOOGLE_WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (!idToken.isNullOrBlank()) {
                    viewModel.signInWithGoogle(idToken, selectedRole, college)
                } else {
                    viewModel.setError("Google authentication returned empty token. Please try again.")
                }
            } catch (e: ApiException) {
                viewModel.setError("Google Sign-In failed (${e.statusCode}): ${e.message}")
            } catch (e: Exception) {
                viewModel.setError("Google Sign-In error: ${e.localizedMessage}")
            }
        } else {
            // User cancelled or back pressed
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.user) {
        val user = uiState.user
        if (user != null) {
            onAuthSuccess(user.role)
        }
    }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ProofPaper
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Brand Header with Official Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = com.proofloop.R.drawable.proofloop_logo),
                    contentDescription = "ProofLoop Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "PROOFLOOP",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    ),
                    color = ProofInk
                )
            }
            Text(
                text = "CONNECTIONS SHOULD LEAD SOMEWHERE.",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = ProofTextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5-Step Workflow Hero
            WorkflowHero(currentStep = 1)

            Spacer(modifier = Modifier.height(20.dp))

            // Role Selector Card
            NeoCard(
                backgroundColor = ProofWhite,
                shadowOffset = 3.dp
            ) {
                Text(
                    text = "SELECT YOUR ROLE:",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black
                    ),
                    color = ProofInk
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeoButton(
                        text = "Student",
                        onClick = { selectedRole = UserRole.STUDENT },
                        backgroundColor = if (selectedRole == UserRole.STUDENT) ProofAcidLime else ProofSurfaceMuted,
                        contentColor = ProofInk,
                        modifier = Modifier.weight(1f)
                    )
                    NeoButton(
                        text = "Alumnus / Alumna",
                        onClick = { selectedRole = UserRole.ALUMNI },
                        backgroundColor = if (selectedRole == UserRole.ALUMNI) ProofAcidLime else ProofSurfaceMuted,
                        contentColor = ProofInk,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue with Google Button
            NeoCard(
                backgroundColor = ProofWhite,
                shadowOffset = 4.dp,
                onClick = {
                    if (!uiState.isLoading) {
                        // Clear previous sign in to ensure account picker dialog displays
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GoogleColoredIcon(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "CONTINUE WITH GOOGLE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider with Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 2.dp, color = ProofBorder)
                Text(
                    text = "  OR USE EMAIL  ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ProofTextMuted
                )
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 2.dp, color = ProofBorder)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Selector Tabs (Sign In / Create Account)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeoButton(
                    text = "Sign In",
                    onClick = {
                        isRegisterMode = false
                        viewModel.clearError()
                    },
                    backgroundColor = if (!isRegisterMode) ProofCobalt else ProofWhite,
                    contentColor = if (!isRegisterMode) ProofWhite else ProofInk,
                    modifier = Modifier.weight(1f)
                )
                NeoButton(
                    text = "Create Account",
                    onClick = {
                        isRegisterMode = true
                        viewModel.clearError()
                    },
                    backgroundColor = if (isRegisterMode) ProofCobalt else ProofWhite,
                    contentColor = if (isRegisterMode) ProofWhite else ProofInk,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Email/Password Form Card
            NeoCard(
                backgroundColor = ProofWhite,
                shadowOffset = 4.dp
            ) {
                if (isRegisterMode) {
                    NeoTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        placeholder = "e.g. Maya Patel",
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ProofInk) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NeoTextField(
                        value = college,
                        onValueChange = { college = it },
                        label = "College / Institution",
                        placeholder = "e.g. IIIT Delhi",
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = ProofInk) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                NeoTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "name@university.edu",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ProofInk) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                NeoTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ProofInk) }
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    NeoTag(
                        text = uiState.errorMessage ?: "",
                        backgroundColor = ProofCoral,
                        contentColor = ProofWhite,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                NeoButton(
                    text = if (isRegisterMode) "Register Account" else "Sign In with Email",
                    onClick = {
                        if (isRegisterMode) {
                            viewModel.register(name, email, password, selectedRole, college)
                        } else {
                            viewModel.login(email, password)
                        }
                    },
                    backgroundColor = ProofCobalt,
                    contentColor = ProofWhite,
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * High-fidelity Google "G" 4-color Vector Icon rendered on Canvas.
 */
@Composable
fun GoogleColoredIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = width.coerceAtMost(height) / 2f

        // Blue right segment
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = true
        )
        // Green bottom segment
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 45f,
            sweepAngle = 90f,
            useCenter = true
        )
        // Yellow bottom-left segment
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 135f,
            sweepAngle = 90f,
            useCenter = true
        )
        // Red top segment
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 225f,
            sweepAngle = 90f,
            useCenter = true
        )

        // Center cutout
        drawCircle(
            color = Color.White,
            radius = radius * 0.58f,
            center = center
        )

        // Blue crossbar
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = androidx.compose.ui.geometry.Offset(centerX, centerY - radius * 0.2f),
            size = androidx.compose.ui.geometry.Size(radius, radius * 0.4f)
        )
    }
}
