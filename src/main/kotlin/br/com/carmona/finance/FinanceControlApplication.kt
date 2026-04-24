package br.com.carmona.finance

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FinanceControlApplication

fun main(args: Array<String>) {
    runApplication<FinanceControlApplication>(*args)
}
