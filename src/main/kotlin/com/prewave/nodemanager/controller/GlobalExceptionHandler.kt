package com.prewave.nodemanager.controller

import com.prewave.nodemanager.exception.DuplicateResourceException
import com.prewave.nodemanager.exception.InvalidOperationException
import com.prewave.nodemanager.exception.ResourceNotFoundException
import com.prewave.nodemanager.interceptor.MdcInterceptor
import org.slf4j.MDC
import org.springframework.beans.TypeMismatchException
import org.springframework.dao.CannotAcquireLockException
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.*

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    data class ErrorResponse(
        val status: Int,
        val errorMessage: String
    )

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException, request: WebRequest): ResponseEntity<Any>? {
        return handleGeneralError(HttpStatus.NOT_FOUND, ex.message ?: "Resource Not found")
    }

    @ExceptionHandler(InvalidOperationException::class)
    fun handleInvalidOperation(ex: InvalidOperationException, request: WebRequest): ResponseEntity<Any>? {
        return handleGeneralError(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid operation")
    }

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicateResource(ex: DuplicateResourceException, request: WebRequest): ResponseEntity<Any>? {
        return handleGeneralError(HttpStatus.CONFLICT, ex.message ?: "Duplicate resource")
    }

    @ExceptionHandler(
        IllegalArgumentException::class,
        IllegalFormatException::class,
        IllegalStateException::class)
    fun handleBadRequestRuntimeException(ex: RuntimeException, request: WebRequest): ResponseEntity<Any>? {
        return handleGeneralError(HttpStatus.BAD_REQUEST, ex.message?: "Bad Request")
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleBadRequestException(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        return handleGeneralError(HttpStatus.BAD_REQUEST, ex.message?: "Bad Request")
    }

    @ExceptionHandler(
        EmptyResultDataAccessException::class,
        DataAccessResourceFailureException::class,
        CannotAcquireLockException::class,
        DataIntegrityViolationException::class
    )
    fun handleDataAccessExceptions(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        val status = when (ex) {
            is EmptyResultDataAccessException -> HttpStatus.NOT_FOUND
            is DataAccessResourceFailureException -> HttpStatus.SERVICE_UNAVAILABLE
            is CannotAcquireLockException -> HttpStatus.LOCKED
            is DataIntegrityViolationException -> HttpStatus.CONFLICT
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        val errorResponse = ErrorResponse(status.value(), ex.message ?: "Database error")
        return ResponseEntity(errorResponse, status)
    }

    // Overriding the ResponseEntityExceptionHandler, to manage the Bad Request with my DTO
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleGeneralError(HttpStatus.BAD_REQUEST, ex.message?: "Bad Request")
    }

    // Overriding the ResponseEntityExceptionHandler, to manage the Bad Request with my DTO
    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleGeneralError(HttpStatus.BAD_REQUEST, ex.message?: "Bad Request")
    }

    private fun handleGeneralError(status: HttpStatus, message: String): ResponseEntity<Any>? {
        val errorResponse = ErrorResponse(status.value(), message)
        logError(errorResponse)
        return ResponseEntity(errorResponse, status)
    }

    fun logError(error: ErrorResponse){
        logger.error { "An Error occurred for call " +
                "x-request-id ${MDC.get(MdcInterceptor.REQUEST_ID_MDC_KEY)}," +
                "x-correlation-id ${MDC.get(MdcInterceptor.CORRELATION_ID_MDC_KEY)}," +
                " status - ${error.status} , message - ${error.errorMessage}"}
    }

}