package com.example.rickandmortymvvm.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.rickandmortymvvm.ui.theme.RickAndMortyTheme

@Composable
fun RickAndMortyApp(
    context: Context,
    activity: ComponentActivity
) {
    RickAndMortyTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            RickAndMortyActions(navController)
        }
        val onBackPressedDispatcher = activity.onBackPressedDispatcher

        RickAndMortyNavGraph(
            navController = navController,
            navigateToDetail = navigationActions.navigateToDetail,
            navigateToSearch = navigationActions.navigateToSearch,
            navigateToLogin = navigationActions.navigateToLogin,
            context = context,
            onBackPressedDispatcher = onBackPressedDispatcher
        )
    }
}