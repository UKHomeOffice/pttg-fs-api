package uk.gov.digital.ho.proving.financialstatus.audit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

trait AuditEventPublisher {
  def publishEvent(event: ApplicationEvent): Unit
}
@Service
class AuditEventPublisherImpl @Autowired()(applicationEventPublisher: ApplicationEventPublisher) extends AuditEventPublisher {

  override def publishEvent(event: ApplicationEvent): Unit = applicationEventPublisher.publishEvent(event)

}
