package com.pokedex.bff.utils

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream
import java.io.InputStreamReader

object CsvParser {
    fun parse(inputStream: InputStream): List<Map<String, String>> {
        InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
            val format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
            CSVParser(reader, format).use { parser ->
                return parser.records.map { it.toMap() }
            }
        }
    }
}