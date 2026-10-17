package com.proofloop.feature.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proofloop.ProofLoopApp
import com.proofloop.data.model.HelpRequest
import com.proofloop.data.repository.IProofLoopRepository
import com.proofloop.domain.matching.MatchResult
import com.proofloop.domain.matching.MatchingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MatchResultsUiState(
    val request: HelpRequest? = null,
    val matches: List<MatchResult> = emptyList(),
    val isLoading: Boolean = true,
    val requestedAlumniIds: Set<String> = emptySet(),
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class MatchResultsViewModel(
    private val repository: IProofLoopRepository = ProofLoopApp.instance.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchResultsUiState())
    val uiState: StateFlow<MatchResultsUiState> = _uiState.asStateFlow()

    fun loadMatches(requestId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val request = repository.getHelpRequestById(requestId)
            val student = repository.getCurrentUser()

            if (request == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Request not found."
                )
                return@launch
            }

            // Observe discoverable alumni
            repository.getDiscoverableAlumni().collect { candidates ->
                val activeCountMap = mutableMapOf<String, Int>()
                candidates.forEach { alumni ->
                    activeCountMap[alumni.uid] = repository.getAlumniActiveConnectionsCount(alumni.uid)
                }

                val rankedMatches = MatchingEngine.findMatches(
                    request = request,
                    student = student,
                    candidates = candidates,
                    activeRequestsCountMap = activeCountMap
                )

                _uiState.value = _uiState.value.copy(
                    request = request,
                    matches = rankedMatches,
                    isLoading = false
                )
            }
        }
    }

    fun sendConnectionRequest(alumniId: String, alumniName: String) {
        val request = uiState.value.request ?: return
        val student = repository.getCurrentUser()
        val studentId = student?.uid ?: request.studentId
        val studentName = student?.name ?: request.studentName

        viewModelScope.launch {
            val result = repository.sendConnectionRequest(
                requestId = request.id,
                requestTitle = request.title,
                studentId = studentId,
                studentName = studentName,
                alumniId = alumniId,
                alumniName = alumniName
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    requestedAlumniIds = _uiState.value.requestedAlumniIds + alumniId,
                    successMessage = "Request sent to $alumniName! You'll see an update when they respond."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to send connection request"
                )
            }
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}
