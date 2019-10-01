package wasted.group

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("groups")
class GroupsController(val groupRepository: GroupRepository) {

  @GetMapping("monthly-report/ids")
  fun monthlyReportGroupsIds(): List<Long> =
    groupRepository.findAllByMonthlyReportTrue().map { it.id }
}
