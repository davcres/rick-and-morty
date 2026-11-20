package com.davcres.template.core.common.extensions

@Suppress("FunctionOnlyReturningConstant")
fun Int.Companion.empty(): Int = 0

fun Int?.orEmpty(): Int = this ?: 0
