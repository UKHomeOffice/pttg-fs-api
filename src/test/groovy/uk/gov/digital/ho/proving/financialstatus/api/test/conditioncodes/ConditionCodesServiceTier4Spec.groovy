package uk.gov.digital.ho.proving.financialstatus.api.test.conditioncodes

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import scala.None$
import scala.Some
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.ConditionCodesServiceTier4
import uk.gov.digital.ho.proving.financialstatus.api.ConditionCodesServiceTier4$
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.test.tier4.TestUtilsTier4
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventPublisher
import uk.gov.digital.ho.proving.financialstatus.audit.EmbeddedMongoClientConfiguration
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.DeploymentDetails
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain.ApplicantConditionCode
import uk.gov.digital.ho.proving.financialstatus.domain.ChildConditionCode
import uk.gov.digital.ho.proving.financialstatus.domain.ConditionCodesCalculationResult
import uk.gov.digital.ho.proving.financialstatus.domain.ConditionCodesCalculator
import uk.gov.digital.ho.proving.financialstatus.domain.PartnerConditionCode
import uk.gov.digital.ho.proving.financialstatus.domain.UserProfile

import javax.servlet.http.Cookie

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@WebAppConfiguration
@ContextConfiguration(classes = [ ServiceConfiguration.class, EmbeddedMongoClientConfiguration.class ])
class ConditionCodesServiceTier4Spec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    AuditEventPublisher auditorMock = Mock()
    Authentication authenticatorMock = Mock()
    ConditionCodesCalculator conditionCodesCalculatorMock = Mock()

    def conditionCodesTier4Service = new ConditionCodesServiceTier4(
        auditorMock,
        authenticatorMock,
        new DeploymentDetails("localhost", "local"),
        conditionCodesCalculatorMock
    )

    MockMvc mockMvc = standaloneSetup(conditionCodesTier4Service)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(), serviceMessages))
        .build()

    String conditionCodeApiUrl() {
        def accessScalaSingletonObject = ConditionCodesServiceTier4$.MODULE$
        accessScalaSingletonObject.ConditionCodeTier4Url()
    }

    def 'A well-formed successful request gets a 200 response'() {
        given:

        def studentType = 'Des'
        def applicationtype = 'T4main'
        def dependants = 2
        stubconditionCodesCalculatorResult()

        when:

        def request = get(conditionCodeApiUrl())
            .param('studentType', studentType)
            .param('applicationtype', applicationtype)
            .param('dependants', dependants.toString())
        def response = mockMvc.perform(request)

        then:

        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isOk())
    }

    def 'A well-formed successful request gets a response containing the calculated condition codes in the response body'() {
        given:

        def expectedApplicantConditionCode = 'a'
        def expectedPartnerConditionCode = 'b'
        def expectedChildConditionCode = 'c'

        def studentType = 'Des'
        def applicationtype = 'T4main'
        def dependants = 2
        stubconditionCodesCalculatorResult()

        when:

        def request = get(conditionCodeApiUrl())
            .param('studentType', studentType)
            .param('applicationtype', applicationtype)
            .param('dependants', dependants.toString())
        def response = mockMvc.perform(request)

        then:



        response.andDo(MockMvcResultHandlers.print())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.applicantConditionCode == expectedApplicantConditionCode
        assert jsonContent.partnerConditionCode == expectedPartnerConditionCode
        assert jsonContent.childConditionCode == expectedChildConditionCode

    }

    def 'A request should authenticate using the supplied Keycloak Proxy cookie token'() {
        given:

        def studentType = 'Des'
        def applicationtype = 'T4main'
        def dependants = 2
        def keyCloakCookieToken = '1234567890'
        stubconditionCodesCalculatorResult()

        when:

        def request = get(conditionCodeApiUrl())
            .cookie(new Cookie('kc-access', keyCloakCookieToken))
            .param('studentType', studentType)
            .param('applicationtype', applicationtype)
            .param('dependants', dependants.toString())
        mockMvc.perform(request)

        then:

        1 * authenticatorMock.getUserProfileFromToken(keyCloakCookieToken) >> None$.MODULE$
    }

    def 'Request and response should both be reported to the auditor'() {
        given:

        def studentType = 'Des'
        def applicationtype = 'T4main'
        def dependants = 2
        def keyCloakCookieToken = "1234567890"
        stubAuthenticationArbitraryUserProfile()
        stubconditionCodesCalculatorResult()

        when:

        def request = get(conditionCodeApiUrl())
            .cookie(new Cookie('kc-access', keyCloakCookieToken))
            .param('studentType', studentType)
            .param('applicationtype', applicationtype)
            .param('dependants', dependants.toString())
        def response = mockMvc.perform(request)

        then:

        response.andDo(MockMvcResultHandlers.print())
        2 * auditorMock.publishEvent(_)
    }

    private void stubAuthenticationArbitraryUserProfile() {
        authenticatorMock.getUserProfileFromToken(_) >> new Some(new UserProfile("", "", "", ""))
    }

    private void stubconditionCodesCalculatorResult() {
        conditionCodesCalculatorMock.calculateConditionCodes() >> new ConditionCodesCalculationResult(
            new Some<>(new ApplicantConditionCode("a")),
            new Some<>(new PartnerConditionCode("b")),
            new Some<>(new ChildConditionCode("c")))
    }

}
