package wasted.expense

import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface ExpenseRepository : MongoRepository<Expense, Long> {

  fun findByGroupIdAndTelegramMessageId(groupId: Long, telegramMessageId: Int): Expense?
  fun findAllByGroupId(groupId: Long): List<Expense>
  fun findAllByGroupIdAndDateGreaterThanEqual(groupId: Long, from: Instant): List<Expense>
  fun findAllByDateGreaterThanEqual(from: Instant): List<Expense>
  fun findTop20ByGroupIdOrderByDateDesc(groupId: Long): List<Expense>

  fun deleteByGroupIdAndTelegramMessageId(groupId: Long, telegramMessageId: Int)
  fun deleteAllByGroupId(groupId: Long)
  fun deleteAllByGroupIdAndDateLessThan(groupId: Long, until: Instant)
}
