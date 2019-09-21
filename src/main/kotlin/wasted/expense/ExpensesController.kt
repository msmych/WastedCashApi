package wasted.expense

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("expenses")
class ExpensesController(val expenseRepository: ExpenseRepository) {

  @GetMapping("last/by/{groupId}")
  fun lastExpenses(@PathVariable("groupId") groupId: Long): List<Expense> {
    return expenseRepository.findTop20ByGroupIdOrderByDateDesc(groupId)
  }
}
