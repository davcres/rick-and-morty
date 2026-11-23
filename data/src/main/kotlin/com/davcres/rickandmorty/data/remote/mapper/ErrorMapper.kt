package com.davcres.rickandmorty.data.remote.mapper

import com.davcres.rickandmorty.domain.model.AppError
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val HTTP_NOT_FOUND = 404
private const val HTTP_SERVER_ERROR_MIN = 500
private const val HTTP_SERVER_ERROR_MAX = 599

fun Throwable.toAppError(): AppError = when (this) {
    is SocketTimeoutException -> AppError.Timeout
    is UnknownHostException, is ConnectException -> AppError.Network
    is HttpException -> when {
        code() == HTTP_NOT_FOUND -> AppError.NotFound
        code() in HTTP_SERVER_ERROR_MIN..HTTP_SERVER_ERROR_MAX -> AppError.Server
        else -> AppError.Unknown(this)
    }
    is IOException -> AppError.Network
    else -> AppError.Unknown(this)
}
