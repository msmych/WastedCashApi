package wasted.expense

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.Instant.now

@Document
data class Expense(@Id val id: Long,
                   val userId: Int,
                   val groupId: Long,
                   val telegramMessageId: Int?,
                   val amount: Long,
                   val currency: String,
                   val category: Category,
                   val date: Instant = now()) {

    companion object {
        @Transient
        const val SEQUENCE = "expense_sequence"
    }

    enum class Category {
        GROCERIES,
        SHOPPING,
        TRANSPORT,
        HOME,
        FEES,
        ENTERTAINMENT,
        TRAVEL,
        HEALTH,
        CAREER,
        GIFTS,
        SPORT,
        HOBBIES,
        BEAUTY,
        OTHER
    }
}
