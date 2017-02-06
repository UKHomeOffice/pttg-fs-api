package uk.gov.digital.ho.proving.financialstatus.audit.configuration

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.ServerAddress
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoClientConfiguration {

  @Value("${auditing.mongodb.host}") private val auditingHost: String = "localhost"
  @Value("${auditing.mongodb.port}") private val auditingPort: Int = 27017
  @Value("${auditing.mongodb.timeout.ms}") private val auditingMongoTimeoutMs: Int = 30000

  @Bean
  def mongoClient(): MongoClient =
    new MongoClient(
      new ServerAddress(auditingHost, auditingPort),
      MongoClientOptions.builder()
        .connectTimeout(auditingMongoTimeoutMs)
        .serverSelectionTimeout(auditingMongoTimeoutMs)
        .build())

}
