package uk.gov.digital.ho.proving.financialstatus.audit

import java.util.UUID

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.DeploymentDetails

import scala.collection.JavaConverters._

object AuditActions {

  def nextId: UUID = {
    UUID.randomUUID
  }

  def auditEvent(deploymentConfig: DeploymentDetails, principal: String, auditEventType: AuditEventType.Value, id: UUID, data: Map[String, AnyRef]): AuditApplicationEvent = {

    val auditData: Map[String, AnyRef] = data match {
      case null =>
        Map("eventId" -> id, "deploymentName" -> deploymentConfig.deploymentName,
          "deploymentNamespace" -> deploymentConfig.deploymentNamespace)
      case default =>
        Map("eventId" -> id, "deploymentName" -> deploymentConfig.deploymentName,
          "deploymentNamespace" -> deploymentConfig.deploymentNamespace) ++ data
    }

    new AuditApplicationEvent(principal, auditEventType.toString, auditData.asJava)
  }

}
