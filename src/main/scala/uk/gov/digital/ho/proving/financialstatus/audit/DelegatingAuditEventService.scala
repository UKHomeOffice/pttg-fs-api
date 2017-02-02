package uk.gov.digital.ho.proving.financialstatus.audit

import java.util
import java.util.Date

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

/**
  * Publishes audit events by delegating to an injected ApiAuditRepository
  */
@Component
class DelegatingAuditEventService @Autowired()(apiAuditRepository: ApiAuditRepository) extends AuditEventRepository {
  override def find(after: Date): util.List[AuditEvent] = List.empty.asJava

  override def find(principal: String, after: Date): util.List[AuditEvent] = List.empty.asJava

  override def find(principal: String, after: Date, `type`: String): util.List[AuditEvent] = List.empty.asJava

  override def add(event: AuditEvent): Unit = apiAuditRepository.insert(event)
}
