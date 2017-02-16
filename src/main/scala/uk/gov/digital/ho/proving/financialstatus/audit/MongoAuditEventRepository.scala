package uk.gov.digital.ho.proving.financialstatus.audit

import java.util
import java.util.Date

import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.data.mongodb.core.MongoOperations

import scala.collection.JavaConverters._

class MongoAuditEventRepository (mongoOperations: MongoOperations,
                                 auditCollectionName: String) extends AuditEventRepository with LoggingAuditEventBsonMapper {

  // find methods are not required
  override def find(after: Date): util.List[AuditEvent] = List.empty.asJava

  override def find(principal: String, after: Date): util.List[AuditEvent] = List.empty.asJava

  override def find(principal: String, after: Date, `type`: String): util.List[AuditEvent] = List.empty.asJava

  override def add(event: AuditEvent): Unit = {
    val document = bsonOf(event)
    mongoOperations.insert(document, auditCollectionName)
  }

}

