package wasted.mongo

import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class MongoMigrator(private val mongoTemplate: MongoTemplate) {

  private val userAddWhatsNew: Runnable = Runnable {
    mongoTemplate.aggregate(newAggregation(AggregationOperation {
      Document("\$addFields", Document(mapOf("whatsNew" to false)))
    }),
      "user",
      Document::class.java)
      .mappedResults
      .forEach { mongoTemplate.save(it, "user") }
  }

  private val migrations: Map<String, Runnable> = mapOf("0.1.0" to userAddWhatsNew)

  private fun manifestValue(key: String): String? {
    return MongoMigrator::class.java.classLoader.getResource("META-INF/MANIFEST.MF")
      ?.readText()
      ?.split("\r\n")
      ?.find { it.startsWith(key) }
      ?.substringAfter(": ")
  }

  @PostConstruct
  fun init() {
    migrations[manifestValue("App-Version")]?.run()
  }
}
