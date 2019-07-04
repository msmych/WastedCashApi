package wasted.expense

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import wasted.mongo.MongoSequenceService

@RestController
@RequestMapping("expenses/load")
class LoadExpensesController(val objectMapper: ObjectMapper,
                             val mongoSequenceService: MongoSequenceService,
                             val expenseRepository: ExpenseRepository) {

    private val log = LoggerFactory.getLogger(LoadExpensesController::class.java)

    @PostMapping("json")
    fun loadJson(@RequestParam file: MultipartFile) {
        val expenses = objectMapper.readValue(file.bytes, Expenses::class.java).expenses
        log.info("Loading {} expenses", expenses.size)
        expenses
                .forEach { expenseRepository.save(
                        Expense(mongoSequenceService.next(Expense.SEQUENCE),
                                it.userId,
                                it.groupId,
                                null,
                                it.amount,
                                it.currency,
                                it.category,
                                it.date))
                }
    }

    data class Expenses(val expenses: Set<Expense>)
}