package wasted.group

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("group")
class GroupController(val userGroupRepository: UserGroupRepository) {

  @GetMapping("{id}/monthly-report")
  fun groupMonthlyReport(@PathVariable id: Long): Boolean =
    userGroupRepository.findById(id).map { it.monthlyReport }.orElse(false)

  @PatchMapping("{id}/monthly-report")
  fun toggleMonthlyReport(@PathVariable id: Long): Boolean {
    val group = userGroupRepository.findById(id)
      .orElseThrow { NoSuchUserGroupException(id) }
    userGroupRepository.save(UserGroup(group.id, monthlyReport = !group.monthlyReport))
    return !group.monthlyReport
  }
}
