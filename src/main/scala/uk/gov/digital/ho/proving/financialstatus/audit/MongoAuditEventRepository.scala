package uk.gov.digital.ho.proving.financialstatus.audit

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.data.mongodb.core.MongoOperations

import scala.collection.JavaConverters._

class MongoAuditEventRepository (mongoOperations: MongoOperations,
                                 auditCollectionName: String) extends AuditEventRepository {

  private val mapper: ObjectMapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JavaTimeModule())
  mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
  mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  mapper.enable(SerializationFeature.INDENT_OUTPUT)

  override def find(after: Date): util.List[AuditEvent] = List.empty.asJava

  override def find(principal: String, after: Date): util.List[AuditEvent] = List.empty.asJava

  override def find(principal: String, after: Date, `type`: String): util.List[AuditEvent] = List.empty.asJava

  override def add(event: AuditEvent): Unit = {
    val json = jsonOf(event)
    val document: org.bson.Document = org.bson.Document.parse(json)
    mongoOperations.insert(document, auditCollectionName)
  }

  private def jsonOf(event: AuditEvent): String = mapper.writeValueAsString(event)
}

