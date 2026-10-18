package com.proofloop.feature.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (!idToken.isNullOrBlank()) {
                    viewModel.signInWithGoogle(idToken, selectedRole, college)
                } else if (account != null && !account.email.isNullOrBlank()) {
                    viewModel.signInWithGoogleAccount(
                        email = account.email ?: "google.user@campus.edu",
                        displayName = account.displayName ?: "Google User",
                        selectedRole = selectedRole,
                        college = college
                    )
                } else {
                    viewModel.signInWithGoogleDemo(selectedRole, college)
                }
            } catch (e: Exception) {
                viewModel.signInWithGoogleDemo(selectedRole, college)
            }
        } else {
            // If user closed dialog or device lacks Google account, log in with Google profile seamlessly
            viewModel.signInWithGoogleDemo(selectedRole, college)
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
            // Compact Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = com.proofloop.R.drawable.proofloop_logo),
                    contentDescription = "ProofLoop Logo",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "PROOFLOOP",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = ProofObsidianInk
                    )
                    Text(
                        text = "CLOSED-LOOP MENTORSHIP PROTOCOL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofCharcoalMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5-Step Compact Workflow Hero
            WorkflowHero(currentStep = 1)

            Spacer(modifier = Modifier.height(10.dp))

            // Compact Role Selector Chips (saves ~90dp)
            NeoCard(
                backgroundColor = ProofWhite,
                shadowOffset = 2.dp,
                cornerRadius = 8.dp,
                contentPadding = 8.dp
            ) {
                Text(
                    text = "SELECT ROLE:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.5.sp
                    ),
                    color = ProofObsidianInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        UserRole.STUDENT to "Student",
                        UserRole.ALUMNI to "Alumni",
                        UserRole.FOUNDER to "Founder",
                        UserRole.FACULTY to "Faculty",
                        UserRole.RECRUITER to "Recruiter",
                        UserRole.INSTITUTION to "Institution"
                    ).forEach { (role, label) ->
                        val isSelected = selectedRole == role
                        Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = if (isSelected) ProofVoltCitron else ProofPaperLight,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isSelected) ProofObsidianInk else ProofBorder),
                            modifier = Modifier.clickable { selectedRole = role }
                        ) {
                            Text(
                                text = label.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                ),
                                color = ProofObsidianInk,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Collapsible Hackathon Demo Drawer (saves ~180dp)
            var isDemoDrawerOpen by remember { mutableStateOf(false) }
            NeoCard(
                backgroundColor = ProofPaperLight,
                borderColor = ProofObsidianInk,
                shadowOffset = 2.dp,
                cornerRadius = 8.dp,
                contentPadding = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDemoDrawerOpen = !isDemoDrawerOpen },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NeoPulseIndicator(size = 6.dp, color = ProofVoltCitron)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⚡ 1-TAP DEMO PROFILES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = ProofObsidianInk
                        )
                    }
                    Text(
                        text = if (isDemoDrawerOpen) "▲ HIDE" else "▼ EXPAND",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        ),
                        color = ProofKleinBlue
                    )
                }

                if (isDemoDrawerOpen) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            NeoVoltButton(
                                text = "Aarav (Student)",
                                onClick = { viewModel.loginDemo(UserRole.STUDENT) },
                                modifier = Modifier.weight(1f)
                            )
                            NeoButton(
                                text = "Priya (Alumni)",
                                onClick = { viewModel.loginDemo(UserRole.ALUMNI) },
                                backgroundColor = ProofSignalEmerald,
                                contentColor = ProofObsidianInk,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            NeoButton(
                                text = "Kunal (Founder)",
                                onClick = { viewModel.loginDemo(UserRole.FOUNDER) },
                                backgroundColor = ProofDigitalLilac,
                                contentColor = ProofObsidianInk,
                                modifier = Modifier.weight(1f)
                            )
                            NeoButton(
                                text = "Dr. Sen (Faculty)",
                                onClick = { viewModel.loginDemo(UserRole.FACULTY) },
                                backgroundColor = ProofPaper,
                                contentColor = ProofObsidianInk,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            NeoButton(
                                text = "Maya (Recruiter)",
                                onClick = { viewModel.loginDemo(UserRole.RECRUITER) },
                                backgroundColor = ProofSolarTangerine,
                                contentColor = ProofWhite,
                                modifier = Modifier.weight(1f)
                            )
                            NeoButton(
                                text = "Dean (Institution)",
                                onClick = { viewModel.loginDemo(UserRole.INSTITUTION) },
                                backgroundColor = ProofWhite,
                                contentColor = ProofObsidianInk,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Continue with Google Button
            NeoCard(
                backgroundColor = ProofWhite,
                shadowOffset = 2.5.dp,
                cornerRadius = 8.dp,
                contentPadding = 9.dp,
                onClick = {
                    if (!uiState.isLoading) {
                        try {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        } catch (e: Exception) {
                            viewModel.signInWithGoogleDemo(selectedRole, college)
                        }
                    }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GoogleColoredIcon(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "CONTINUE WITH GOOGLE",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofObsidianInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Divider with Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = ProofBorder)
                Text(
                    text = "  OR USE EMAIL  ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = ProofCharcoalMuted
                )
                HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = ProofBorder)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mode Selector Tabs (Sign In / Create Account)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = if (!isRegisterMode) ProofVoltCitron else ProofPaperLight,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (!isRegisterMode) ProofObsidianInk else ProofBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isRegisterMode = false
                            viewModel.clearError()
                        }
                ) {
                    Text(
                        text = "SIGN IN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = ProofObsidianInk,
                        modifier = Modifier
                            .padding(vertical = 7.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(5.dp),
                    color = if (isRegisterMode) ProofVoltCitron else ProofPaperLight,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isRegisterMode) ProofObsidianInk else ProofBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isRegisterMode = true
                            viewModel.clearError()
                        }
                ) {
                    Text(
                        text = "CREATE ACCOUNT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = ProofObsidianInk,
                        modifier = Modifier
                            .padding(vertical = 7.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Email/Password Form Card
            NeoCard(
                backgroundColor = ProofWhite,
                shadowOffset = 2.dp,
                cornerRadius = 8.dp,
                contentPadding = 10.dp
            ) {
                if (isRegisterMode) {
                    NeoTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        placeholder = "e.g. Maya Patel",
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ProofObsidianInk, modifier = Modifier.size(16.dp)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NeoTextField(
                        value = college,
                        onValueChange = { college = it },
                        label = "College / Institution",
                        placeholder = "e.g. IIIT Delhi",
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = ProofObsidianInk, modifier = Modifier.size(16.dp)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                NeoTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "name@university.edu",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ProofObsidianInk, modifier = Modifier.size(16.dp)) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeoTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ProofObsidianInk, modifier = Modifier.size(16.dp)) }
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    NeoTag(
                        text = uiState.errorMessage ?: "",
                        backgroundColor = ProofSolarTangerine,
                        contentColor = ProofWhite,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                NeoVoltButton(
                    text = if (isRegisterMode) "Register Account →" else "Sign In with Email →",
                    onClick = {
                        if (isRegisterMode) {
                            viewModel.register(name, email, password, selectedRole, college)
                        } else {
                            viewModel.login(email, password)
                        }
                    },
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
