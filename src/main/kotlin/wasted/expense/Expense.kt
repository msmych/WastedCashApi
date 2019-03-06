package wasted.expense

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class Expense(@Id val id: Long,
                   val userId: Long,
                   val groupId: Long,
                   val amount: Long,
                   val currency: String,
                   val category: Category,
                   val date: Date = Date()) {

    companion object {
        @Transient
        const val SEQUENCE: String = "expense_sequence"
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