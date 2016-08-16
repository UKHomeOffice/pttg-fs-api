package uk.gov.digital.ho.proving.financialstatus.audit

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent
import spock.lang.Ignore
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.audit.AuditActions

import scala.collection.immutable.Map;

class AuditActionsSpec extends Specification{

    def 'puts eventId in the data' (){

        given:
        Map<String,String> data = new Map.Map1("","");

        when:
        def eventId = AuditActions.nextId()
        AuditApplicationEvent e = AuditActions.auditEvent('SEARCH', eventId, data)

        then:
        e.auditEvent.data.get("eventId") == eventId
    }

    def 'creates data map if it is null' (){

        when:
        def eventId = AuditActions.nextId();
        AuditApplicationEvent e = AuditActions.auditEvent('SEARCH', eventId, null)

        then:
        e.auditEvent.data != null
    }

    def 'generates event ids' (){
        expect:
        AuditActions.nextId() != AuditActions.nextId()
    }
}
