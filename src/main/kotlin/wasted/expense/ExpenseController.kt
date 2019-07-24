package wasted.expense

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import wasted.expense.Expense.Category.OTHER
import wasted.expense.ExpenseController.ExpenseRemovalType.ALL
import wasted.expense.ExpenseController.ExpenseRemovalType.UP_TO_THIS_MONTH
import wasted.mongo.MongoSequenceService
import wasted.user.User
import wasted.user.UserRepository
import java.time.ZonedDateTime.now
import java.util.*

@RestController
@RequestMapping("expense")
class ExpenseController(val expenseRepository: ExpenseRepository,
                        val mongoSequenceService: MongoSequenceService,
                        val userRepository: UserRepository) {

    private val log = LoggerFactory.getLogger(ExpenseController::class.java)

    @GetMapping("{id}")
    fun getExpenseById(@PathVariable id: Long): Expense? {
        return expenseRepository.findById(id).orElse(null)
    }

    @GetMapping
    fun getExpenseByGroupIdAndTelegramMessageId(@RequestParam groupId: Long,
                                                @RequestParam telegramMessageId: Int): Expense {
        return expenseRepository.findByGroupIdAndTelegramMessageId(groupId, telegramMessageId)
                ?: throw NoSuchExpenseException()
    }

    @PostMapping
    fun createExpense(@RequestBody request: PostExpenseRequest): Expense {
        log.info("Creating expense {}", request)
        val user = userRepository.findById(request.userId)
                .orElseGet { userRepository.save(User(request.userId, arrayListOf("USD", "EUR", "RUB"))) }
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

    @PutMapping
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

    @DeleteMapping("{id}")
    fun removeById(@PathVariable id: Long) {
        expenseRepository.deleteById(id)
    }

    @DeleteMapping
    fun removeExpenseByGroupIdAndTelegramMessageId(@RequestParam groupId: Long,
                                                   @RequestParam telegramMessageId: Int) {
        expenseRepository.deleteByGroupIdAndTelegramMessageId(groupId, telegramMessageId)
    }

    @DeleteMapping("/in/{groupId}/type/{type}")
    fun removeByType(@PathVariable groupId: Long, @PathVariable type: ExpenseRemovalType) {
        when (type) {
            ALL -> expenseRepository.deleteAllByGroupId(groupId)
            UP_TO_THIS_MONTH -> expenseRepository.deleteAllByGroupIdAndDateLessThan(
                    groupId,
                    Date.from(now()
                            .withDayOfMonth(1)
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .toInstant()))
        }
    }

    enum class ExpenseRemovalType {
        ALL,
        UP_TO_THIS_MONTH
    }
}