package com.example.rickandmortymvvm.ui

import android.content.Context
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmortymvvm.domain.model.Characters
import com.example.rickandmortymvvm.ui.detail.DetailScreen
import com.example.rickandmortymvvm.ui.home.HomeScreen
import com.example.rickandmortymvvm.ui.login.LoginScreen
import com.example.rickandmortymvvm.ui.login.SignUpScreen
import com.example.rickandmortymvvm.ui.search.SearchScreen
import com.example.rickandmortymvvm.ui.splash.SplashScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RickAndMortyNavGraph(
    modifier: Modifier = Modifier,
    navigateToSearch: () -> Unit,
    navigateToDetail: (Int, String) -> Unit,
    navigateToLogin: () -> Unit,
    context: Context,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(route = Screen.Splash.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) }) {
            SplashScreen(navController)
        }
        composable(route = Screen.Login.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) }) {
            LoginScreen(navController)
        }
        composable(route = Screen.SignUp.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) }) {
            SignUpScreen(navController)
        }
        composable(route = Screen.Home.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) }) {
            HomeScreen(
                navController,
                onItemClicked = { id, name ->
                    navigateToDetail(id, name)
                },
                onSearchClicked = navigateToSearch,
                onLogoutPressed = navigateToLogin
            )
        }
        composable(route = Screen.Search.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) }) {
            SearchScreen(
                onItemClicked = { id, name ->
                    navigateToDetail(id, name)
                },
                upPress = {
                    onBackPressedDispatcher.onBackPressed()
                },
                modifier = modifier,
                context = context
            )
        }
        composable(
            route = Screen.Detail.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(400)) },
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("previousScreen") { type = NavType.StringType }
            )
        ) {
            DetailScreen(
                upPress = {
                    onBackPressedDispatcher.onBackPressed()
                }
            )
        }
    }

}






