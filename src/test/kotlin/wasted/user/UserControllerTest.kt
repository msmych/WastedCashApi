package wasted.user

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode.LENIENT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import wasted.token.TokenInterceptor
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(UserController::class)
internal class UserControllerTest {

    @Autowired
    lateinit var mvc: MockMvc
    @MockBean
    lateinit var userRepository: UserRepository
    @MockBean
    lateinit var tokenInterceptor: TokenInterceptor

    @BeforeEach
    fun setUp() {
        whenever(tokenInterceptor.preHandle(any(), any(), any())).thenReturn(true)
        whenever(userRepository.findById(1))
                .thenReturn(Optional.of(User(1, arrayListOf("USD", "EUR"), 1234)))
    }

    @Test
    fun savingUser() {
        whenever(userRepository.findById(any())).thenReturn(Optional.empty())
        mvc.perform(post("/user/1"))
                .andExpect(status().isOk)
        verify(userRepository).save(any<User>())
    }

    @Test
    fun gettingUserCurrencies() {
        JSONAssert.assertEquals("[USD, EUR]",
                mvc.perform(get("/user/1/currencies"))
                .andExpect(status().isOk)
                .andReturn()
                .response.contentAsString,
                LENIENT)
    }

    @Test
    fun addingUserCurrency() {
        JSONAssert.assertEquals("[USD, EUR, RUB]",
                mvc.perform(patch("/user/1/currency/rub")
                        .contentType(APPLICATION_JSON))
                        .andExpect(status().isOk)
                        .andReturn().response.contentAsString,
                LENIENT)
    }
}
