package wasted.expense

import org.springframework.data.mongodb.repository.MongoRepository

interface ExpenseRepository : MongoRepository<Expense, Long>