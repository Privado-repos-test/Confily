package com.paligot.conferences.android.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun Conferences4HallTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = darkColors,
        typography = typography,
        content = content
    )
}
