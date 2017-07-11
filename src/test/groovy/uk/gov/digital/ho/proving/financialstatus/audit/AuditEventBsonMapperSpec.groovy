package uk.gov.digital.ho.proving.financialstatus.audit

import org.springframework.boot.actuate.audit.AuditEvent
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils

import java.time.LocalDate
import java.time.ZoneOffset

class AuditEventBsonMapperSpec extends Specification {

    def "Converts an Audit Event to BSON appropriate for storage"() {
        given:
        def underTest = DataUtils.createAuditEventBsonMapper()
        AuditEvent auditEvent = createAuditEvent()

        when:
        def bsonResult = underTest.bsonOf(auditEvent)

        then:
        bsonResult.toString() == expectedAuditEventAsBsonString
    }

    private static String expectedAuditEventAsBsonString =
        "Document{{timestamp=2017-01-01T00:00:00+0000, principal=a_principal, type=a_type, data=Document{{a_key=a_value}}}}"

    private static AuditEvent createAuditEvent() {
        def data = ["a_key": "a_value"]
        def timestamp = Date.from(LocalDate.of(2017, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
        def auditEvent = new AuditEvent(timestamp, "a_principal", "a_type", data)
        auditEvent
    }
}
