package wasted.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(@Id val id: Int,
                val currencies: ArrayList<String>)
