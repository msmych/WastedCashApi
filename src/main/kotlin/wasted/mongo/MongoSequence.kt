package wasted.mongo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class MongoSequence(@Id val id: String, val sequence: Long)