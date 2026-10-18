package com.proofloop.feature.requests

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Search
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
import com.proofloop.data.model.UserRole

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
    val blueprintScrollState = rememberScrollState()

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
                color = ProofPaperLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, ProofRecessedMuted)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ProofObsidianInk,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEW LOOP MISSION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = ProofObsidianInk
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Workflow indicator (Step 1 -> Step 2)
                WorkflowHero(currentStep = 1)

                Spacer(modifier = Modifier.height(8.dp))

                // MISSION BLUEPRINTS PICKER (Reusable Mentorship Templates)
                NeoCard(
                    backgroundColor = ProofPureWhite,
                    contentPadding = 8.dp,
                    shadowOffset = 2.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = ProofKleinBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MISSION BLUEPRINTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = ProofKleinBlue
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "PRE-SCOPED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ProofCharcoalMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(blueprintScrollState),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.availableBlueprints.forEach { bp ->
                            val isSelected = uiState.selectedBlueprint?.id == bp.id
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) ProofDigitalLilac else ProofPaperLight,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (isSelected) ProofObsidianInk else ProofRecessedMuted
                                ),
                                modifier = Modifier
                                    .width(150.dp)
                                    .clickable { viewModel.selectBlueprint(bp) }
                            ) {
                                Column(modifier = Modifier.padding(7.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${bp.suggestedDurationMinutes}m",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            color = ProofObsidianInk
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = ProofObsidianInk,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = bp.title,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = ProofObsidianInk,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = bp.category,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = ProofCharcoalMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.selectedBlueprint != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ProofSignalEmerald.copy(alpha = 0.25f), RoundedCornerShape(5.dp))
                                .border(1.dp, ProofObsidianInk, RoundedCornerShape(5.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Active: ${uiState.selectedBlueprint?.title}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                color = ProofObsidianInk
                            )
                            Text(
                                text = "Clear",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = ProofSolarTangerine
                                ),
                                modifier = Modifier.clickable { viewModel.clearBlueprint() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // MISSION COACH BUTTON & SUGGESTION CARD
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    color = ProofPlumSurface,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ProofObsidianInk)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ProofSignalEmerald,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PROOFLOOP COACH",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = ProofSignalEmerald
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ProofVoltCitron,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofObsidianInk),
                                modifier = Modifier.clickable { viewModel.requestMissionCoaching() }
                            ) {
                                Text(
                                    text = "REFINE DRAFT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp
                                    ),
                                    color = ProofObsidianInk,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // If coach suggestion is available
                        AnimatedVisibility(
                            visible = uiState.coachSuggestion != null,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            val suggestion = uiState.coachSuggestion
                            if (suggestion != null) {
                                Column(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .background(ProofPureWhite, RoundedCornerShape(6.dp))
                                        .border(1.dp, ProofObsidianInk, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                    ) {
                                    Text(
                                        text = "COACH PROPOSAL:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = ProofKleinBlue
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = suggestion.refinedTitle,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                        color = ProofObsidianInk
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = suggestion.refinedProblemStatement,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = ProofCharcoalMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Deliverable: ${suggestion.suggestedOutcome}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        color = ProofObsidianInk
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = { viewModel.dismissCoachSuggestion() },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Dismiss", color = ProofCharcoalMuted, fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Button(
                                            onClick = { viewModel.applyCoachSuggestion(suggestion) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ProofSignalEmerald,
                                                contentColor = ProofObsidianInk
                                            ),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                                            shape = RoundedCornerShape(5.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, ProofObsidianInk)
                                        ) {
                                            Text("Apply Proposal", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                NeoCard(
                    backgroundColor = ProofPureWhite,
                    contentPadding = 10.dp,
                    shadowOffset = 2.5.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LOOP MISSION SCOPE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = ProofKleinBlue
                        )
                        Text(
                            text = "HIGH-SIGNAL INPUTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ProofCharcoalMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Title
                    NeoTextField(
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChange,
                        label = "Concise Request Title",
                        placeholder = "e.g. Review Jetpack Compose architecture for app"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    NeoTextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = "Problem Description",
                        placeholder = "Describe the exact technical challenge or context you are blocked on...",
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Selector
                    Text(
                        text = "Help Category",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        color = ProofObsidianInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        HelpCategories.ALL.forEach { cat ->
                            val isSelected = uiState.category == cat
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = if (isSelected) ProofDigitalLilac else ProofPaperLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofObsidianInk),
                                modifier = Modifier.clickable { viewModel.onCategoryChange(cat) }
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = ProofObsidianInk,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Required Skills Chips
                    Text(
                        text = "Required Skills",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        color = ProofObsidianInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Selected skills
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        uiState.requiredSkills.forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = ProofSignalEmerald,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofObsidianInk)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = skill,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                        color = ProofObsidianInk
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = ProofObsidianInk,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable { viewModel.removeSkill(skill) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Add skill input & suggestions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newSkillInput,
                            onValueChange = { newSkillInput = it },
                            placeholder = { Text("Add skill...", color = ProofCharcoalMuted, fontSize = 11.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ProofPureWhite,
                                unfocusedContainerColor = ProofPureWhite,
                                focusedBorderColor = ProofObsidianInk,
                                unfocusedBorderColor = ProofRecessedMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (newSkillInput.isNotBlank()) {
                                    viewModel.addSkill(newSkillInput)
                                    newSkillInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(ProofVoltCitron, RoundedCornerShape(6.dp))
                                .border(1.5.dp, ProofObsidianInk, RoundedCornerShape(6.dp))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = ProofObsidianInk, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        quickSkillSuggestions.filter { !uiState.requiredSkills.contains(it) }.take(4).forEach { suggestion ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ProofPaperLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ProofRecessedMuted),
                                modifier = Modifier.clickable { viewModel.addSkill(suggestion) }
                            ) {
                                Text(
                                    text = "+ $suggestion",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = ProofObsidianInk,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Desired Outcome
                    NeoTextField(
                        value = uiState.desiredOutcome,
                        onValueChange = viewModel::onDesiredOutcomeChange,
                        label = "Desired Deliverable (What will follow?)",
                        placeholder = "e.g. Actionable PR feedback and agreed architecture refactor",
                        singleLine = false,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Expected Duration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Duration",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = ProofObsidianInk
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            durationOptions.forEach { mins ->
                                val isSelected = uiState.durationMinutes == mins
                                Surface(
                                    shape = RoundedCornerShape(5.dp),
                                    color = if (isSelected) ProofDigitalLilac else ProofPaperLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ProofObsidianInk),
                                    modifier = Modifier.clickable { viewModel.onDurationChange(mins) }
                                ) {
                                    Text(
                                        text = "${mins}m",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = ProofObsidianInk,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preferred Availability
                    Text(
                        text = "Preferred Availability",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        color = ProofObsidianInk
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        availabilityOptions.forEach { slot ->
                            val isSelected = uiState.preferredAvailability == slot
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = if (isSelected) ProofDigitalLilac else ProofPaperLight,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ProofObsidianInk else ProofRecessedMuted
                                ),
                                modifier = Modifier.clickable { viewModel.onAvailabilityChange(slot) }
                            ) {
                                Text(
                                    text = slot,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.sp
                                    ),
                                    color = ProofObsidianInk,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                            Spacer(modifier = Modifier.height(8.dp))
                            NeoTag(
                                text = uiState.errorMessage ?: "",
                                backgroundColor = ProofSolarTangerine,
                                contentColor = ProofPureWhite,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Submit CTA
                    val ctaText = when (uiState.creatorRole) {
                        UserRole.FOUNDER.name -> "Find Matching Collaborators"
                        UserRole.FACULTY.name -> "Find Matching Researchers"
                        UserRole.RECRUITER.name -> "Find Matching Candidates"
                        else -> "Find Matching Mentors"
                    }
                    NeoButton(
                        text = ctaText,
                        onClick = viewModel::submitRequest,
                        backgroundColor = ProofVoltCitron,
                        contentColor = ProofObsidianInk,
                        icon = Icons.Default.Search,
                        enabled = !uiState.isLoading,
                        isLoading = uiState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
