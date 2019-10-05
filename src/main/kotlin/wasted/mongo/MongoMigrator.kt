package wasted.mongo

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import wasted.mongo.MongoMigrator.MigrationStrategy.ALL
import wasted.mongo.MongoMigrator.MigrationStrategy.CURRENT_VERSION
import javax.annotation.PostConstruct

@Component
class MongoMigrator(private val mongoTemplate: MongoTemplate) {

  private val log = LoggerFactory.getLogger(MongoMigrator::class.java)

  @Value("\${migration-strategy}")
  lateinit var migrationStrategy: MigrationStrategy

  private fun manifestValue(key: String): String? {
    return MongoMigrator::class.java.classLoader.getResource("META-INF/MANIFEST.MF")
      ?.readText()
      ?.split("\r\n")
      ?.find { it.startsWith(key) }
      ?.substringAfter(": ")
  }

  @PostConstruct
  fun init() {
    val version = manifestValue("App-Version")
    log.info("Migration strategy: {}, version {}", migrationStrategy, version)
    when (migrationStrategy) {
      CURRENT_VERSION -> migrations[version]?.accept(mongoTemplate)
      ALL -> migrations.values.forEach { it.accept(mongoTemplate) }
      else -> {
      }
    }
  }

  enum class MigrationStrategy {
    NONE, CURRENT_VERSION, ALL
  }
}
