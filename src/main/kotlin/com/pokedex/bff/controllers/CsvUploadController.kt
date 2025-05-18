package com.pokedex.bff.controllers

import com.pokedex.bff.services.CsvImportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/csv")
class CsvUploadController(
    private val csvImportService: CsvImportService
) {
    @PostMapping("/import")
    fun uploadCsv(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        csvImportService.importPokemonCsv(file)
        return ResponseEntity.ok("CSV importado com sucesso")
    }

    @GetMapping("/test")
    fun test(): ResponseEntity<String> = ResponseEntity.ok("Controller funcionando!")
}