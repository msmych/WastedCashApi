package wasted.expense

import org.springframework.web.bind.annotation.*
import wasted.expense.Expense.Category.OTHER
import wasted.mongo.MongoSequenceService
import wasted.user.User
import wasted.user.UserRepository

@RestController
@RequestMapping("expense")
class ExpenseController(val expenseRepository: ExpenseRepository,
                        val mongoSequenceService: MongoSequenceService,
                        val userRepository: UserRepository) {

    @GetMapping("")
    fun getExpenseByGroupIdAndTelegramMessageId(@RequestParam groupId: Long,
                                                @RequestParam telegramMessageId: Int): Expense {
        return expenseRepository.findByGroupIdAndTelegramMessageId(groupId, telegramMessageId)
                ?: throw NoSuchExpenseException()
    }

    @PostMapping("")
    fun createExpense(@RequestBody request: PostExpenseRequest): Expense {
        val user = userRepository.findById(request.userId)
                .orElse(userRepository.save(User(request.userId, arrayListOf("USD", "EUR", "RUB"))))
        return expenseRepository.save(Expense(
                mongoSequenceService.next(Expense.SEQUENCE),
                request.userId,
                request.groupId,
                request.telegramMessageId,
                request.amount,
                user.currencies[0],
                OTHER))
    }

    data class PostExpenseRequest(val userId: Int,
                                  val groupId: Long,
                                  val telegramMessageId: Int?,
                                  val amount: Long = 0)

    @PutMapping("")
    fun updateExpense(@RequestBody expense: Expense) {
        expenseRepository.save(expense)
    }

    @DeleteMapping("")
    fun removeExpenseByGroupIdAndTelegramMessageId(@RequestParam groupId: Long,
                                                   @RequestParam telegramMessageId: Int) {
        expenseRepository.deleteByGroupIdAndTelegramMessageId(groupId, telegramMessageId)
    }
}