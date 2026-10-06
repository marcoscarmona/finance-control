package br.com.financialcontrol.adapters.input.web.controller

import org.springframework.http.HttpStatus
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/users/{userId}/insights")
class InsightsController(
    private val jdbc: JdbcTemplate,
) {
    @GetMapping("/category-suggestions")
    fun categorySuggestions(
        @PathVariable userId: UUID,
    ) = jdbc.query(
        """SELECT COALESCE(e.merchant, e.description), SUM(e.total_amount), COUNT(*)
               FROM expenses e JOIN categories c ON c.id = e.category_id
               WHERE e.user_id = ?::uuid AND c.name = 'Outros' AND e.total_amount > 0
               GROUP BY COALESCE(e.merchant, e.description) HAVING COUNT(*) >= 2 ORDER BY SUM(e.total_amount) DESC""",
        { rs, _ -> CategorySuggestion(rs.getString(1), rs.getBigDecimal(2), rs.getInt(3)) },
        userId.toString(),
    )

    @PostMapping("/category-suggestions/accept")
    @ResponseStatus(HttpStatus.CREATED)
    fun acceptCategory(
        @PathVariable userId: UUID,
        @RequestBody body: AcceptCategoryRequest,
    ): CategorySuggestion {
        val categoryId = UUID.randomUUID()
        jdbc.update(
            "INSERT INTO categories (id, user_id, name, color, active) VALUES (?::uuid, ?::uuid, ?, ?, true)",
            categoryId.toString(),
            userId.toString(),
            body.categoryName,
            body.color,
        )
        jdbc.update(
            "INSERT INTO merchant_category_rules (id, user_id, merchant_key, category_id) VALUES (?::uuid, ?::uuid, ?, ?::uuid) ON CONFLICT (user_id, merchant_key) DO UPDATE SET category_id = EXCLUDED.category_id",
            UUID.randomUUID().toString(),
            userId.toString(),
            body.merchant.lowercase(),
            categoryId.toString(),
        )
        jdbc.update(
            "UPDATE expenses SET category_id = ?::uuid WHERE user_id = ?::uuid AND lower(COALESCE(merchant, description)) = ?",
            categoryId.toString(),
            userId.toString(),
            body.merchant.lowercase(),
        )
        return CategorySuggestion(body.merchant, BigDecimal.ZERO, 0)
    }

    @GetMapping("/subscription-suggestions")
    fun subscriptionSuggestions(
        @PathVariable userId: UUID,
    ) = jdbc.query(
        """SELECT COALESCE(e.merchant, e.description), AVG(e.total_amount), MAX(e.purchase_date), (array_agg(e.category_id))[1], (array_agg(e.credit_card_id))[1]
               FROM expenses e WHERE e.user_id = ?::uuid AND e.total_amount > 0 AND e.description NOT ILIKE '%Parcela %'
               GROUP BY COALESCE(e.merchant, e.description) HAVING COUNT(*) >= 2 ORDER BY MAX(e.purchase_date) DESC""",
        {
            rs,
            _,
            ->
            SubscriptionSuggestion(
                rs.getString(1),
                rs.getBigDecimal(2),
                rs.getDate(3).toLocalDate(),
                UUID.fromString(rs.getString(4)),
                UUID.fromString(rs.getString(5)),
            )
        },
        userId.toString(),
    )

    @PostMapping("/subscription-suggestions/accept")
    @ResponseStatus(HttpStatus.CREATED)
    fun acceptSubscription(
        @PathVariable userId: UUID,
        @RequestBody body: AcceptSubscriptionRequest,
    ): SubscriptionSuggestion {
        jdbc.update(
            "INSERT INTO subscriptions (id, user_id, category_id, credit_card_id, name, amount, frequency, charge_day, active) VALUES (?::uuid, ?::uuid, ?::uuid, ?::uuid, ?, ?, 'MONTHLY', ?, true)",
            UUID.randomUUID().toString(),
            userId.toString(),
            body.categoryId.toString(),
            body.creditCardId.toString(),
            body.name,
            body.amount,
            body.chargeDay,
        )
        return SubscriptionSuggestion(body.name, body.amount, LocalDate.now(), body.categoryId, body.creditCardId)
    }

    data class CategorySuggestion(
        val merchant: String,
        val observedAmount: BigDecimal,
        val occurrences: Int,
    )

    data class SubscriptionSuggestion(
        val name: String,
        val amount: BigDecimal,
        val lastCharge: LocalDate,
        val categoryId: UUID,
        val creditCardId: UUID,
    )

    data class AcceptCategoryRequest(
        val merchant: String,
        val categoryName: String,
        val color: String = "#0F9D84",
    )

    data class AcceptSubscriptionRequest(
        val name: String,
        val amount: BigDecimal,
        val categoryId: UUID,
        val creditCardId: UUID,
        val chargeDay: Int,
    )
}
