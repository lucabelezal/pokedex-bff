package com.pokedex.bff

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class PokedexBffApplicationTests {
    @Test
    fun contextLoads() {
        // Testa se o contexto Spring Boot carrega sem erros
    }
}
