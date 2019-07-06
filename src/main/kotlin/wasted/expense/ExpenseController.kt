package wasted.expense

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import wasted.expense.Expense.Category.OTHER
import wasted.expense.ExpenseController.ExpenseRemovalType.ALL
import wasted.mongo.MongoSequenceService
import wasted.user.User
import wasted.user.UserRepository

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

    @GetMapping("telegramMessageIds")
    fun findTelegramMessageIdsByGroupId(@RequestParam groupId: Long): List<Int> {
        return expenseRepository.findAllByGroupIdAndTelegramMessageIdNotNull(groupId)
                .map { it.telegramMessageId ?: throw IllegalArgumentException() }
    }

    @PostMapping
    fun createExpense(@RequestBody request: PostExpenseRequest): Expense {
        log.info("Creating expense {}", request)
        val mayBeUser = userRepository.findById(request.userId)
        val user = with(mayBeUser) {
            if (isPresent) get()
            else userRepository.save(User(request.userId, arrayListOf("USD", "EUR", "RUB")))
        }
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
        }
    }

    enum class ExpenseRemovalType {
        ALL
    }
}