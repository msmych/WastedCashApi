package wasted.total

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import wasted.expense.Expense.Category
import wasted.expense.ExpenseRepository

@RestController
@RequestMapping("total")
class TotalController(val expenseRepository: ExpenseRepository) {

    @GetMapping
    fun getTotal(@RequestParam groupId: Long): List<Total> {
        return expenseRepository.findAllByGroupId(groupId)
                .map { Total(it.userId, it.amount, it.currency, it.category) }
    }

    data class Total(val userId: Int,
                     val amount: Long,
                     val currency: String,
                     val category: Category)
}