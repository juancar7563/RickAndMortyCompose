package com.example.rickandmortymvvm.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

sealed class Screen(val route: String){

    object Splash: Screen("splash")
    object Home: Screen("home")
    object Login: Screen("login")
    object SignUp: Screen("signup")
    object Detail: Screen("detail/{id}?previousScreen={previousScreen}") {
        fun passId(id:Int, screen:String): String {
            return "detail/$id?previousScreen=$screen"
        }
    }
    object Search: Screen("search")
}

class RickAndMortyActions(navController: NavController) {
    val navigateToLogin: () -> Unit = {
        navController.navigate(Screen.Login.route) {
            popUpTo(navController.graph.findStartDestination().id){
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToSignUp: () -> Unit = {
        navController.navigate(Screen.SignUp.route) {
            popUpTo(navController.graph.findStartDestination().id){
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToHome: () -> Unit = {
        navController.navigate(Screen.Home.route) {
            popUpTo(navController.graph.findStartDestination().id){
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToDetail: (Int, String) -> Unit = {id, screen ->
        navController.navigate(Screen.Detail.passId(id, screen)) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    val navigateToSearch: () -> Unit = {
        navController.navigate(Screen.Search.route) {
            popUpTo(navController.graph.findStartDestination().id){
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}