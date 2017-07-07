package uk.gov.digital.ho.proving.financialstatus.api.test.tier2and5

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import spock.lang.Unroll
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
class ApplicantMaintenanceThresholdServiceSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    AuditEventPublisher mockAuditor = Mock()
    Authentication mockAuthenticator = Mock()
    TierChecker mockTierChecker = Mock()
    ApplicantTypeChecker mockApplicantTypeChecker = Mock()
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
                                                        mockApplicantTypeChecker,
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

    def callApi(applicantType) {
        def response = mockMvc.perform(
            get(url)
                .param("applicantType", applicantType)
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }


    @Unroll
    def "Tier 2/5 Doctorate - Check #applicantType applicants with #dependants dependants then threshold is #threshold"() {
        expect:
        def response = callApi(applicantType, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold

        where:
        applicantType | dependants || threshold
        "main"        | 0          || 945
        "main"        | 1          || 1575
        "main"        | 2          || 2205
        "main"        | 3          || 2835
        "main"        | 4          || 3465
        "main"        | 5          || 4095
        "main"        | 6          || 4725
        "main"        | 7          || 5355
        "main"        | 8          || 5985
        "main"        | 9          || 6615
        "main"        | 10         || 7245
        "main"        | 11         || 7875
        "main"        | 12         || 8505
        "main"        | 13         || 9135
        "main"        | 14         || 9765
        "main"        | 15         || 10395
        "main"        | 16         || 11025
        "main"        | 17         || 11655
        "main"        | 18         || 12285
        "main"        | 19         || 12915
        "main"        | 20         || 13545

    }

    @Unroll
    def "Tier 2/5 Doctorate - Check main dependant as applicant"() {
        expect:
        def response = callApi(applicantType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold

        where:
        applicantType || threshold
        "dependant"   || 0
    }

}
