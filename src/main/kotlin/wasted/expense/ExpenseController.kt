package wasted.expense

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wasted.expense.Expense.Category
import wasted.mongo.MongoSequenceService

@RestController
@RequestMapping("expense")
class ExpenseController(val expenseRepository: ExpenseRepository,
                        val mongoSequenceService: MongoSequenceService) {

    @PostMapping("")
    fun insertExpense(@RequestBody request: PostExpenseRequest) {
        expenseRepository.save(Expense(
                mongoSequenceService.next(Expense.SEQUENCE),
                request.userId,
                request.groupId,
                request.amount,
                request.currency,
                request.category))
    }

    data class PostExpenseRequest(val userId: Int,
                                  val groupId: Long,
                                  val amount: Long,
                                  val currency: String,
                                  val category: Category)
}