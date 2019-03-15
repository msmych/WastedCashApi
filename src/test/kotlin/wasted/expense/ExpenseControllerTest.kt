package wasted.expense

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import wasted.expense.Expense.Category.GROCERIES
import wasted.expense.ExpenseController.PostExpenseRequest
import wasted.mongo.MongoSequenceService

@ExtendWith(SpringExtension::class)
@WebMvcTest(ExpenseController::class)
internal class ExpenseControllerTest {

    @Autowired
    lateinit var mvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper
    @MockBean
    lateinit var expenseRepository: ExpenseRepository
    @MockBean
    lateinit var mongoSequenceService: MongoSequenceService

    @Test fun savingExpense() {
        mvc.perform(post("/expense")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        PostExpenseRequest(1, 2, 1000, "USD", GROCERIES))))
                .andExpect(status().isOk)
        verify(mongoSequenceService).next(any())
        verify(expenseRepository).save(any<Expense>())
    }
}