package br.com.financialcontrol.infrastructure.importer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.YearMonth

class MercadoPagoStatementParserTest {
    @Test
    fun `parses the closed invoice and installment purchases`() {
        val statement =
            MercadoPagoStatementParser.parseText(
                """
                Emitida em: 06/10/2026
                Essa é sua fatura de outubro
                Total a pagar R$ 33,63
                Cartão Visa [************8866]
                19/09 MERCADOLIVRE*TRYIT Parcela 1 de 5 R$ 35,20
                13/07 MERCADOPAGO*LOJAELECTROLU Parcela 15 de 18 R$ 34,38
                """.trimIndent(),
            )

        assertEquals(YearMonth.of(2026, 10), statement.month)
        assertEquals("8866", statement.lastFourDigits)
        assertEquals(BigDecimal("33.63"), statement.closedTotal)
        assertEquals(2, statement.rows.size)
        assertEquals(15, statement.rows.last().installmentNumber)
        assertEquals(18, statement.rows.last().installments)
    }
}
