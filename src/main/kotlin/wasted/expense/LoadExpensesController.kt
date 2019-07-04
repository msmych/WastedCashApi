package wasted.expense

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
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
        save(objectMapper.readValue(file.bytes, Expenses::class.java).expenses)
    }

    private fun save(expenses: Set<Expense>) {
        log.info("Loading {} expenses", expenses.size)
        expenses
                .forEach {
                    expenseRepository.save(
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

    @PostMapping("string")
    fun loadString(@RequestBody s: String) {
        save(objectMapper.readValue(s, Expenses::class.java).expenses)
    }

    data class Expenses(val expenses: Set<Expense>)
}