package wasted.group

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import wasted.token.TokenInterceptor
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(GroupController::class)
internal class GroupControllerTest {

  @Autowired
  lateinit var mvc: MockMvc
  @MockBean
  lateinit var userGroupRepository: UserGroupRepository
  @MockBean
  lateinit var tokenInterceptor: TokenInterceptor

  private val group = UserGroup(1234, monthlyReport = true)

  @BeforeEach
  fun setUp() {
    whenever(tokenInterceptor.preHandle(any(), any(), any())).thenReturn(true)
    whenever(userGroupRepository.findById(1234)).thenReturn(Optional.of(group))
  }

  @Test
  fun should_get_monthly_report() {
    assertTrue(mvc.perform(get("/group/1234/monthly-report"))
      .andExpect(status().isOk)
      .andReturn().response.contentAsString.toBoolean())
    verify(userGroupRepository).findById(1234)
  }

  @Test
  fun should_toggle_monthly_report() {
    whenever(userGroupRepository.findById(1234))
      .thenReturn(Optional.of(UserGroup(1234)))
    assertTrue(mvc.perform(patch("/group/1234/monthly-report"))
      .andExpect(status().isOk)
      .andReturn().response.contentAsString.toBoolean())
    verify(userGroupRepository).findById(1234)
  }
}
