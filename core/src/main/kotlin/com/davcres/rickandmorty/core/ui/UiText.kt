package com.davcres.rickandmorty.core.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class StringResource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList(),
    ) : UiText

    @Suppress("SpreadOperator")
    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
        is StringResource -> if (args.isEmpty()) {
            stringResource(resId)
        } else {
            stringResource(resId, *args.toTypedArray())
        }
    }

    @Suppress("SpreadOperator")
    fun asString(context: Context): String = when (this) {
        is DynamicString -> value
        is StringResource -> if (args.isEmpty()) {
            context.getString(resId)
        } else {
            context.getString(resId, *args.toTypedArray())
        }
    }
}
