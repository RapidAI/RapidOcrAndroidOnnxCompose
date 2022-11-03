package com.benjaminwan.ocr.ui.widget.snackbar

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.benjaminwan.ocr.R

enum class ToastType(@DrawableRes val icon: Int, val color: Color) {
    Success(R.drawable.ic_success, Color(0xFF66BC6A)),
    Info(R.drawable.ic_info, Color(0xFF24B7F6)),
    Warning(R.drawable.ic_warning, Color(0xFFFFA921)),
    Error(R.drawable.ic_error, Color(0xFFEF524F)),
    ;
}