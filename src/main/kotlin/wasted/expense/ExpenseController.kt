package wasted.expense

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import wasted.expense.Expense.Category
import wasted.expense.Expense.Category.*
import wasted.mongo.MongoSequenceService
import wasted.user.User
import wasted.user.UserRepository

@RestController
@RequestMapping("expense")
class ExpenseController(val expenseRepository: ExpenseRepository,
                        val mongoSequenceService: MongoSequenceService,
                        val userRepository: UserRepository) {

    @PostMapping("")
    fun createExpense(@RequestBody request: PostExpenseRequest): Expense {
        val user = userRepository.findById(request.userId)
                .orElse(userRepository.save(User(request.userId, arrayListOf("USD", "EUR", "RUB"))))
        return expenseRepository.save(Expense(
                mongoSequenceService.next(Expense.SEQUENCE),
                request.userId,
                request.groupId,
                request.telegramMessageId,
                0,
                user.currencies[0],
                OTHER))
    }

    data class PostExpenseRequest(val userId: Int,
                                  val groupId: Long,
                                  val telegramMessageId: Int?)
}