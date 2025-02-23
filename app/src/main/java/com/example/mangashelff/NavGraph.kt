package com.example.mangashelff

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mangashelff.ui.Screens.HomeScreen
import com.example.mangashelff.ui.Screens.MangaDetailScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val viewModel: MangaViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = "mangaList") {
        composable("mangaList") {
            HomeScreen(navController,viewModel)
        }
        composable("mangaDetail") {
            MangaDetailScreen(navController,viewModel)
//            MangaDetailScreen(navController,mangaViewModel)
        }
    }
}