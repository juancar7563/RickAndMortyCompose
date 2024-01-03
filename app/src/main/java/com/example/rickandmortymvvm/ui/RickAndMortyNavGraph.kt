package com.example.rickandmortymvvm.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmortymvvm.ui.detail.DetailScreen
import com.example.rickandmortymvvm.ui.home.HomeScreen
import com.example.rickandmortymvvm.ui.search.SearchScreen

@Composable
fun RickAndMortyNavGraph (
    modifier: Modifier = Modifier,
    navigateToHome: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToDetail: (Int, String) -> Unit,
    context: Context,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onItemClicked = { id, name ->
                    navigateToDetail(id, name)
                },
                onSearchClicked = navigateToSearch
            )
        }
        composable(route = Screen.Search.route) {
            SearchScreen(
                onItemClicked = { id, name ->
                    navigateToDetail(id, name)
                },
                modifier = modifier,
                context = context
            )
        }
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("previousScreen") { type = NavType.StringType  }
            )
        ) {
            DetailScreen(
                upPress = {
                    when (it.arguments?.getString("previousScreen")) {
                        Screen.Home.route -> navigateToHome()
                        Screen.Search.route -> navigateToSearch()
                        else -> navigateToHome() // Opcional: Establece una pantalla predeterminada para casos inesperados
                    }
                }
            )
        }
    }

}






