package wasted.group

import org.springframework.data.mongodb.repository.MongoRepository

interface UserGroupRepository : MongoRepository<UserGroup, Long> {

  fun findAllByMonthlyReportTrue(): List<UserGroup>
}
