package uk.gov.digital.ho.proving.financialstatus.audit

import java.util.UUID

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent

import scala.collection.JavaConverters._

object AuditActions {

  def nextId: UUID = {
    UUID.randomUUID
  }

  def auditEvent(principal: String, auditEventType: AuditEventType.Value, id: UUID, data: Map[String, AnyRef]): AuditApplicationEvent = {

    val auditData: Map[String, AnyRef] = data match {
      case null =>
        Map("eventId" -> id)
      case default =>
        Map("eventId" -> id) ++ data
    }

    new AuditApplicationEvent(principal, auditEventType.toString, auditData.asJava)
  }

}
