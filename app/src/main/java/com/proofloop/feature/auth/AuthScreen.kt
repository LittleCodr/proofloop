package com.proofloop.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proofloop.core.ui.components.*
import com.proofloop.core.ui.theme.*
import com.proofloop.data.model.UserRole

@Composable
fun AuthScreen(
    onAuthSuccess: (role: String) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("IIIT Delhi") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

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
            // Hero Title
            Text(
                text = "PROOFLOOP",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = ProofInk
            )
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

            // 5-Step Hero Workflow
            WorkflowHero(currentStep = 1)

            Spacer(modifier = Modifier.height(20.dp))

            // Mode Selector Tabs (Sign In / Register)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NeoButton(
                    text = "Sign In",
                    onClick = { isRegisterMode = false },
                    backgroundColor = if (!isRegisterMode) ProofCobalt else ProofWhite,
                    contentColor = if (!isRegisterMode) ProofWhite else ProofInk,
                    modifier = Modifier.weight(1f)
                )
                NeoButton(
                    text = "Create Account",
                    onClick = { isRegisterMode = true },
                    backgroundColor = if (isRegisterMode) ProofCobalt else ProofWhite,
                    contentColor = if (isRegisterMode) ProofWhite else ProofInk,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Auth Form Card
            NeoCard(
                backgroundColor = ProofWhite,
                shadowOffset = 4.dp
            ) {
                if (isRegisterMode) {
                    Text(
                        text = "I AM A:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    NeoTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        placeholder = "e.g. Aarav Sharma",
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
                    placeholder = "name@domain.edu",
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
                    text = if (isRegisterMode) "Create Account" else "Sign In",
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

            // Demo Quick-Start for Judges & Offline Hackathon Evaluation
            NeoCard(
                backgroundColor = ProofPaper,
                shadowOffset = 3.dp,
                borderColor = ProofInk
            ) {
                Text(
                    text = "HACKSHIFT DEMO QUICK-START",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black
                    ),
                    color = ProofInk
                )
                Text(
                    text = "1-tap evaluation with seeded student or alumni profiles:",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProofTextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NeoButton(
                        text = "Student Demo",
                        onClick = { viewModel.loginDemo(UserRole.STUDENT) },
                        backgroundColor = ProofLavender,
                        contentColor = ProofInk,
                        modifier = Modifier.weight(1f)
                    )
                    NeoButton(
                        text = "Alumni Demo",
                        onClick = { viewModel.loginDemo(UserRole.ALUMNI) },
                        backgroundColor = ProofAcidLime,
                        contentColor = ProofInk,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
