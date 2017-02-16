package uk.gov.digital.ho.proving.financialstatus.audit

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import scala.collection.immutable.Map
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.DeploymentDetails

import static uk.gov.digital.ho.proving.financialstatus.audit.AuditActions.auditEvent

class AuditActionsSpec extends Specification {

    def deploymentConfig = new DeploymentDetails("localhost", "local")

    def 'puts eventId in the data'() {

        given:
        Map<String, String> data = new Map.Map1("", "");

        when:
        def eventId = AuditActions.nextId()
        AuditApplicationEvent e = auditEvent(deploymentConfig, "anonymous", AuditEventType.SEARCH(), eventId, data)

        then:
        e.auditEvent.data.get("eventId") == eventId
    }

    def 'creates data map if it is null'() {

        when:
        def eventId = AuditActions.nextId();
        AuditApplicationEvent e = auditEvent(deploymentConfig, "anonymous", AuditEventType.SEARCH(), eventId, null)

        then:
        e.auditEvent.data != null
    }

    def 'generates event ids'() {
        expect:
        AuditActions.nextId() != AuditActions.nextId()
    }

    def 'adds deploymentName if data is null'() {
        when:
        def eventId = AuditActions.nextId()
        AuditApplicationEvent e = auditEvent(deploymentConfig, "anonymous", AuditEventType.SEARCH(), eventId, null)

        then:
        e.auditEvent.data.get("deploymentName") == "localhost"
    }

    def 'adds deploymentName if data is provided'() {
        given:
        Map<String, String> data = new Map.Map1("", "")

        when:
        def eventId = AuditActions.nextId()
        AuditApplicationEvent e = auditEvent(deploymentConfig, "anonymous", AuditEventType.SEARCH(), eventId, data)

        then:
        e.auditEvent.data.get("deploymentName") == "localhost"
    }

    def 'adds deploymentNamespace if data is null'() {
        when:
        def eventId = AuditActions.nextId()
        AuditApplicationEvent e = auditEvent(deploymentConfig, "anonymous", AuditEventType.SEARCH(), eventId, null)

        then:
        e.auditEvent.data.get("deploymentNamespace") == "local"
    }

    def 'adds deploymentNamespace if data is provided'() {
        given:
        Map<String, String> data = new Map.Map1("", "")

        when:
        def eventId = AuditActions.nextId()
        AuditApplicationEvent e = auditEvent(deploymentConfig, "anonymous", AuditEventType.SEARCH(), eventId, data)

        then:
        e.auditEvent.data.get("deploymentNamespace") == "local"
    }
}
