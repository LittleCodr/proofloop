package com.proofloop.feature.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.animation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proofloop.core.ui.components.*
import com.proofloop.core.ui.theme.*
import com.proofloop.data.model.HelpCategories

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateRequestScreen(
    onNavigateBack: () -> Unit,
    onMatchesFound: (requestId: String) -> Unit,
    viewModel: CreateRequestViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var newSkillInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val quickSkillSuggestions = listOf(
        "Kotlin", "Jetpack Compose", "Coroutines", "System Design",
        "Android Architecture", "App Performance", "Firebase", "Offline Sync"
    )

    val durationOptions = listOf(15, 30, 45, 60)
    val availabilityOptions = listOf(
        "Weekends 2-4 PM",
        "Weekday Evenings (7-9 PM)",
        "Saturday Morning (10-12 AM)",
        "Sunday 4-6 PM"
    )

    LaunchedEffect(uiState.createdRequestId) {
        val id = uiState.createdRequestId
        if (id != null) {
            onMatchesFound(id)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ProofPaper
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ProofPaper,
                border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ProofInk
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NEW HELP REQUEST",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        ),
                        color = ProofInk
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Workflow indicator (Step 1 -> Step 2)
                WorkflowHero(currentStep = 1)

                Spacer(modifier = Modifier.height(16.dp))

                NeoCard(
                    backgroundColor = ProofWhite,
                    shadowOffset = 4.dp
                ) {
                    Text(
                        text = "BE SPECIFIC & TIME-BOUNDED",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = ProofCobalt
                    )
                    Text(
                        text = "Alumni are more likely to accept requests that have a defined scope and clear outcome.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ProofTextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    NeoTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChange,
                        label = "Concise Request Title",
                        placeholder = "e.g. Review Jetpack Compose architecture for app"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description
                    NeoTextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = "Problem Description",
                        placeholder = "Describe the exact technical challenge or context you are blocked on...",
                        singleLine = false,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Selector
                    Text(
                        text = "Help Category",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ProofInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HelpCategories.ALL.forEach { cat ->
                            val isSelected = uiState.category == cat
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) ProofCobalt else ProofWhite,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, ProofBorder),
                                modifier = Modifier.clickable { viewModel.onCategoryChange(cat) }
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) ProofWhite else ProofInk,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Required Skills Chips
                    Text(
                        text = "Required Skills",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ProofInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Selected skills
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.requiredSkills.forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ProofAcidLime,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, ProofBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = skill,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ProofInk
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = ProofInk,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { viewModel.removeSkill(skill) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Add skill input & suggestions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newSkillInput,
                            onValueChange = { newSkillInput = it },
                            placeholder = { Text("Add another skill...", color = ProofTextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ProofWhite,
                                unfocusedContainerColor = ProofWhite,
                                focusedBorderColor = ProofInk,
                                unfocusedBorderColor = ProofBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (newSkillInput.isNotBlank()) {
                                    viewModel.addSkill(newSkillInput)
                                    newSkillInput = ""
                                }
                            },
                            modifier = Modifier
                                .background(ProofCobalt, RoundedCornerShape(8.dp))
                                .border(1.5.dp, ProofBorder, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = ProofWhite)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Quick suggestions:",
                        style = MaterialTheme.typography.labelSmall,
                        color = ProofTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        quickSkillSuggestions.filter { !uiState.requiredSkills.contains(it) }.take(5).forEach { suggestion ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ProofSurfaceMuted,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofBorder),
                                modifier = Modifier.clickable { viewModel.addSkill(suggestion) }
                            ) {
                                Text(
                                    text = "+ $suggestion",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ProofInk,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Desired Outcome
                    NeoTextField(
                        value = uiState.desiredOutcome,
                        onValueChange = viewModel::onDesiredOutcomeChange,
                        label = "Desired Outcome (What will follow?)",
                        placeholder = "e.g. Actionable list of architecture fixes and agreed next action",
                        singleLine = false,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expected Duration
                    Text(
                        text = "Expected Session Duration",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ProofInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        durationOptions.forEach { mins ->
                            val isSelected = uiState.durationMinutes == mins
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) ProofCobalt else ProofWhite,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, ProofBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.onDurationChange(mins) }
                            ) {
                                Text(
                                    text = "${mins}m",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isSelected) ProofWhite else ProofInk,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preferred Availability
                    Text(
                        text = "Preferred Availability",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ProofInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        availabilityOptions.forEach { slot ->
                            val isSelected = uiState.preferredAvailability == slot
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) ProofLavender else ProofWhite,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.5.dp,
                                    ProofBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onAvailabilityChange(slot) }
                            ) {
                                Text(
                                    text = slot,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = ProofInk,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Error Message
                    AnimatedVisibility(
                        visible = uiState.errorMessage != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))
                            NeoTag(
                                text = uiState.errorMessage ?: "",
                                backgroundColor = ProofCoral,
                                contentColor = ProofWhite,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit CTA
                    NeoButton(
                        text = "Find Matching Alumni",
                        onClick = viewModel::submitRequest,
                        backgroundColor = ProofCobalt,
                        contentColor = ProofWhite,
                        icon = Icons.Default.Search,
                        enabled = !uiState.isLoading,
                        isLoading = uiState.isLoading
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
