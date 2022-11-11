package com.benjaminwan.ocr.screens.gallery

import android.content.Context
import android.net.Uri
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.benjaminwan.ocr.app.App
import com.benjaminwan.ocr.base.BaseViewModel
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.boxScoreThreshRange
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.boxThreshRange
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.maxSideLenRange
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.paddingRange
import com.benjaminwan.ocr.screens.gallery.GalleryState.Companion.unClipRatioRange
import com.benjaminwan.ocr.utils.decodeUri
import com.benjaminwan.ocr.utils.toClipboard
import com.benjaminwan.ocrlibrary.OcrEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryViewModel(
    initialState: GalleryState,
    private val context: Context,
) : BaseViewModel<GalleryState>(initialState) {

    private val ocrEngine = OcrEngine(App.INST.applicationContext)

    init {

    }

    fun setSelectTab(tab: GalleryTab) {
        setState { copy(selectTab = tab) }
    }

    fun setUri(uri: Uri? = null) {
        setState { copy(imageUri = uri, selectTab = tabs.first()) }
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

    fun detect() = suspend {
        val state = awaitState()
        val maxSideLen = state.maxSideLen.toInt()
        val padding = state.padding.toInt()
        val uri = state.imageUri ?: throw Exception("uri is null")
        val bmp = context.decodeUri(uri) ?: throw Exception("bitmap is null")
        withContext(Dispatchers.IO) {
            ocrEngine.detect(bmp, maxSideLen, padding, 0.5F, 0.3F, 1.6F, true, true)
        }
    }
        .execute {
            if (it is Fail) showError(it.error.message.toString())
            copy(detectRequest = it, selectTab = if (it is Success) GalleryTab.TextResult else selectTab)
        }

    fun toClipboard(text: String) {
        context.toClipboard(text)
        showInfo("已复制到剪切板!")
    }

    override fun onCleared() {
        super.onCleared()
        ocrEngine.close()
    }

    companion object : MavericksViewModelFactory<GalleryViewModel, GalleryState> {
        override fun create(viewModelContext: ViewModelContext, state: GalleryState): GalleryViewModel? {
            val context = viewModelContext.activity.applicationContext
            return GalleryViewModel(state, context)
        }
    }

}