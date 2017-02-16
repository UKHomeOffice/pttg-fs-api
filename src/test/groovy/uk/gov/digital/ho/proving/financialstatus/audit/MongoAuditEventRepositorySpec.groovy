package uk.gov.digital.ho.proving.financialstatus.audit

import org.springframework.boot.actuate.audit.AuditEvent
import org.springframework.data.mongodb.core.MongoOperations
import spock.lang.Specification

class MongoAuditEventRepositorySpec extends Specification {

    def "inserts the event into the appropriate collection"() {
        given:
        def mockMongoOperationsInternalToRepository = Mock(MongoOperations)
        def auditCollectionName = "arbitrary_collection_name"
        def underTest = new MongoAuditEventRepository(mockMongoOperationsInternalToRepository, auditCollectionName)

        def auditEvent = createAuditEvent()

        when:
        underTest.add(auditEvent)

        then:
        1 * mockMongoOperationsInternalToRepository.insert(_, auditCollectionName)
    }

    private static createAuditEvent() {
        def data = ["key":"value"]
        new AuditEvent("expectedPrincipal", "type", data)
    }
}
