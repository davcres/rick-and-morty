package com.davcres.rickandmorty.domain.model

sealed interface AppError {
    data object Network : AppError
    data object Timeout : AppError
    data object NotFound : AppError
    data object Server : AppError
    data class Unknown(val throwable: Throwable? = null) : AppError
}
