package com.benjaminwan.ocr.base

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import com.airbnb.mvrx.MavericksState

open class BaseState : MavericksState {
    val scaffoldState: ScaffoldState = ScaffoldState(DrawerState(DrawerValue.Closed), SnackbarHostState())
}