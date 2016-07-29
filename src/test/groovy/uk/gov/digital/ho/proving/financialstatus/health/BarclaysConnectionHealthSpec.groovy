package uk.gov.digital.ho.proving.financialstatus.health

import cucumber.api.java.Before
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.Status
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class BarclaysConnectionHealthSpec extends Specification {

    def restTemplate
    def response

    @Before
    def setup() {
        restTemplate = Mock(RestTemplate)
    }

    def "should report DOWN when server unreachable"() {

        given:
        BarclaysConnectionHealth healthCheck = new BarclaysConnectionHealth(restTemplate, "")

        and:
        restTemplate.getForEntity(*_) >> new RuntimeException("error")

        when:
        Health result = healthCheck.health()

        then:
        result.getStatus() == Status.DOWN
    }

    def "should report UP when server is responding"() {

        given:
        BarclaysConnectionHealth healthCheck = new BarclaysConnectionHealth(restTemplate, "")

        and:
        restTemplate.getForEntity(*_) >> ResponseEntity.status(HttpStatus.OK).build()

        when:
        Health result = healthCheck.health()

        then:
        result.getStatus() == Status.UP
    }
}
