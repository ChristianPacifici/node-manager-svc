package com.prewave.nodemanager.configuration

import com.prewave.nodemanager.interceptor.MdcInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig @Autowired constructor(
    private val mdcInterceptor: MdcInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(mdcInterceptor)
            .addPathPatterns("/**") // Apply to all endpoints
    }
}