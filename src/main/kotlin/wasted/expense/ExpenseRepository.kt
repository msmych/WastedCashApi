package wasted.expense

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface ExpenseRepository : MongoRepository<Expense, Long> {

    fun findByGroupIdAndTelegramMessageId(groupId: Long, telegramMessageId: Int): Expense?
    fun findAllByGroupId(groupId: Long): List<Expense>
    fun findAllByGroupIdAndTelegramMessageIdNotNull(groupId: Long): List<Expense>
    fun findAllByGroupIdAndDateGreaterThanEqual(groupId: Long, from: Date): List<Expense>

    fun deleteByGroupIdAndTelegramMessageId(groupId: Long, telegramMessageId: Int)
    fun deleteAllByGroupId(groupId: Long)
    fun deleteAllByGroupIdAndDateLessThan(groupId: Long, until: Date)
}