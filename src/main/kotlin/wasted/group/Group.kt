package wasted.group

data class Group(val id: Long,
                 val userIds: ArrayList<Int>,
                 val monthlyReport: Boolean = false)
