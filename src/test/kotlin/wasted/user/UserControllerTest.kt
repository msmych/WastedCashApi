package wasted.user

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
import java.util.Optional.empty

@ExtendWith(SpringExtension::class)
@WebMvcTest(UserController::class)
internal class UserControllerTest {

  @Autowired
  lateinit var mvc: MockMvc
  @MockBean
  lateinit var userRepository: UserRepository
  @MockBean
  lateinit var tokenInterceptor: TokenInterceptor

  private val user = User(1, arrayListOf("USD", "EUR"), true)

  @BeforeEach
  fun setUp() {
    whenever(tokenInterceptor.preHandle(any(), any(), any())).thenReturn(true)
    whenever(userRepository.existsById(1)).thenReturn(true)
    whenever(userRepository.findById(1)).thenReturn(Optional.of(user))
  }

  @Test
  fun should_return_user_not_exists() {
    whenever(userRepository.existsById(1)).thenReturn(false)
    assertFalse(mvc.perform(get("/user/1/exists"))
      .andExpect(status().isOk)
      .andReturn().response.contentAsString.toBoolean())
  }

  @Test
  fun should_return_user_exists() {
    assertTrue(mvc.perform(get("/user/1/exists"))
      .andExpect(status().isOk)
      .andReturn().response.contentAsString.toBoolean())
  }

  @Test
  fun should_save_user() {
    whenever(userRepository.findById(any())).thenReturn(empty())
    mvc.perform(post("/user/1"))
      .andExpect(status().isOk)
    verify(userRepository).save(any<User>())
  }

  @Test
  fun should_get_user_currencies() {
    JSONAssert.assertEquals("[USD, EUR]",
      mvc.perform(get("/user/1/currencies"))
        .andExpect(status().isOk)
        .andReturn()
        .response.contentAsString,
      LENIENT)
  }

  @Test
  fun should_add_user_currency() {
    JSONAssert.assertEquals("[USD, EUR, RUB]",
      mvc.perform(patch("/user/1/currency/rub")
        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk)
        .andReturn().response.contentAsString,
      LENIENT)
  }

  @Test
  fun should_get_whats_new() {
    assertTrue(mvc.perform(get("/user/1/whats-new"))
      .andExpect(status().isOk)
      .andReturn().response.contentAsString.toBoolean())
    verify(userRepository).findById(1)
  }

  @Test
  fun should_toggle_whats_new() {
    whenever(userRepository.findById(1))
      .thenReturn(Optional.of(User(1, arrayListOf("USD", "EUR"), false)))
    assertTrue(mvc.perform(patch("/user/1/whats-new"))
      .andExpect(status().isOk)
      .andReturn().response.contentAsString.toBoolean())
    verify(userRepository).findById(1)
  }
}
