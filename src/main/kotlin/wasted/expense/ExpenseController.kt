package wasted.expense

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import wasted.expense.Expense.Category.OTHER
import wasted.mongo.MongoSequenceService
import wasted.user.User
import wasted.user.UserRepository
import java.lang.IllegalArgumentException

@RestController
@RequestMapping("expense")
class ExpenseController(val expenseRepository: ExpenseRepository,
                        val mongoSequenceService: MongoSequenceService,
                        val userRepository: UserRepository) {

    private val log = LoggerFactory.getLogger(ExpenseController::class.java)

    @GetMapping("")
    fun getExpenseByGroupIdAndTelegramMessageId(@RequestParam groupId: Long,
                                                @RequestParam telegramMessageId: Int): Expense {
        return expenseRepository.findByGroupIdAndTelegramMessageId(groupId, telegramMessageId)
                ?: throw NoSuchExpenseException()
    }

    @PostMapping("")
    fun createExpense(@RequestBody request: PostExpenseRequest): Expense {
        log.info("Creating expense {}", request)
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
    fun updateExpense(@RequestBody request: PutExpenseRequest) {
        log.info("Updating expense {}", request)
        val expense = expenseRepository.findById(request.id)
                .orElseThrow { NoSuchExpenseException() }
        expenseRepository.save(Expense(
                expense.id,
                expense.userId,
                expense.groupId,
                expense.telegramMessageId,
                request.amount,
                request.currency,
                request.category,
                expense.date))
    }

    data class PutExpenseRequest(val id: Long,
                                 val amount: Long,
                                 val currency: String,
                                 val category: Expense.Category)

    @DeleteMapping("")
    fun removeExpenseByGroupIdAndTelegramMessageId(@RequestParam groupId: Long,
                                                   @RequestParam telegramMessageId: Int) {
        expenseRepository.deleteByGroupIdAndTelegramMessageId(groupId, telegramMessageId)
    }
}