package uk.gov.digital.ho.proving.financialstatus.api.test.consent

import groovy.json.JsonSlurper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.acl.BankService
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdServiceTier2And5
import uk.gov.digital.ho.proving.financialstatus.api.UserConsentService
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.api.test.tier2and5.TestUtilsTier2And5
import uk.gov.digital.ho.proving.financialstatus.api.test.tier4.TestUtilsTier4
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain.UserConsent
import uk.gov.digital.ho.proving.financialstatus.domain.UserConsentResult
import uk.gov.digital.ho.proving.financialstatus.domain.UserConsentStatusChecker

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@WebAppConfiguration
@ContextConfiguration(classes = ServiceConfiguration.class)
class UserConsentServiceSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    def mockBankService = Mock(BankService)

    ApplicationEventPublisher auditor = Mock()
    Authentication authenticator = Mock()

    def userConsentService = new UserConsentService(new UserConsentStatusChecker(mockBankService),
        serviceMessages, auditor, authenticator
    )

    MockMvc mockMvc = standaloneSetup(userConsentService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(), serviceMessages))
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

        1 * mockBankService.checkUserConsent(_, _, _, _) >> new UserConsent(sortCode, accountNumber, dob.toString(), new UserConsentResult("INITIATED", "INITIATED"))


        expect:
        def response = callApi(sortCode,accountNumber,dob)
        //response.andExpect(status().is(httpStatus))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.sortCode == sortCode
        jsonContent.accountNumber == accountNumber
        jsonContent.consent == "INITIATED"

    }


}
