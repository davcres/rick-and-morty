package com.davcres.rickandmorty.feature.characters.mapper

import com.davcres.rickandmorty.core.ui.UiText
import com.davcres.rickandmorty.domain.model.AppError
import com.davcres.rickandmorty.feature.characters.R

fun AppError.toUiText(fallbackResId: Int = R.string.search_error_fallback): UiText = when (this) {
    AppError.Timeout -> UiText.StringResource(R.string.error_timeout_message)
    AppError.Network -> UiText.StringResource(R.string.error_network_message)
    AppError.NotFound -> UiText.StringResource(fallbackResId)
    AppError.Server, is AppError.Unknown -> UiText.StringResource(fallbackResId)
}
