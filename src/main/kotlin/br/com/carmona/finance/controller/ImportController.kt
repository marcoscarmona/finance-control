package br.com.carmona.finance.controller

import br.com.carmona.finance.service.ImportService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/import")
class ImportController(
    private val importService: ImportService
) {
    @PostMapping("/csv", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun importCsv(@RequestParam file: MultipartFile) = mapOf(
        "imported" to importService.importCsv(file)
    )
}
