
package com.pokedex.bff.infrastructure.config.web

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {
    @Bean
    fun customOpenAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Pokédex BFF API")
                .version("1.0.0")
                .description("Pokédex BFF OpenAPI Documentation")
        )
}
