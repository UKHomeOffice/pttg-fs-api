package uk.gov.digital.ho.proving.financialstatus.audit

import java.util.UUID

import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
trait ApiAuditRepository extends MongoRepository[AuditEvent, UUID] {

}
