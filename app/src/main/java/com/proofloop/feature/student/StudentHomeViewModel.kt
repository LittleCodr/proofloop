package com.proofloop.feature.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proofloop.ProofLoopApp
import com.proofloop.data.model.Connection
import com.proofloop.data.model.ConnectionStatus
import com.proofloop.data.model.HelpRequest
import com.proofloop.data.model.Outcome
import com.proofloop.data.model.UserAccount
import com.proofloop.data.repository.IProofLoopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentHomeUiState(
    val currentUser: UserAccount? = null,
    val activeRequests: List<HelpRequest> = emptyList(),
    val pendingConnections: List<Connection> = emptyList(),
    val acceptedConnections: List<Connection> = emptyList(),
    val completedOutcomes: List<Outcome> = emptyList(),
    val isLoading: Boolean = false
)

class StudentHomeViewModel(
    private val repository: IProofLoopRepository = ProofLoopApp.instance.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentHomeUiState())
    val uiState: StateFlow<StudentHomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val user = repository.getCurrentUser()
        _uiState.value = _uiState.value.copy(currentUser = user)
        val studentId = user?.uid ?: return

        // 1. Observe student's help requests
        viewModelScope.launch {
            repository.getStudentHelpRequests(studentId).collect { requests ->
                _uiState.value = _uiState.value.copy(activeRequests = requests)
            }
        }

        // 2. Observe student's connections (pending and accepted)
        viewModelScope.launch {
            repository.getConnectionsForStudent(studentId).collect { connections ->
                val pending = connections.filter { it.status == ConnectionStatus.PENDING.name }
                val accepted = connections.filter { it.status == ConnectionStatus.ACCEPTED.name }
                _uiState.value = _uiState.value.copy(
                    pendingConnections = pending,
                    acceptedConnections = accepted
                )
            }
        }

        // 3. Observe completed outcomes
        viewModelScope.launch {
            repository.getCompletedOutcomesForStudent(studentId).collect { outcomes ->
                _uiState.value = _uiState.value.copy(completedOutcomes = outcomes)
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}
