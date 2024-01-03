package com.example.rickandmortymvvm.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.rickandmortymvvm.ui.theme.RickAndMortyTheme

@Composable
fun RickAndMortyApp(
    context: Context
) {
    RickAndMortyTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            RickAndMortyActions(navController)
        }

        RickAndMortyNavGraph(
            navController = navController,
            navigateToHome = navigationActions.navigateToHome,
            navigateToDetail = navigationActions.navigateToDetail,
            navigateToSearch = navigationActions.navigateToSearch,
            context = context
        )
    }
}