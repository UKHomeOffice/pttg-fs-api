package uk.gov.digital.ho.proving.financialstatus.audit

import com.mongodb.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoClientConfiguration {

  @Value("${auditing.host}") private val auditingHost: String = null
  @Value("${auditing.port}") private val auditingPort: Int = 27017

  // TODO: Production configuration for Mongo client

  @Bean
  def mongoClient(): MongoClient = new MongoClient(auditingHost, auditingPort)

}
