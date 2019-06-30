package wasted.total

import org.springframework.web.bind.annotation.*
import wasted.expense.Expense
import wasted.expense.ExpenseRepository
import java.time.ZonedDateTime
import java.util.*

@RestController
@RequestMapping("total")
class TotalController(val expenseRepository: ExpenseRepository) {

    @GetMapping
    fun getTotal(@RequestParam groupId: Long): List<Total> {
        return toTotalList(expenseRepository.findAllByGroupId(groupId))
    }

    private fun toTotalList(expenses: List<Expense>): List<Total> {
        return expenses.groupBy { it.currency }
                .map { cur ->
                    cur.value.groupBy { it.category }
                            .map { cat -> cat.value
                                    .map {
                                        Total(it.userId, it.amount, cur.key, cat.key)
                                    }.reduce { acc, total ->
                                        Total(total.userId, acc.amount + total.amount, total.currency, total.category)
                                    }
                            }
                }.flatten()
    }

    @GetMapping("{period}")
    fun getRecentTotal(@PathVariable period: String, @RequestParam groupId: Long): List<Total> {
        val from = Date.from(when (period) {
            "month" -> ZonedDateTime.now().withDayOfMonth(1)
            else -> throw IllegalArgumentException()
        }
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .toInstant())
        return toTotalList(expenseRepository.findAllByGroupIdAndDateGreaterThanEqual(groupId, from))
    }
}