package uk.gov.digital.ho.proving.financialstatus.api.test.conditioncodes

import cats.data.Validated
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
import uk.gov.digital.ho.proving.financialstatus.domain.ApplicantTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.CourseTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.TierChecker
import uk.gov.digital.ho.proving.financialstatus.domain.VariantTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.ApplicantConditionCode
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.ChildConditionCode
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.ConditionCodesCalculationResult
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.ConditionCodesCalculator
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.ConditionCodesCalculatorProvider
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.PartnerConditionCode
import uk.gov.digital.ho.proving.financialstatus.domain.StudentTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.UserProfile

import javax.servlet.http.Cookie

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@WebAppConfiguration
@ContextConfiguration(classes = [ ServiceConfiguration.class, EmbeddedMongoClientConfiguration.class ])
class ConditionCodesServiceTier4Spec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    AuditEventPublisher mockAuditorMock = Mock()
    Authentication mockAuthenticatorMock = Mock()
    TierChecker mockTierChecker = Mock()
    ApplicantTypeChecker mockApplicantTypeChecker = Mock()
    VariantTypeChecker mockVariantTypeChecker = Mock()

    ConditionCodesCalculatorProvider conditionCodesCalculatorProviderMock = Mock()
    ConditionCodesCalculator conditionCodesCalculatorMock = Mock()

    def conditionCodesTier4Service = new ConditionCodesServiceTier4(
        mockAuditorMock,
        mockAuthenticatorMock,
        new DeploymentDetails("localhost", "local"),
        conditionCodesCalculatorProviderMock,
        new StudentTypeChecker("des", "general", "pgdd", "suso"),
        new CourseTypeChecker("main", "pre-sessional", "below-degree"),
        serviceMessages
    )

    MockMvc mockMvc = standaloneSetup(conditionCodesTier4Service)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(),
                                                        mockTierChecker,
                                                        mockApplicantTypeChecker,
                                                        mockVariantTypeChecker,
                                                        serviceMessages))
        .build()

    String conditionCodeApiUrl() {
        def accessScalaSingletonObject = ConditionCodesServiceTier4$.MODULE$
        accessScalaSingletonObject.ConditionCodeTier4Url()
    }

    def 'A well-formed successful request gets a 200 response'() {
        given:

        def studentType = 'Des'
        def dependantsOnly = 'false'
        def dependants = 2
        stubconditionCodesCalculatorResult()

        when:

        def request = get(conditionCodeApiUrl())
            .param('studentType', studentType)
            .param('dependantsOnly', dependantsOnly)
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
        def dependantsOnly = 'false'
        def dependants = 2
        stubconditionCodesCalculatorResult()

        when:

        def request = get(conditionCodeApiUrl())
            .param('studentType', studentType)
            .param('dependantsOnly', dependantsOnly)
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
        def dependantsOnly = 'false'
        def dependants = 2
        def keyCloakCookieToken = '1234567890'
        stubconditionCodesCalculatorResult()

        when:

        def request = get(conditionCodeApiUrl())
            .cookie(new Cookie('kc-access', keyCloakCookieToken))
            .param('studentType', studentType)
            .param('dependantsOnly', dependantsOnly)
            .param('dependants', dependants.toString())
        mockMvc.perform(request)

        then:

        1 * mockAuthenticatorMock.getUserProfileFromToken(keyCloakCookieToken) >> None$.MODULE$
    }

    def 'Request and response should both be reported to the auditor'() {
        given:

        def studentType = 'Des'
        def dependantsOnly = 'true'
        def dependants = 2
        def keyCloakCookieToken = "1234567890"
        stubAuthenticationArbitraryUserProfile()
        stubconditionCodesCalculatorResult()

        when:

        def request = get(conditionCodeApiUrl())
            .cookie(new Cookie('kc-access', keyCloakCookieToken))
            .param('studentType', studentType)
            .param('dependantsOnly', dependantsOnly)
            .param('dependants', dependants.toString())
        def response = mockMvc.perform(request)

        then:

        response.andDo(MockMvcResultHandlers.print())
        2 * mockAuditorMock.publishEvent(_)
    }

    private void stubAuthenticationArbitraryUserProfile() {
        mockAuthenticatorMock.getUserProfileFromToken(_) >> new Some(new UserProfile("", "", "", ""))
    }

    private void stubconditionCodesCalculatorResult() {
        conditionCodesCalculatorProviderMock.provide(_) >> new Validated.Valid(conditionCodesCalculatorMock)
        conditionCodesCalculatorMock.calculateConditionCodes(_, _, _, _, _, _) >> new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<>(new ApplicantConditionCode("a")),
            new Some<>(new PartnerConditionCode("b")),
            new Some<>(new ChildConditionCode("c"))
        ))
    }

}
