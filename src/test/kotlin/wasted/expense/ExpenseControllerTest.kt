package wasted.expense

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import wasted.expense.Expense.Category.OTHER
import wasted.expense.ExpenseController.PostExpenseRequest
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

  @BeforeEach
  fun setUp() {
    whenever(tokenInterceptor.preHandle(any(), any(), any())).thenReturn(true)
    whenever(userRepository.findById(any())).thenReturn(Optional.of(User(1, arrayListOf("USD"), false)))
  }

  @Test
  fun creatingExpense() {
    whenever(expenseRepository.save(any<Expense>())).then { it.arguments[0] }
    JSONAssert.assertEquals(objectMapper.writeValueAsString(
      Expense(0, 1, 2, 3, 0, "USD", OTHER)),
      mvc.perform(post("/expense")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(PostExpenseRequest(1, 2, 3))))
        .andExpect(status().isOk)
        .andReturn().response.contentAsString,
      CustomComparator(LENIENT, Customization("date") { _, _ -> true }))
    verify(mongoSequenceService).next(any())
    verify(expenseRepository).save(any<Expense>())
  }
}
