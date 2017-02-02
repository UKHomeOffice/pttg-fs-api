package uk.gov.digital.ho.proving.financialstatus.audit

import com.mongodb.MongoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories
class AuditConfiguration {
  @Value("${auditing.service}") private val auditingService: String = null

  @Value("${auditing.databasename}") private val auditingDatabaseName = "auditing"

  @Bean
  def mongoOperations(): MongoOperations = new MongoTemplate(mongoDbFactory())

  @Bean
  def mongoDbFactory(): MongoDbFactory = new SimpleMongoDbFactory(mongoClient(), auditingDatabaseName)

  // TODO: Production configuration for Mongo client

  def mongoClient(): MongoClient = new MongoClient(auditingService)

}
