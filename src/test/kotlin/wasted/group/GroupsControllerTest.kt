package wasted.group

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
import wasted.token.TokenInterceptor

@ExtendWith(SpringExtension::class)
@WebMvcTest(GroupsController::class)
internal class GroupsControllerTest {

  @Autowired
  lateinit var mvc: MockMvc
  @MockBean
  lateinit var groupRepository: GroupRepository
  @MockBean
  lateinit var tokenInterceptor: TokenInterceptor

  private fun group(id: Long, monthlyReport: Boolean = false): Group = Group(id, arrayListOf(), monthlyReport)

  @BeforeEach
  fun setUp() {
    whenever(tokenInterceptor.preHandle(any(), any(), any())).thenReturn(true)
    whenever(groupRepository.findAllByMonthlyReportTrue())
      .thenReturn(listOf(group(1, true), group(2, true), group(3, true)))
  }

  @Test
  fun getting_monthly_report_ids() {
    JSONAssert.assertEquals("[1,2,3]",
      mvc.perform(get("/groups/monthly-report/ids"))
      .andExpect(status().isOk)
      .andReturn().response.contentAsString,
      LENIENT)
  }
}
