package uk.gov.digital.ho.proving.financialstatus.audit

import java.util.UUID

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent

import scala.collection.JavaConverters._

object AuditActions {

  def nextId: UUID = {
    UUID.randomUUID
  }

  def auditEvent(auditEventType: AuditEventType.Value, id: UUID, data: Map[String, AnyRef]): AuditApplicationEvent = {

    def auditData: Map[String, AnyRef] = data match {
      case null =>
        Map("eventId" -> id)
      case default =>
        Map("eventId" -> id) ++ data
    }

    new AuditApplicationEvent(getPrincipal, auditEventType.toString, auditData.asJava)
  }

  private def getPrincipal: String = "anonymous"

}
