package com.benjaminwan.ocr.screens

import androidx.annotation.DrawableRes
import com.benjaminwan.ocr.R
import com.benjaminwan.ocr.utils.getString

sealed class Screen(val route: String, val title: String, @DrawableRes val iconId: Int) {
    object Loading : Screen("Loading", "Loading", 0)
    object Main : Screen("Main", getString(R.string.screen_main), 0)
    object Gallery : Screen("Gallery", getString(R.string.screen_gallery), R.drawable.ic_gallery)
    object Camera : Screen("Camera", getString(R.string.screen_camera), R.drawable.ic_camera)
    object Imei : Screen("Imei", getString(R.string.screen_imei), R.drawable.ic_imei)
    object IdCard : Screen("IdCard", getString(R.string.screen_id_card), R.drawable.ic_id_card)
    object Plate : Screen("Plate", getString(R.string.screen_plate), R.drawable.ic_plate)
    object About : Screen("About", getString(R.string.screen_about), R.drawable.ic_about)
    companion object {
        val menuList: List<Screen>
            get() = listOf(
                Gallery, Camera, Imei, IdCard, Plate, About
            )
    }
}
