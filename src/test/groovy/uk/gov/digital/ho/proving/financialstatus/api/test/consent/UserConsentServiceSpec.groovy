package uk.gov.digital.ho.proving.financialstatus.api.test.consent

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.UserConsentService
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.test.tier4.TestUtilsTier4
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventPublisher
import uk.gov.digital.ho.proving.financialstatus.audit.EmbeddedMongoClientConfiguration
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.DeploymentDetails
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.bank.BarclaysBankService
import uk.gov.digital.ho.proving.financialstatus.domain.*

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@WebAppConfiguration
@ContextConfiguration(classes = [ ServiceConfiguration.class, EmbeddedMongoClientConfiguration.class ])
class UserConsentServiceSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    def mockBarclaysBankService = Mock(BarclaysBankService)

    AuditEventPublisher mockAuditor = Mock()
    Authentication mockAuthenticator = Mock()
    TierChecker mockTierChecker = Mock()
    ApplicantTypeChecker mockApplicantTypeChecker = Mock()
    VariantTypeChecker mockVariantTypeChecker = Mock()

    def userConsentService = new UserConsentService(new UserConsentStatusChecker(mockBarclaysBankService),
        serviceMessages, mockAuditor, mockAuthenticator, new DeploymentDetails("localhost", "local")
    )

    MockMvc mockMvc = standaloneSetup(userConsentService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(),
                                                        mockTierChecker,
                                                        mockApplicantTypeChecker,
                                                        mockVariantTypeChecker,
                                                        serviceMessages))
        .build()

    def url = ConsentUtils.consentUrl

    def callApi(sortCode, accountNumber, dob) {
        def response = mockMvc.perform(
            get(String.format(url, sortCode, accountNumber))
                .param("dob", dob.toString())
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Consent initiated"() {

        def sortCode = "010616"
        def accountNumber = "00005000"
        def dob = LocalDate.of(2000,1,1)

        1 * mockBarclaysBankService.checkUserConsent(_, _, _) >> new UserConsent(sortCode, accountNumber, dob.toString(), new UserConsentResult("INITIATED", "INITIATED"))

        expect:
        def response = callApi(sortCode,accountNumber,dob)
        response.andExpect(status().is(200))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.consent == "INITIATED"

    }

    def "Consent pending"() {

        def sortCode = "010616"
        def accountNumber = "00005000"
        def dob = LocalDate.of(2000,1,1)

        1 * mockBarclaysBankService.checkUserConsent(_, _, _) >> new UserConsent(sortCode, accountNumber, dob.toString(), new UserConsentResult("PENDING", "PENDING"))

        expect:
        def response = callApi(sortCode,accountNumber,dob)
        response.andExpect(status().is(200))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.consent == "PENDING"

    }

    def "Consent success"() {

        def sortCode = "010616"
        def accountNumber = "00005000"
        def dob = LocalDate.of(2000,1,1)

        1 * mockBarclaysBankService.checkUserConsent(_, _, _) >> new UserConsent(sortCode, accountNumber, dob.toString(), new UserConsentResult("SUCCESS", "SUCCESS"))

        expect:
        def response = callApi(sortCode,accountNumber,dob)
        response.andExpect(status().is(200))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.consent == "SUCCESS"

    }

    def "Consent failure"() {

        def sortCode = "010616"
        def accountNumber = "00005000"
        def dob = LocalDate.of(2000,1,1)

        1 * mockBarclaysBankService.checkUserConsent(_, _, _) >> new UserConsent(sortCode, accountNumber, dob.toString(), new UserConsentResult("FAILURE", "FAILURE"))

        expect:
        def response = callApi(sortCode,accountNumber,dob)
        response.andExpect(status().is(200))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.consent == "FAILURE"

    }

    def "Consent invalid"() {

        def sortCode = "010616"
        def accountNumber = "00005000"
        def dob = LocalDate.of(2000,1,1)

        1 * mockBarclaysBankService.checkUserConsent(_, _, _) >> new UserConsent(sortCode, accountNumber, dob.toString(), new UserConsentResult("INVALID", "INVALID"))

        expect:
        def response = callApi(sortCode,accountNumber,dob)
        response.andExpect(status().is(200))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.consent == "INVALID"

    }


}
