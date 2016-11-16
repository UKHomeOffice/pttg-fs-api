package uk.gov.digital.ho.proving.financialstatus

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import groovy.json.JsonSlurper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.ServiceRunner
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration

import static java.time.LocalDateTime.now
import static java.time.LocalDateTime.parse
import static java.time.temporal.ChronoUnit.MINUTES
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [ServiceRunner.class, ServiceConfiguration.class]
)
class AuditIntegrationSpec extends Specification {

    def path = "/pttg/financialstatusservice/v1/maintenance/threshold?"
    def params = "inLondon=true&studentType=doctorate&accommodationFeesPaid=123.45"
    def url

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    AuditEventRepository auditEventRepository

    Appender logAppender = Mock()

    def setup() {
        url = path + params

        withMockLogAppender()
    }

    def withMockLogAppender() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logAppender);
    }

    def "Searches are audited as INFO level log output with AUDIT prefix in json format and SEARCH type with a timestamp"() {

        given:

        List<LoggingEvent> logEntries = []

        _ * logAppender.doAppend(_) >> { arg ->
            if (arg[0].formattedMessage.contains("AUDIT")) {
                logEntries.add(arg[0])
            }
        }

        when:
        restTemplate.getForEntity(url, String.class)
        LoggingEvent logEntry = logEntries[0]
        def logEntryJson = new JsonSlurper().parseText(logEntry.formattedMessage - "AUDIT:")

        then:

        logEntries.size >= 1
        logEntry.level == Level.INFO

        logEntryJson.principal == "anonymous"
        logEntryJson.type == "SEARCH"
        logEntryJson.data.method == "calculate-threshold"
        logEntryJson.data.accommodationFeesPaid == 123.45

        MINUTES.between(parse(logEntryJson.timestamp), now()) < 1;
    }

}
