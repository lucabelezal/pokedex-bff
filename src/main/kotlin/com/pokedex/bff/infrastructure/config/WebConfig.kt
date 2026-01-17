package com.pokedex.bff.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    @Value("\${app.cors.allowed-origins:*}") private val allowedOrigins: String,
    @Value("\${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}") private val allowedMethods: String,
    @Value("\${app.cors.allowed-headers:*}") private val allowedHeaders: String
) : WebMvcConfigurer {
    @Bean
    fun restTemplate() = RestTemplate()

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*allowedOrigins.split(",").map { it.trim() }.toTypedArray())
            .allowedMethods(*allowedMethods.split(",").map { it.trim() }.toTypedArray())
            .allowedHeaders(*allowedHeaders.split(",").map { it.trim() }.toTypedArray())
    }
}
