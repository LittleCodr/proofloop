package com.proofloop.feature.alumni

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proofloop.ProofLoopApp
import com.proofloop.data.model.Connection
import com.proofloop.data.model.PublicProfile
import com.proofloop.data.model.UserAccount
import com.proofloop.data.model.UserRole
import com.proofloop.data.repository.IProofLoopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AlumniInboxUiState(
    val currentUser: UserAccount? = null,
    val profile: PublicProfile? = null,
    val incomingRequests: List<Connection> = emptyList(),
    val acceptedConnections: List<Connection> = emptyList(),
    val isLoading: Boolean = false,
    val feedbackMessage: String? = null,
    val errorMessage: String? = null
)

class AlumniInboxViewModel(
    private val repository: IProofLoopRepository = ProofLoopApp.instance.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlumniInboxUiState())
    val uiState: StateFlow<AlumniInboxUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val user = repository.getCurrentUser()
        _uiState.value = _uiState.value.copy(currentUser = user)
        val alumniId = user?.uid ?: return

        // 1. Observe current user updates
        viewModelScope.launch {
            repository.observeCurrentUser().collect { updatedUser ->
                _uiState.value = _uiState.value.copy(currentUser = updatedUser)
                if (updatedUser != null && _uiState.value.profile == null) {
                    _uiState.value = _uiState.value.copy(
                        profile = PublicProfile(
                            uid = updatedUser.uid,
                            displayName = updatedUser.name,
                            role = UserRole.ALUMNI.name,
                            college = updatedUser.college,
                            acceptingRequests = true,
                            maxActiveRequests = 3
                        )
                    )
                }
            }
        }

        // 2. Observe incoming connection requests
        viewModelScope.launch {
            repository.getIncomingConnectionsForAlumni(alumniId).collect { incoming ->
                _uiState.value = _uiState.value.copy(incomingRequests = incoming)
            }
        }

        // 3. Observe accepted connections
        viewModelScope.launch {
            repository.getAcceptedConnectionsForAlumni(alumniId).collect { accepted ->
                _uiState.value = _uiState.value.copy(acceptedConnections = accepted)
            }
        }

        // 4. Observe public profile
        viewModelScope.launch {
            repository.getDiscoverableAlumni().collect { list ->
                val myProfile = list.firstOrNull { it.uid == alumniId }
                if (myProfile != null) {
                    _uiState.value = _uiState.value.copy(profile = myProfile)
                }
            }
        }
    }

    fun acceptRequest(connectionId: String) {
        val currentUserId = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            val result = repository.respondToConnection(connectionId, accept = true, currentUserId = currentUserId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    feedbackMessage = "Connection accepted! Workspace opened to agree on a next action."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to accept connection"
                )
            }
        }
    }

    fun declineRequest(connectionId: String) {
        val currentUserId = repository.getCurrentUser()?.uid ?: return
        viewModelScope.launch {
            val result = repository.respondToConnection(connectionId, accept = false, currentUserId = currentUserId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(feedbackMessage = "Request declined.")
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to decline connection"
                )
            }
        }
    }

    fun toggleAcceptingRequests() {
        val user = repository.getCurrentUser() ?: return
        val currentProfile = uiState.value.profile ?: PublicProfile(
            uid = user.uid,
            displayName = user.name,
            role = UserRole.ALUMNI.name,
            college = user.college,
            acceptingRequests = true,
            maxActiveRequests = 3
        )
        val updated = currentProfile.copy(acceptingRequests = !currentProfile.acceptingRequests)
        viewModelScope.launch {
            repository.updateAlumniPublicProfile(updated)
            _uiState.value = _uiState.value.copy(profile = updated)
        }
    }

    fun logout() {
        repository.logout()
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null, errorMessage = null)
    }
}
