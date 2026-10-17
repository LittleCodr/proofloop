package com.proofloop.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object StudentHome : Screen("student_home")
    object CreateRequest : Screen("create_request")
    object MatchResults : Screen("match_results/{requestId}") {
        fun createRoute(requestId: String) = "match_results/$requestId"
    }
    object AlumniInbox : Screen("alumni_inbox")
    object ConnectionWorkspace : Screen("connection_workspace/{connectionId}") {
        fun createRoute(connectionId: String) = "connection_workspace/$connectionId"
    }
    object OutcomeTracker : Screen("outcome_tracker")
    object ImpactLedger : Screen("impact_ledger")
}

