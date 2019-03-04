package wasted.expense

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wasted.expense.Expense.Category.GROCERIES
import wasted.mongo.MongoSequenceService

@RestController
@RequestMapping("expense")
class ExpenseController(val expenseRepository: ExpenseRepository,
                        val mongoSequenceService: MongoSequenceService) {

    @PostMapping("/")
    fun insertExpense() {
        expenseRepository.save(Expense(mongoSequenceService.next(Expense.SEQUENCE), 2, 3, 1000, "EUR", GROCERIES))
    }
}