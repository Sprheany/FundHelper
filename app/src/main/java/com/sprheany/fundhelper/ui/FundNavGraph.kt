package com.sprheany.fundhelper.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sprheany.fundhelper.ui.home.HomeScreen
import com.sprheany.fundhelper.ui.search.SearchScreen

@Composable
fun FundNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = FundDestinations.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = FundDestinations.HOME) {
            HomeScreen(
                navigateToSearch = {
                    navController.popUpTo(FundDestinations.SEARCH)
                }
            )
        }
        composable(route = FundDestinations.SEARCH) {
            SearchScreen(
                navigateUp = { navController.popBackStack() }
            )
        }
    }
}

fun NavController.popUpTo(route: String) = navigate(route) {
    popUpTo(graph.findStartDestination().id) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}