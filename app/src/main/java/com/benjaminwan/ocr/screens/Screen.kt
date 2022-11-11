package com.benjaminwan.ocr.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.benjaminwan.ocr.R

sealed class Screen(val route: String, @StringRes val titleId: Int, @DrawableRes val iconId: Int) {
    object Loading : Screen("Loading", 0, 0)
    object Main : Screen("Main", R.string.screen_main, 0)
    object Gallery : Screen("Gallery", R.string.screen_gallery, R.drawable.ic_gallery)
    object Camera : Screen("Camera", R.string.screen_camera, R.drawable.ic_camera)
    object Imei : Screen("Imei", R.string.screen_imei, R.drawable.ic_imei)
    object IdCard : Screen("IdCard", R.string.screen_id_card, R.drawable.ic_id_card)
    object Plate : Screen("Plate", R.string.screen_plate, R.drawable.ic_plate)
    object About : Screen("About", R.string.screen_about, R.drawable.ic_about)
    companion object {
        val mainMenuList: List<Screen>
            get() = listOf(
                Gallery, Camera, Imei, IdCard, Plate, About
            )
    }
}
