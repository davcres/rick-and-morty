package com.davcres.rickandmorty.data.remote.mapper

import com.davcres.rickandmorty.domain.model.AppError
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val HTTP_BAD_REQUEST = 400
private const val HTTP_NOT_FOUND = 404
private const val HTTP_INTERNAL_SERVER_ERROR = 500

class ErrorMapperTest {

    @Test
    fun `SocketTimeoutException maps to AppError Timeout`() {
        val exception = SocketTimeoutException("timeout")
        assertEquals(AppError.Timeout, exception.toAppError())
    }

    @Test
    fun `UnknownHostException maps to AppError Network`() {
        val exception = UnknownHostException("Unable to resolve host")
        assertEquals(AppError.Network, exception.toAppError())
    }

    @Test
    fun `ConnectException maps to AppError Network`() {
        val exception = ConnectException("Failed to connect")
        assertEquals(AppError.Network, exception.toAppError())
    }

    @Test
    fun `IOException maps to AppError Network`() {
        val exception = IOException("Broken pipe")
        assertEquals(AppError.Network, exception.toAppError())
    }

    @Test
    fun `HttpException with 404 maps to AppError NotFound`() {
        val response = Response.error<Any>(
            HTTP_NOT_FOUND,
            "Not found".toResponseBody("text/plain".toMediaTypeOrNull()),
        )
        val exception = HttpException(response)
        assertEquals(AppError.NotFound, exception.toAppError())
    }

    @Test
    fun `HttpException with 500 maps to AppError Server`() {
        val response = Response.error<Any>(
            HTTP_INTERNAL_SERVER_ERROR,
            "Server error".toResponseBody("text/plain".toMediaTypeOrNull()),
        )
        val exception = HttpException(response)
        assertEquals(AppError.Server, exception.toAppError())
    }

    @Test
    fun `HttpException with 400 maps to AppError Unknown`() {
        val response = Response.error<Any>(
            HTTP_BAD_REQUEST,
            "Bad request".toResponseBody("text/plain".toMediaTypeOrNull()),
        )
        val exception = HttpException(response)
        val result = exception.toAppError()
        assertTrue(result is AppError.Unknown)
    }

    @Test
    fun `generic exception maps to AppError Unknown`() {
        val exception = IllegalStateException("Something broke")
        val result = exception.toAppError()
        assertTrue(result is AppError.Unknown)
        assertEquals(exception, (result as AppError.Unknown).throwable)
    }
}
