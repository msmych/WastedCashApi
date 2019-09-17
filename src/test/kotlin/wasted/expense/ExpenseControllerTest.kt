package wasted.expense

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.Customization
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode.LENIENT
import org.skyscreamer.jsonassert.comparator.CustomComparator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.util.NestedServletException
import wasted.expense.Expense.Category.OTHER
import wasted.expense.Expense.Category.SHOPPING
import wasted.expense.ExpenseController.PostExpenseRequest
import wasted.expense.ExpenseController.PutExpenseRequest
import wasted.mongo.MongoSequenceService
import wasted.token.TokenInterceptor
import wasted.user.User
import wasted.user.UserRepository
import java.util.*

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
  @MockBean
  lateinit var userRepository: UserRepository
  @MockBean
  lateinit var tokenInterceptor: TokenInterceptor

  private val expense = Expense(0, 1, 2, 3, 0, "USD", OTHER)

  @BeforeEach
  fun setUp() {
    whenever(tokenInterceptor.preHandle(any(), any(), any())).thenReturn(true)
    whenever(expenseRepository.findById(expense.id)).thenReturn(Optional.of(expense))
    whenever(userRepository.findById(any())).thenReturn(Optional.of(User(1, arrayListOf("USD"), false)))
  }

  @Test
  fun should_get_expense_by_id() {
    JSONAssert.assertEquals(objectMapper.writeValueAsString(expense),
      mvc.perform(get("/expense/${expense.id}"))
        .andExpect(status().isOk)
        .andReturn().response.contentAsString,
      true)
  }

  @Test
  fun should_get_expense_by_group_id_and_telegram_message_id() {
    whenever(expenseRepository.findByGroupIdAndTelegramMessageId(expense.groupId, expense.telegramMessageId ?: 0))
      .thenReturn(expense)
    JSONAssert.assertEquals(objectMapper.writeValueAsString(expense),
      mvc.perform(get("/expense?groupId=${expense.groupId}&telegramMessageId=${expense.telegramMessageId}"))
        .andExpect(status().isOk)
        .andReturn().response.contentAsString,
      true)
  }

  @Test
  fun should_respond_with_error_if_no_such_expense() {
    whenever(expenseRepository.findByGroupIdAndTelegramMessageId(expense.groupId, expense.telegramMessageId ?: 0))
      .thenReturn(null)
    assertTrue(assertThrows<NestedServletException> {
      mvc.perform(get("/expense?groupId=${expense.groupId}&telegramMessageId=${expense.telegramMessageId}"))
        .andExpect(status().isBadRequest)
    }.cause is NoSuchExpenseException)
  }

  @Test
  fun should_create_expense() {
    whenever(expenseRepository.save(any<Expense>())).then { it.arguments[0] }
    JSONAssert.assertEquals(objectMapper.writeValueAsString(expense),
      mvc.perform(post("/expense")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(PostExpenseRequest(1, 2, 3, "USD"))))
        .andExpect(status().isOk)
        .andReturn().response.contentAsString,
      CustomComparator(LENIENT, Customization("date") { _, _ -> true }))
    verify(mongoSequenceService).next(any())
    verify(expenseRepository).save(any<Expense>())
  }

  @Test
  fun should_update_expense() {
    mvc.perform(put("/expense")
      .contentType(APPLICATION_JSON)
      .content(objectMapper.writeValueAsBytes(PutExpenseRequest(0, 1000, "EUR", SHOPPING))))
      .andExpect(status().isOk)
    verify(expenseRepository).save(any<Expense>())
  }
}
