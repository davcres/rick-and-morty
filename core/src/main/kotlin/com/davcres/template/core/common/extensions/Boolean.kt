package com.davcres.template.core.common.extensions

fun Boolean?.orEmpty(): Boolean = this == true

@Suppress("FunctionOnlyReturningConstant")
fun Boolean.Companion.empty(): Boolean = false
