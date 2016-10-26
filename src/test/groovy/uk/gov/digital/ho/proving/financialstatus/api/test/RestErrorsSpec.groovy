package uk.gov.digital.ho.proving.financialstatus.api.test

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import steps.WireMockTestDataLoader
import uk.gov.digital.ho.proving.financialstatus.acl.BankService
import uk.gov.digital.ho.proving.financialstatus.api.DailyBalanceService
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.client.HttpUtils
import uk.gov.digital.ho.proving.financialstatus.domain.Account
import uk.gov.digital.ho.proving.financialstatus.domain.AccountDailyBalances
import uk.gov.digital.ho.proving.financialstatus.domain.AccountStatusChecker

import java.time.LocalDate

import static TestUtils.getMessageSource
import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = ServiceConfiguration.class)
class RestErrorsSpec extends Specification {

    def serviceName = "http://localhost:8083"
    def stubPort = 8083
    def stubUrl = "/financialstatus/v1/123456/12345678/balances*"
    def apiUrl = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"
    def verifyUrl = "/financialstatus/v1/123456/12345678/balances.*"

    ServiceMessages serviceMessages = new ServiceMessages(getMessageSource())

    def maxAttempts = 3
    def backoffPeriod = 5

    @Shared
    def restConnectionTimeout = 5000
    def testDataLoader

    @Shared
    def customHttpRequestFactory = new HttpComponentsClientHttpRequestFactory()
    def customRestTemplate = new RestTemplate(customHttpRequestFactory)

    HttpUtils httpUtils = new HttpUtils(customRestTemplate, maxAttempts, backoffPeriod)

    BankService mockBankService = new TestBankService(new ObjectMapper(), httpUtils, serviceName)

    ApplicationEventPublisher auditor = Mock()

    def dailyBalanceService = new DailyBalanceService(new AccountStatusChecker(mockBankService, 28), serviceMessages, auditor)
    MockMvc mockMvc = standaloneSetup(dailyBalanceService).setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter()).build()


    def setupSpec() {
        customHttpRequestFactory.setConnectionRequestTimeout(restConnectionTimeout)
        customHttpRequestFactory.setConnectTimeout(restConnectionTimeout)
        customHttpRequestFactory.setReadTimeout(restConnectionTimeout)
    }

    def cleanupSpec() {
    }

    def setup() {
        testDataLoader = new WireMockTestDataLoader(stubPort)
    }

    def cleanup() {
        testDataLoader?.stop()
    }

    def "check for 0 retry on 3 second delay returning 404 status code"() {
        // Try once only when we get a 404 error before failing
        given:
        testDataLoader.withDelayedAndStatusResponse(stubUrl, 3, 404)

        when:
        def response = mockMvc.perform(
            get(apiUrl)
                .param("fromDate", "2016-05-13")
                .param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("dob","2000-01-01")
                .param("userId", "user123456")
                .param("accountHolderConsent", "true")


        )
        then:
        response.andExpect(status().is(404))
        verify(1, getRequestedFor(urlMatching(verifyUrl)))

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == "No records for sort code 123456 and account number 12345678"
    }

    def "check for 3 retries on 4 second delay returning 500 status code"() {
        // Try 3 times when we get a 500 error before failing
        given:
        testDataLoader.withDelayedAndStatusResponse(stubUrl, 4, 500)

        when:
        def response = mockMvc.perform(
            get(apiUrl)
                .param("fromDate", "2016-05-13")
                .param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("dob","2000-01-01")
                .param("userId", "user123456")
                .param("accountHolderConsent", "true")

        )
        then:
        response.andExpect(status().isInternalServerError())
        verify(3, getRequestedFor(urlMatching(verifyUrl)))

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == "500 Internal Server Error"
    }

    def "check for 3 retries on 7 second delay returning 200 status code"() {
        // Try the service 3 times but only wait for 5 seconds, the mock is set to return
        // a 200 after 7 seconds, but we should report a connection timeout as the service
        // hasn't responded in time
        given:
        testDataLoader.withDelayedAndStatusResponse(stubUrl, 7, 200)

        when:
        def response = mockMvc.perform(
            get(apiUrl)
                .param("fromDate", "2016-05-13")
                .param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("dob","2000-01-01")
                .param("userId", "user123456")
                .param("accountHolderConsent", "true")

        )
        then:
        response.andExpect(status().isInternalServerError())
        verify(3, getRequestedFor(urlMatching(verifyUrl)))

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == "Connection timeout"
    }


    class TestBankService implements BankService{

        def bankUrl = ""
        ObjectMapper objectMapper
        HttpUtils httpUtils

        TestBankService(ObjectMapper objectMapper, HttpUtils httpUtils, String serviceName){
            bankUrl = "$serviceName/financialstatus/v1"
            this.objectMapper = objectMapper
            this.httpUtils = httpUtils
        }

        @Override
        String bankName() {
            return null
        }

        @Override
        String bankUrl() {
            return null
        }

        @Override
        ObjectMapper objectMapper() {
            return null
        }

        @Override
        AccountDailyBalances fetchAccountDailyBalances(Account account, LocalDate fromDate, LocalDate toDate, LocalDate dob, String userId, boolean accountHolderConsent) {
            def url = buildUrl(account, fromDate, toDate, dob, userId, accountHolderConsent)
            def httpResponse = httpUtils.performRequest(url)
        }

        @Override
        String buildUrl(Account account, LocalDate fromDate, LocalDate toDate, LocalDate dob, String userId, boolean accountHolderConsent) {
            return "$bankUrl/${account.sortCode}/${account.accountNumber}/balances?fromDate=$fromDate&toDate=$toDate&dob=$dob&userId=$userId&accountHolderConsent=$accountHolderConsent"
        }
    }
}
