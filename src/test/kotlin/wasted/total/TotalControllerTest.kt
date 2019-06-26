package wasted.total

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode.LENIENT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import wasted.expense.Expense
import wasted.expense.Expense.Category.FEES
import wasted.expense.Expense.Category.SHOPPING
import wasted.expense.ExpenseRepository
import wasted.token.TokenInterceptor
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(TotalController::class)
internal class TotalControllerTest {

    private val e1 = Expense(1, 2, 3, 4, 1000, "USD", SHOPPING, Date())
    private val e2 = Expense(100, 200, 3, 400, 5000, "RUB", FEES, Date())
    private val e3 = Expense(10, 20, 30, 40, 999, "USD", SHOPPING, Date())

    @Autowired
    lateinit var mvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper
    @MockBean
    lateinit var expenseRepository: ExpenseRepository
    @MockBean
    lateinit var tokenInterceptor: TokenInterceptor

    @BeforeEach
    fun setUp() {
        whenever(tokenInterceptor.preHandle(any(), any(), any())).thenReturn(true)
        whenever(expenseRepository.findAllByGroupId(any())).thenReturn(listOf(e1, e2))
        whenever(expenseRepository.findAllByGroupIdAndDateGreaterThanEqual(any(), any())).thenReturn(listOf(e3))
    }

    @Test
    fun gettingByGroupId() {
        JSONAssert.assertEquals(objectMapper.writeValueAsString(listOf(
                Total(2, 1000, "USD", SHOPPING),
                Total(200, 5000, "RUB", FEES))),
                mvc.perform(get("/total?groupId=3"))
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString,
                LENIENT)
    }

    @Test
    fun gettingRecent() {
        JSONAssert.assertEquals(objectMapper.writeValueAsString(listOf(
                Total(20, 999, "USD", SHOPPING))),
                mvc.perform(get("/total/month?groupId=3"))
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString,
                LENIENT)
    }
}