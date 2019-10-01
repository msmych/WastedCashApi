package wasted.group

import org.springframework.data.mongodb.repository.MongoRepository

interface GroupRepository : MongoRepository<Group, Long> {

  fun findAllByMonthlyReportTrue(): List<Group>
}
