package com.benjaminwan.ocr.base

import androidx.compose.material.SnackbarDuration
import com.airbnb.mvrx.MavericksViewModel
import com.benjaminwan.ocr.ui.widget.snackbar.SnackType
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : BaseState>(initialState: S) : MavericksViewModel<S>(initialState) {

    private fun showSnackbar(msg: String, toastType: SnackType) = viewModelScope.launch {
        val state = awaitState()
        val scaffoldState = state.scaffoldState
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        scaffoldState.snackbarHostState.showSnackbar(
            msg,
            toastType.name,
            duration = SnackbarDuration.Short
        )
    }

    fun showSuccess(msg: String) = showSnackbar(msg, SnackType.Success)
    fun showInfo(msg: String) = showSnackbar(msg, SnackType.Info)
    fun showWarning(msg: String) = showSnackbar(msg, SnackType.Warning)
    fun showError(msg: String) = showSnackbar(msg, SnackType.Error)
}