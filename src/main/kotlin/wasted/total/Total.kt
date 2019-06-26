package wasted.total

import wasted.expense.Expense

data class Total(val userId: Int,
                 val amount: Long,
                 val currency: String,
                 val category: Expense.Category)