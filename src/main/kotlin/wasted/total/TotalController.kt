package wasted.total

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wasted.expense.Expense
import wasted.expense.ExpenseRepository
import wasted.total.Total.Type
import wasted.total.Total.Type.ALL
import wasted.total.Total.Type.MONTH
import java.time.ZonedDateTime.now
import java.util.*

@RestController
@RequestMapping("total")
class TotalController(val expenseRepository: ExpenseRepository) {

    @GetMapping("in/{groupId}/type/{type}")
    fun getTotal(@PathVariable groupId: Long, @PathVariable type: Type): List<Total> {
        if (type == ALL)
            return toTotalList(expenseRepository.findAllByGroupId(groupId))
        val from = Date.from(when (type) {
            MONTH -> now().withDayOfMonth(1)
            else -> throw IllegalArgumentException()
        }
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .toInstant())
        return toTotalList(expenseRepository.findAllByGroupIdAndDateGreaterThanEqual(groupId, from))
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
}