package com.benjaminwan.ocr.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.benjaminwan.ocr.screens.about.AboutScreen
import com.benjaminwan.ocr.screens.camera.CameraScreen
import com.benjaminwan.ocr.screens.gallery.GalleryScreen
import com.benjaminwan.ocr.screens.idcard.IdCardScreen
import com.benjaminwan.ocr.screens.imei.ImeiScreen
import com.benjaminwan.ocr.screens.loading.LoadingScreen
import com.benjaminwan.ocr.screens.main.MainScreen
import com.benjaminwan.ocr.screens.plate.PlateScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Gallery.route,
    ) {

        composable(
            route = Screen.Loading.route,
        ) {
            LoadingScreen(navController)
        }

        composable(
            route = Screen.Main.route,
        ) {
            MainScreen(navController)
        }

        composable(
            route = Screen.Gallery.route,
        ) {
            GalleryScreen(navController)
        }

        composable(
            route = Screen.Camera.route,
        ) {
            CameraScreen(navController)
        }

        composable(
            route = Screen.Imei.route,
        ) { backStackEntry ->
            ImeiScreen(navController)
        }

        composable(
            route = Screen.IdCard.route,
        ) {
            IdCardScreen(navController)
        }

        composable(
            route = Screen.Plate.route,
        ) {
            PlateScreen(navController)
        }

        composable(
            route = Screen.About.route,
        ) {
            AboutScreen(navController)
        }
    }
}