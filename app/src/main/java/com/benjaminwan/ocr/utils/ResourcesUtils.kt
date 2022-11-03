package com.benjaminwan.ocr.utils

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.benjaminwan.ocr.app.App

fun getString(@StringRes id: Int): String =
    App.INST.resources.getString(id)

fun getString(@StringRes id: Int, vararg formatArgs: Any): String =
    App.INST.resources.getString(id, *formatArgs)

fun getStringArray(@ArrayRes id: Int): Array<String> =
    App.INST.resources.getStringArray(id)
