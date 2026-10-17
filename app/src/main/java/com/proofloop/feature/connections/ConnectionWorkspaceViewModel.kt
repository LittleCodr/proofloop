package com.proofloop.feature.connections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proofloop.ProofLoopApp
import com.proofloop.data.model.Connection
import com.proofloop.data.model.Outcome
import com.proofloop.data.model.UserAccount
import com.proofloop.data.repository.IProofLoopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConnectionWorkspaceUiState(
    val currentUser: UserAccount? = null,
    val connection: Connection? = null,
    val outcome: Outcome? = null,
    val actionText: String = "",
    val isLoading: Boolean = true,
    val feedbackMessage: String? = null,
    val errorMessage: String? = null
)

class ConnectionWorkspaceViewModel(
    private val repository: IProofLoopRepository = ProofLoopApp.instance.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionWorkspaceUiState())
    val uiState: StateFlow<ConnectionWorkspaceUiState> = _uiState.asStateFlow()

    fun loadConnection(connectionId: String) {
        val user = repository.getCurrentUser()
        _uiState.value = _uiState.value.copy(currentUser = user, isLoading = true)

        viewModelScope.launch {
            repository.getConnectionById(connectionId).collect { conn ->
                if (conn != null) {
                    _uiState.value = _uiState.value.copy(
                        connection = conn,
                        actionText = conn.agreedNextAction.ifBlank { _uiState.value.actionText },
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getOutcomeForConnection(connectionId).collect { out ->
                _uiState.value = _uiState.value.copy(outcome = out)
            }
        }
    }

    fun onActionTextChange(text: String) {
        _uiState.value = _uiState.value.copy(actionText = text)
    }

    fun saveAgreedAction() {
        val conn = uiState.value.connection ?: return
        val text = uiState.value.actionText.trim()
        if (text.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Agreed action cannot be empty")
            return
        }

        viewModelScope.launch {
            val result = repository.updateAgreedAction(conn.id, text)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(feedbackMessage = "Agreed next action saved successfully!")
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to save action"
                )
            }
        }
    }

    fun studentMarkComplete() {
        val conn = uiState.value.connection ?: return
        val user = repository.getCurrentUser() ?: return

        // Security check
        if (user.uid != conn.studentId) {
            _uiState.value = _uiState.value.copy(errorMessage = "Unauthorized: Only the student can mark action complete.")
            return
        }

        viewModelScope.launch {
            val result = repository.studentMarkComplete(conn.id, user.uid)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    feedbackMessage = "Action marked complete! Alumnus can now confirm the outcome."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to update completion"
                )
            }
        }
    }

    fun alumniConfirmOutcome() {
        val conn = uiState.value.connection ?: return
        val user = repository.getCurrentUser() ?: return

        // Security check
        if (user.uid != conn.alumniId) {
            _uiState.value = _uiState.value.copy(errorMessage = "Unauthorized: Only the alumnus can confirm the outcome.")
            return
        }

        viewModelScope.launch {
            val result = repository.alumniConfirmOutcome(conn.id, user.uid)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    feedbackMessage = "Interaction outcome independently confirmed! Full loop closed."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to confirm outcome"
                )
            }
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null, errorMessage = null)
    }
}
