package com.pokedex.bff.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class RootController {
    @GetMapping("/")
    fun index(): String {
        return "API rodando com sucesso!"
    }
}