package wasted.expense

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import wasted.expense.Expense.Category.SHOPPING
import wasted.token.TokenInterceptor

@ExtendWith(SpringExtension::class)
@WebMvcTest(ExpensesController::class)
internal class ExpensesControllerTest {

  @Autowired
  lateinit var mvc: MockMvc
  @Autowired
  lateinit var objectMapper: ObjectMapper
  @MockBean
  lateinit var tokenInterceptor: TokenInterceptor
  @MockBean
  lateinit var expenseRepository: ExpenseRepository

  private val expense = Expense(1, 2, 3, 4, 1000, "EUR", SHOPPING)

  @BeforeEach
  fun setUp() {
    whenever(tokenInterceptor.preHandle(any(), any(), any())).thenReturn(true)
  }

  @Test
  fun should_return_last_expenses() {
    val expenses = listOf(expense, expense, expense)
    whenever(expenseRepository.findTop20ByGroupIdOrderByDateDesc(3))
      .thenReturn(expenses)
    JSONAssert.assertEquals(
      objectMapper.writeValueAsString(expenses),
      mvc.perform(get("/expenses/last/by/3"))
        .andExpect(status().isOk)
        .andReturn().response.contentAsString,
      true)
  }
}
