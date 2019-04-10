package wasted.expense

import org.springframework.data.mongodb.repository.MongoRepository

interface ExpenseRepository : MongoRepository<Expense, Long> {

    fun findByGroupIdAndTelegramMessageId(groupId: Long, telegramMessageId: Int): Expense?
    fun deleteByGroupIdAndTelegramMessageId(groupId: Long, telegramMessageId: Int)
}