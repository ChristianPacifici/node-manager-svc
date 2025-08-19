package com.prewave.nodemanager.interceptor

import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class MdcInterceptor : HandlerInterceptor {
    companion object {
        const val REQUEST_ID_HEADER = "x-request-id"
        const val CORRELATION_ID_HEADER = "x-correlation-id"
        const val REQUEST_ID_MDC_KEY = "requestId"
        const val CORRELATION_ID_MDC_KEY = "correlationId"
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.getHeader(REQUEST_ID_HEADER)?.let { MDC.put(REQUEST_ID_MDC_KEY, it) }
        request.getHeader(CORRELATION_ID_HEADER)?.let { MDC.put(CORRELATION_ID_MDC_KEY, it) }
        return true
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        MDC.clear()
    }
}