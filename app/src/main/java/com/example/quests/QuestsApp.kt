package com.example.quests

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quests.ui.navigation.QuestsNavHost


/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun QuestsApp(navController: NavHostController = rememberNavController()) {
    QuestsNavHost(navController = navController)
}

// TODO: TopAppBar