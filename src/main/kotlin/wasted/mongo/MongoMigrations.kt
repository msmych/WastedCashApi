package wasted.mongo

import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import java.util.function.Consumer

private val userAddWhatsNew = Consumer<MongoTemplate> { mongo ->
  mongo.aggregate(Aggregation.newAggregation(AggregationOperation {
    Document("\$addFields", Document(mapOf("whatsNew" to false)))
  }), "user", Document::class.java)
    .mappedResults
    .forEach { mongo.save(it, "user") }
}

val migrations: Map<String, Consumer<MongoTemplate>> = mapOf(
  "0.1.0" to userAddWhatsNew
)