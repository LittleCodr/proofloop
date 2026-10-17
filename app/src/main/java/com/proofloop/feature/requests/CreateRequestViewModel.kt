package com.proofloop.feature.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proofloop.ProofLoopApp
import com.proofloop.data.model.CoachSuggestion
import com.proofloop.data.model.HelpCategories
import com.proofloop.data.model.HelpRequest
import com.proofloop.data.model.MissionBlueprint
import com.proofloop.data.model.RequestStatus
import com.proofloop.data.repository.IProofLoopRepository
import com.proofloop.domain.blueprint.MissionBlueprintRegistry
import com.proofloop.domain.coach.MissionCoach
import com.proofloop.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateRequestUiState(
    val title: String = "Review Android Compose Architecture",
    val description: String = "Need focused guidance on MVVM unidirectional data flow and repository caching patterns in Jetpack Compose.",
    val category: String = HelpCategories.CODE_REVIEW,
    val requiredSkills: List<String> = listOf("Kotlin", "Jetpack Compose", "Android Architecture"),
    val desiredOutcome: String = "Actionable list of architecture fixes and agreed next action.",
    val durationMinutes: Int = 30,
    val preferredAvailability: String = "Weekends 2-4 PM",
    val selectedBlueprint: MissionBlueprint? = null,
    val availableBlueprints: List<MissionBlueprint> = emptyList(),
    val creatorRole: String = UserRole.STUDENT.name,
    val coachSuggestion: CoachSuggestion? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val createdRequestId: String? = null
)

class CreateRequestViewModel(
    private val repository: IProofLoopRepository = ProofLoopApp.instance.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CreateRequestUiState(
            availableBlueprints = repository.getAvailableBlueprints(),
            creatorRole = repository.getCurrentUser()?.role ?: UserRole.STUDENT.name
        )
    )
    val uiState: StateFlow<CreateRequestUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) { _uiState.value = _uiState.value.copy(title = title, errorMessage = null) }
    fun onDescriptionChange(desc: String) { _uiState.value = _uiState.value.copy(description = desc, errorMessage = null) }
    fun onCategoryChange(cat: String) { _uiState.value = _uiState.value.copy(category = cat) }
    fun onDesiredOutcomeChange(outcome: String) { _uiState.value = _uiState.value.copy(desiredOutcome = outcome, errorMessage = null) }
    fun onDurationChange(duration: Int) { _uiState.value = _uiState.value.copy(durationMinutes = duration) }
    fun onAvailabilityChange(avail: String) { _uiState.value = _uiState.value.copy(preferredAvailability = avail) }

    fun selectBlueprint(blueprint: MissionBlueprint) {
        val currentUser = repository.getCurrentUser()
        val prefilled = MissionBlueprintRegistry.createPrefilledRequest(
            blueprint = blueprint,
            studentId = currentUser?.uid.orEmpty(),
            studentName = currentUser?.name.orEmpty()
        )
        _uiState.value = _uiState.value.copy(
            selectedBlueprint = blueprint,
            title = prefilled.title,
            description = prefilled.description,
            category = prefilled.category,
            requiredSkills = prefilled.requiredSkills,
            desiredOutcome = prefilled.desiredOutcome,
            durationMinutes = prefilled.durationMinutes,
            preferredAvailability = prefilled.preferredAvailability,
            errorMessage = null,
            coachSuggestion = null
        )
    }

    fun clearBlueprint() {
        _uiState.value = _uiState.value.copy(selectedBlueprint = null)
    }

    fun requestMissionCoaching() {
        val current = _uiState.value
        val suggestion = MissionCoach.generateCoachingSuggestion(
            rawTitle = current.title,
            rawDescription = current.description,
            selectedCategory = current.category
        )
        _uiState.value = _uiState.value.copy(coachSuggestion = suggestion)
    }

    fun dismissCoachSuggestion() {
        _uiState.value = _uiState.value.copy(coachSuggestion = null)
    }

    fun applyCoachSuggestion(suggestion: CoachSuggestion) {
        _uiState.value = _uiState.value.copy(
            title = suggestion.refinedTitle,
            description = suggestion.refinedProblemStatement,
            category = suggestion.suggestedCategory,
            requiredSkills = suggestion.suggestedSkills,
            desiredOutcome = suggestion.suggestedOutcome,
            durationMinutes = suggestion.suggestedDurationMinutes,
            coachSuggestion = null,
            errorMessage = null
        )
    }

    fun addSkill(skill: String) {
        val trimmed = skill.trim()
        if (trimmed.isNotEmpty() && !uiState.value.requiredSkills.contains(trimmed)) {
            _uiState.value = _uiState.value.copy(requiredSkills = uiState.value.requiredSkills + trimmed)
        }
    }

    fun removeSkill(skill: String) {
        _uiState.value = _uiState.value.copy(requiredSkills = uiState.value.requiredSkills - skill)
    }

    fun submitRequest() {
        val state = uiState.value
        if (state.title.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a concise title for your request")
            return
        }
        if (state.description.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please describe the specific problem you need guidance on")
            return
        }
        if (state.desiredOutcome.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please specify the desired outcome you want to achieve")
            return
        }
        if (state.requiredSkills.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please specify at least one required skill")
            return
        }

        val currentUser = repository.getCurrentUser()
        val studentId = currentUser?.uid ?: "student_guest"
        val studentName = currentUser?.name ?: "Student"

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val request = HelpRequest(
                studentId = studentId,
                studentName = studentName,
                title = state.title.trim(),
                description = state.description.trim(),
                category = state.category,
                requiredSkills = state.requiredSkills,
                desiredOutcome = state.desiredOutcome.trim(),
                durationMinutes = state.durationMinutes,
                preferredAvailability = state.preferredAvailability,
                status = RequestStatus.OPEN.name,
                creatorRole = state.creatorRole,
                blueprintId = state.selectedBlueprint?.id ?: "",
                blueprintVersion = state.selectedBlueprint?.version ?: 1,
                createdAt = System.currentTimeMillis()
            )

            val result = repository.createHelpRequest(request)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, createdRequestId = result.getOrNull())
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to submit request"
                )
            }
        }
    }
}
