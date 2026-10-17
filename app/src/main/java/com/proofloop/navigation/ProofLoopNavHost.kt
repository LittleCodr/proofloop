package com.proofloop.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.proofloop.ProofLoopApp
import com.proofloop.data.model.UserRole
import com.proofloop.feature.alumni.AlumniInboxScreen
import com.proofloop.feature.auth.AuthScreen
import com.proofloop.feature.connections.ConnectionWorkspaceScreen
import com.proofloop.feature.impact.ImpactLedgerScreen
import com.proofloop.feature.requests.CreateRequestScreen
import com.proofloop.feature.requests.MatchResultsScreen
import com.proofloop.feature.student.StudentHomeScreen

@Composable
fun ProofLoopNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val currentUser = ProofLoopApp.instance.repository.getCurrentUser()
    val startDestination = when {
        currentUser == null -> Screen.Auth.route
        currentUser.role.equals(UserRole.INSTITUTION.name, ignoreCase = true) -> Screen.ImpactLedger.route
        currentUser.role.equals(UserRole.STUDENT.name, ignoreCase = true) ||
        currentUser.role.equals(UserRole.FOUNDER.name, ignoreCase = true) -> Screen.StudentHome.route
        else -> Screen.AlumniInbox.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(280)) + fadeIn(animationSpec = tween(280))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 4 }, animationSpec = tween(280)) + fadeOut(animationSpec = tween(280))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 4 }, animationSpec = tween(280)) + fadeIn(animationSpec = tween(280))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(280)) + fadeOut(animationSpec = tween(280))
        }
    ) {
        // Auth Screen
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = { role ->
                    val destination = when {
                        role.equals(UserRole.INSTITUTION.name, ignoreCase = true) -> Screen.ImpactLedger.route
                        role.equals(UserRole.STUDENT.name, ignoreCase = true) ||
                        role.equals(UserRole.FOUNDER.name, ignoreCase = true) -> Screen.StudentHome.route
                        else -> Screen.AlumniInbox.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }


        // Student Home
        composable(Screen.StudentHome.route) {
            StudentHomeScreen(
                onRequestHelpClick = {
                    navController.navigate(Screen.CreateRequest.route)
                },
                onViewMatchesClick = { requestId ->
                    navController.navigate(Screen.MatchResults.createRoute(requestId))
                },
                onOpenWorkspaceClick = { connectionId ->
                    navController.navigate(Screen.ConnectionWorkspace.createRoute(connectionId))
                },
                onOpenImpactLedgerClick = {
                    navController.navigate(Screen.ImpactLedger.route)
                },
                onSignOutClick = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Create Help Request
        composable(Screen.CreateRequest.route) {
            CreateRequestScreen(
                onNavigateBack = { navController.popBackStack() },
                onMatchesFound = { requestId ->
                    navController.navigate(Screen.MatchResults.createRoute(requestId)) {
                        popUpTo(Screen.CreateRequest.route) { inclusive = true }
                    }
                }
            )
        }

        // Match Results
        composable(
            route = Screen.MatchResults.route,
            arguments = listOf(navArgument("requestId") { type = NavType.StringType })
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            MatchResultsScreen(
                requestId = requestId,
                onNavigateBack = { navController.popBackStack() },
                onViewHome = {
                    navController.navigate(Screen.StudentHome.route) {
                        popUpTo(Screen.StudentHome.route) { inclusive = true }
                    }
                }
            )
        }

        // Alumni Inbox
        composable(Screen.AlumniInbox.route) {
            AlumniInboxScreen(
                onOpenWorkspaceClick = { connectionId ->
                    navController.navigate(Screen.ConnectionWorkspace.createRoute(connectionId))
                },
                onOpenImpactLedgerClick = {
                    navController.navigate(Screen.ImpactLedger.route)
                },
                onSignOutClick = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Connection Workspace
        composable(
            route = Screen.ConnectionWorkspace.route,
            arguments = listOf(navArgument("connectionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val connectionId = backStackEntry.arguments?.getString("connectionId") ?: ""
            ConnectionWorkspaceScreen(
                connectionId = connectionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Institutional Impact Ledger
        composable(Screen.ImpactLedger.route) {
            ImpactLedgerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
