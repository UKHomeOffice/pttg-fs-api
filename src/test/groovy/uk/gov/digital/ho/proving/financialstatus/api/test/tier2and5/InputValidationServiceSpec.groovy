package uk.gov.digital.ho.proving.financialstatus.api.test.tier2and5

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdServiceTier2And5
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.test.tier4.TestUtilsTier4
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventPublisher
import uk.gov.digital.ho.proving.financialstatus.audit.EmbeddedMongoClientConfiguration
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.DeploymentDetails
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain.ApplicantTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.TierChecker
import uk.gov.digital.ho.proving.financialstatus.domain.VariantTypeChecker

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@WebAppConfiguration
@ContextConfiguration(classes = [ ServiceConfiguration.class, EmbeddedMongoClientConfiguration.class ])
class InputValidationServiceSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    AuditEventPublisher mockAuditor = Mock()
    Authentication mockAuthenticator = Mock()
    TierChecker mockTierChecker = Mock()
    ApplicantTypeChecker stubApplicantTypeChecker = new ApplicantTypeChecker("main", "dependant")
    VariantTypeChecker mockVariantTypeChecker = Mock()


    def thresholdService = new ThresholdServiceTier2And5(
                                                        TestUtilsTier2And5.maintenanceThresholdServiceBuilder(),
                                                        mockTierChecker,
                                                        TestUtilsTier2And5.getApplicantTypeChecker(),
                                                        mockVariantTypeChecker,
                                                        serviceMessages,
                                                        mockAuditor,
                                                        mockAuthenticator,
                                                        new DeploymentDetails("localhost", "local")
                                                    )

    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(),
                                                        mockTierChecker,
                                                        stubApplicantTypeChecker,
                                                        mockVariantTypeChecker,
                                                        serviceMessages))
        .build()

    def url = TestUtilsTier2And5.thresholdUrl

    def callApi(applicantType, dependants) {
        def response = mockMvc.perform(
            get(url)
                .param("applicantType", applicantType)
                .param("dependants", dependants.toString())
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Tier 2/5 Applicant types validation"() {

        expect:
        def response = callApi(applicantType, 0)
        response.andExpect(status().is(httpStatus))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == statusMessage

        where:
        applicantType || httpStatus || statusMessage
        "main"        || 200        || "OK"
        "MAIN"        || 200        || "OK"
        "MAin"        || 200        || "OK"
        "dependant"   || 200        || "OK"
        "DEPENDANT"   || 200        || "OK"
        "depEnDANt"   || 200        || "OK"
    }

    def "Tier 2/5 Applicant types validation failures"() {

        when:
        stubApplicantTypeChecker.values()

        then:
        "main,dependant"

        expect:
        def response = callApi(applicantType, 0)
        response.andExpect(status().is(httpStatus))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == statusMessage

        where:
        applicantType || httpStatus || statusMessage
        "rubbish"     || 400        || "Parameter error: Invalid applicantType, must be one of [main,dependant]"
        ""            || 400        || "Parameter error: Invalid applicantType, must be one of [main,dependant]"
    }

    def "Tier 2/5 Input validation for dependants"() {
        expect:
        def response = callApi(applicantType, dependants)
        response.andExpect(status().is(httpStatus))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == statusMessage

        where:
        applicantType | dependants || httpStatus || statusMessage
        "main"        | 0          || 200        || "OK"
        "main"        | 1          || 200        || "OK"
        "dependant"   | 0          || 200        || "OK"
        "dependant"   | 1          || 200        || "OK"
        "dependant"   | 2          || 200        || "OK"

    }

    def "Tier 2/5 Input validation for dependants failures"() {
        expect:
        def response = callApi(applicantType, dependants)
        response.andExpect(status().is(httpStatus))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == statusMessage

        where:
        applicantType | dependants || httpStatus || statusMessage
        "main"        | -1         || 400        || "Parameter error: Invalid number of dependants: -1"
        "dependant"   | -1         || 400        || "Parameter error: Invalid number of dependants: -1"
        "dependant"   | 0          || 200        || "OK"

    }


}
