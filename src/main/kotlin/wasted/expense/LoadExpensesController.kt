package wasted.expense

import com.fasterxml.jackson.databind.ObjectMapper
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

    @PostMapping("json")
    fun loadJson(@RequestParam file: MultipartFile) {
        val expenses = objectMapper.readValue(file.bytes, Expenses::class.java)
                .expenses.forEach { expenseRepository.save(
                Expense(mongoSequenceService.next("expense"),
                        it.userId,
                        it.groupId,
                        it.telegramMessageId,
                        it.amount,
                        it.currency,
                        it.category,
                        it.date))
        }

    }

    data class Expenses(val expenses: Set<Expense>)
}