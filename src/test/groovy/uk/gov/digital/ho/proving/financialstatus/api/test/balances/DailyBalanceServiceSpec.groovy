package uk.gov.digital.ho.proving.financialstatus.api.test.balances

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.bank.BarclaysBankService
import uk.gov.digital.ho.proving.financialstatus.api.DailyBalanceService
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.test.DataUtils
import uk.gov.digital.ho.proving.financialstatus.api.test.tier4.TestUtilsTier4
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventPublisher
import uk.gov.digital.ho.proving.financialstatus.audit.EmbeddedMongoClientConfiguration
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.DeploymentDetails
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain.AccountStatusChecker

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = [ ServiceConfiguration.class, EmbeddedMongoClientConfiguration.class ])
class DailyBalanceServiceSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    def mockBankService = Mock(BarclaysBankService)

    AuditEventPublisher auditor = Mock()
    Authentication authenticator = Mock()

    def dailyBalanceService = new DailyBalanceService(new AccountStatusChecker(mockBankService, 28), serviceMessages,
        auditor, authenticator, new DeploymentDetails("localhost", "local"))
    MockMvc mockMvc = standaloneSetup(dailyBalanceService).setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter()).build()

    def "daily balance threshold check pass"() {

        given:
        def url = "/pttg/financialstatus/v1/accounts/12-34-56/12345678/dailybalancestatus"
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)
        def dob = LocalDate.of(2000, 1, 1)
        def userId = "user123456"
        def accountHolderConsent = true

        def minimum = new BigDecimal(2500.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def lower = new BigDecimal(2560.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def upper = new BigDecimal(3500.00).setScale(2, BigDecimal.ROUND_HALF_UP)

        1 * mockBankService.fetchAccountDailyBalances(_, _, _, _, _) >> DataUtils.generateRandomBankResponseOK(fromDate, toDate, lower, upper, true, false)

        when:
        def response = mockMvc.perform(
            get(url)
                .param("toDate", toDate.toString())
                .param("minimum", minimum.toString())
                .param("fromDate", fromDate.toString())
                .param("dob", dob.toString())
                .param("userId", userId)
                .param("accountHolderConsent", accountHolderConsent.toString())
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.pass == true
        jsonContent.accountHolderName == "Fred Flintstone"
        jsonContent.account.sortCode == "123456"
        jsonContent.account.accountNumber == "12345678"

    }


    def "daily balance threshold check fail (minimum below threshold)"() {

        given:
        def url = "/pttg/financialstatus/v1/accounts/12-34-56/12345678/dailybalancestatus"

        def lowestIndex = 5
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)
        def dob = LocalDate.of(2000, 1, 1)
        def lowestDate = toDate.minusDays(5)
        def userId = "user123456"
        def accountHolderConsent = true

        def minimum = new BigDecimal(2500.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def lower = new BigDecimal(2660.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def upper = new BigDecimal(3500.00).setScale(2, BigDecimal.ROUND_HALF_UP)

        def lowest = 1800.00


        1 * mockBankService.fetchAccountDailyBalances(_, _, _, _, _) >> DataUtils.generateDailyBalancesForFail(fromDate, toDate, lower, upper, lowest, lowestIndex)

        when:
        def response = mockMvc.perform(
            get(url)
                .param("toDate", toDate.toString())
                .param("minimum", minimum.toString())
                .param("fromDate", fromDate.toString())
                .param("dob", dob.toString())
                .param("userId", userId)
                .param("accountHolderConsent", accountHolderConsent.toString())
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.pass == false
        jsonContent.failureReason.lowestBalanceValue == 1800.00
        jsonContent.failureReason.lowestBalanceDate == lowestDate.toString()
        jsonContent.accountHolderName == "Fred Flintstone"
        jsonContent.account.sortCode == "123456"
        jsonContent.account.accountNumber == "12345678"


    }

    def "daily balance threshold check fail (not enough entries)"() {

        given:
        def url = "/pttg/financialstatus/v1/accounts/12-34-56/12345678/dailybalancestatus"
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(27)
        def dob = LocalDate.of(2000, 1, 1)
        def mockFromDate = toDate.minusDays(26)
        def userId = "user123456"
        def accountHolderConsent = true

        def minimum = new BigDecimal(2500.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def lower = new BigDecimal(2660.23).setScale(2, BigDecimal.ROUND_HALF_UP)
        def upper = new BigDecimal(3500.00).setScale(2, BigDecimal.ROUND_HALF_UP)

        1 * mockBankService.fetchAccountDailyBalances(_, _, _, _, _) >> DataUtils.generateRandomBankResponseOK(mockFromDate, toDate, lower, upper, true, false)

        when:
        def response = mockMvc.perform(
            get(url)
                .param("toDate", toDate.toString())
                .param("minimum", minimum.toString())
                .param("fromDate", fromDate.toString())
                .param("dob", dob.toString())
                .param("userId", userId)
                .param("accountHolderConsent", accountHolderConsent.toString())
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.pass == false
        jsonContent.failureReason.recordCount == 27
        jsonContent.failureReason.lowestBalanceValue == null
        jsonContent.failureReason.lowestBalanceDate == null
        jsonContent.accountHolderName == "Fred Flintstone"
        jsonContent.account.sortCode == "123456"
        jsonContent.account.accountNumber == "12345678"


    }

}
