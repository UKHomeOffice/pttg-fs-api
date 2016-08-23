package uk.gov.digital.ho.proving.financialstatus

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.Appender
import groovy.json.JsonSlurper
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.audit.AuditEventRepository
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Ignore
import spock.lang.Specification
import steps.WireMockTestDataLoader
import uk.gov.digital.ho.proving.financialstatus.api.ServiceRunner
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdResponse
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration

import static java.time.LocalDateTime.now
import static java.time.LocalDateTime.parse
import static java.time.temporal.ChronoUnit.MINUTES

@SpringApplicationConfiguration(classes = [ServiceRunner.class, ServiceConfiguration.class])
@WebAppConfiguration
@IntegrationTest("server.port:0")
@Ignore
class AuditIntegrationSpec extends Specification {

    @Value('${local.server.port}')
    def port

    def path = "/pttg/financialstatusservice/v1/maintenance/threshold?"
    def params = "inLondon=true&studentType=doctorate&accommodationFeesPaid=123.45"
    def url

    RestTemplate restTemplate

    @Autowired
    AuditEventRepository auditEventRepository

    Appender logAppender = Mock()

    def setup() {
        restTemplate = new TestRestTemplate()
        url = "http://localhost:" + port + path + params

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
