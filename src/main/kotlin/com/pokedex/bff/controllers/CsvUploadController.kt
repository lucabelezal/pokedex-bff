package com.pokedex.bff.controllers

import com.pokedex.bff.service.CsvImportService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping("/csv")
class CsvUploadController(
    private val csvImportService: CsvImportService
) {
    @PostMapping("/import")
    fun uploadCsv(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("tableName") tableName: String // Adicionado o parâmetro tableName
    ): ResponseEntity<String> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body("O arquivo está vazio.")
        }
        try {
            // AQUI ESTÁ A MUDANÇA: Chamando o novo método e passando o tableName
            val insertedCount = csvImportService.importCsvForTable(tableName, file.inputStream)
            return ResponseEntity.ok("CSV para a tabela '$tableName' importado com sucesso. $insertedCount linhas inseridas.")
        } catch (e: IllegalArgumentException) {
            // Captura erros de tableName inválido ou mapeamento não encontrado
            return ResponseEntity.badRequest().body("Erro: ${e.message}")
        } catch (e: IOException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao ler o arquivo: ${e.message}")
        } catch (e: Exception) {
            // Para capturar NumberFormatException, DataIntegrityViolationException, etc.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro durante a importação do CSV para a tabela '$tableName': ${e.message}")
        }
    }

    @GetMapping("/test")
    fun test(): ResponseEntity<String> = ResponseEntity.ok("Controller funcionando!")
}