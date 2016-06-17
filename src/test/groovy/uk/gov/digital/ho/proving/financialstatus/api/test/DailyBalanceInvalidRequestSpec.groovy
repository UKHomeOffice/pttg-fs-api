package uk.gov.digital.ho.proving.financialstatus.api.test

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.acl.MockBankService
import uk.gov.digital.ho.proving.financialstatus.api.DailyBalanceService
import uk.gov.digital.ho.proving.financialstatus.api.ServiceConfiguration

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = ServiceConfiguration.class)
class DailyBalanceInvalidRequestSpec extends Specification {

    def mockBankService = Mock(MockBankService)

    def dailyBalanceService = new DailyBalanceService(mockBankService, 28)
    MockMvc mockMvc = standaloneSetup(dailyBalanceService).setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter()).build()

    def invalidSortCode = "Parameter error: Invalid sort code"
    def invalidAccountNumber = "Parameter error: Invalid account number"
    def invalidTotalFunds = "Parameter error: Invalid Total Funds Required"
    def invalidFromDate = "Parameter error: Invalid from date"
    def invalidToDate = "Parameter error: Invalid to date"
    def invalidDateRange = "Parameter error: Invalids dates, from date must be before to date"

    def "daily balance reject invalid sort code (invalid character)"() {

        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456a/12345678/dailybalancestatus"

        // 1 * mockBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.generateRandomBankResponseOK(fromDate, toDate, 2560.23, 3500, true, false)

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "2560.23").param("fromDate", "2016-05-13")
        )

        then:
        // response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidSortCode
    }

    def "daily balance reject invalid sort code (too few numbers)"() {

        given:
        def url = "/pttg/financialstatusservice/v1/accounts/12345/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "2560.23").param("fromDate", "2016-05-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidSortCode
    }

    def "daily balance reject invalid sort code (too many numbers)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/1234567/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "2560.23").param("fromDate", "2016-05-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidSortCode
    }

    def "daily balance reject invalid account number (invalid character)"() {

        given:
        def url = "/pttg/financialstatusservice/v1/accounts/12345/123d5678/dailybalancestatus"

        // 1 * mockBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.generateRandomBankResponseOK(fromDate, toDate, 2560.23, 3500, true, false)

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "2560.23").param("fromDate", "2016-05-13")
        )

        then:
        // response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidAccountNumber
    }

    def "daily balance reject invalid account number (too few numbers)"() {

        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/1234567/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "2560.23").param("fromDate", "2016-05-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidAccountNumber
    }

    def "daily balance reject invalid account number (too many numbers)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/123456789/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "2560.23").param("fromDate", "2016-05-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidAccountNumber
    }

    def "daily balance reject invalid minimum value (below zero)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "-2560.23").param("fromDate", "2016-05-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidTotalFunds
    }

    def "daily balance reject invalid minimum value (equal to zero)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "0").param("fromDate", "2016-05-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidTotalFunds
    }

    // Invalid from date tests

    def "daily balance reject invalid from date (invalid year)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "2560.23").param("fromDate", "2a16-05-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidFromDate
    }

    def "daily balance reject invalid from date (invalid month)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "2560.23").param("fromDate", "2016-15-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidFromDate
    }

    def "daily balance reject invalid from date (invalid day)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09").param("minimum", "2560.23").param("fromDate", "2016-05-43")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidFromDate
    }

    // Invalid to date tests

    def "daily balance reject invalid to date (invalid year)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-06-09").param("minimum", "2560.23").param("toDate", "2a16-05-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidToDate
    }

    def "daily balance reject invalid to date (invalid month)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-06-09").param("minimum", "2560.23").param("toDate", "2016-15-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidToDate
    }

    def "daily balance reject invalid to date (invalid day)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-06-09").param("minimum", "2560.23").param("toDate", "2016-05-43")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidToDate
    }

    // Invalid range of dates

    def "daily balance reject invalid date ranges (from date after to date)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-08-09").param("minimum", "2560.23").param("toDate", "2016-06-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidDateRange
    }

    def "daily balance reject invalid date ranges (from date equal to date)"() {
        given:
        def url = "/pttg/financialstatusservice/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-06-13").param("minimum", "2560.23").param("toDate", "2016-06-13")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0000"
        jsonContent.status.message == invalidDateRange
    }

}
