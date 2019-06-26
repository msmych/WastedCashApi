package wasted.total

import org.springframework.web.bind.annotation.*
import wasted.expense.ExpenseRepository
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@RestController
@RequestMapping("total")
class TotalController(val expenseRepository: ExpenseRepository) {

    @GetMapping
    fun getTotal(@RequestParam groupId: Long): List<Total> {
        return expenseRepository.findAllByGroupId(groupId)
                .map { Total(it.userId, it.amount, it.currency, it.category) }
    }

    @GetMapping("{period}")
    fun getRecentTotal(@PathVariable period: String, @RequestParam groupId: Long): List<Total> {
        val from = Date.from(when (period) {
            "month" -> LocalDateTime.now().withDayOfMonth(1)
            else -> throw IllegalArgumentException()
        }
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .atZone(ZoneId.systemDefault()).toInstant())
        return expenseRepository.findAllByGroupIdAndDateGreaterThanEqual(groupId, from)
                .map { Total(it.userId, it.amount, it.currency, it.category) }
    }
}