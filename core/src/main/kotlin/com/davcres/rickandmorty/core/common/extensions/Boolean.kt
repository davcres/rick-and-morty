package com.davcres.rickandmorty.core.common.extensions

fun Boolean?.orEmpty(): Boolean = this == true

@Suppress("FunctionOnlyReturningConstant")
fun Boolean.Companion.empty(): Boolean = false
