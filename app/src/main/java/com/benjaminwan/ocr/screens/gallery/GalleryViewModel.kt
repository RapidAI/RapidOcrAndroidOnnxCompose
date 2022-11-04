package com.benjaminwan.ocr.screens.gallery

import android.content.Context
import android.net.Uri
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.boxScoreThreshRange
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.boxThreshRange
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.maxSideLenRange
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.paddingRange
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.unClipRatioRange

class GalleryViewModel(
    initialState: GalleryState,
    private val context: Context,
) : MavericksViewModel<GalleryState>(initialState) {

    init {

    }

    fun setSelectTab(tab: GalleryTab) {
        setState { copy(selectTab = tab) }
    }

    fun setUri(uri: Uri? = null) {
        setState { copy(imageUri = uri) }
    }

    fun setMaxSideLen(input: String) {
        val value = try {
            input.toInt()
        } catch (e: Exception) {
            null
        }
        setState { copy(maxSideLen = input, maxSideLenError = value == null || value !in maxSideLenRange) }
    }

    fun setPadding(input: String) {
        val value = try {
            input.toInt()
        } catch (e: Exception) {
            null
        }
        setState { copy(padding = input, paddingError = value == null || value !in paddingRange) }
    }

    fun setBoxScoreThresh(input: String) {
        val value = input.toFloatOrNull()
        setState { copy(boxScoreThresh = input, boxScoreThreshError = value == null || value !in boxScoreThreshRange) }
    }

    fun setBoxThresh(input: String) {
        val value = input.toFloatOrNull()
        setState { copy(boxThresh = input, boxThreshError = value == null || value !in boxThreshRange) }
    }

    fun setUnClipRatio(input: String) {
        val value = input.toFloatOrNull()
        setState { copy(unClipRatio = input, unClipRatioError = value == null || value !in unClipRatioRange) }
    }

    fun setDoAngle(input: Boolean) {
        setState { copy(doAngle = input, mostAngle = if (!input) false else mostAngle) }
    }

    fun setMostAngle(input: Boolean) {
        setState { copy(mostAngle = input) }
    }

    companion object : MavericksViewModelFactory<GalleryViewModel, GalleryState> {
        override fun create(viewModelContext: ViewModelContext, state: GalleryState): GalleryViewModel? {
            val context = viewModelContext.activity.applicationContext
            return GalleryViewModel(state, context)
        }
    }

}