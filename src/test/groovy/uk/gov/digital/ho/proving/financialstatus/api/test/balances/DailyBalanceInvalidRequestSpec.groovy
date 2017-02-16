package uk.gov.digital.ho.proving.financialstatus.api.test.balances

import cucumber.api.java.Before
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.audit.EmbeddedMongoClientConfiguration

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

/**
 * @Author Home Office Digital
 */

@WebAppConfiguration
@ContextConfiguration(classes = [ServiceConfiguration.class, EmbeddedMongoClientConfiguration.class])
class DailyBalanceInvalidRequestSpec extends Specification {

    @Autowired
    private WebApplicationContext webApplicationContext
    private MockMvc mockMvc

    def invalidSortCode = "Parameter error: Invalid sort code"
    def invalidAccountNumber = "Parameter error: Invalid account number"
    def invalidTotalFunds = "Parameter error: Invalid value for minimum"
    def invalidFromDate = "Parameter error: Invalid from date"
    def invalidConversionFromDate = "Parameter conversion error: Invalid from date"
    def invalidConversionToDate = "Parameter conversion error: Invalid to date"

    def invalidToDate = "Parameter error: Invalid to date"
    def invalidDob = "Parameter error: Invalid date of birth"
    def invalidDateRange = "Parameter error: Invalid dates, from date must be before to date"

    @Before
    def setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    def "daily balance reject invalid sort code (invalid character)"() {

        given:
        def url = "/pttg/financialstatus/v1/accounts/12345a/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isNotFound())
    }

    def "daily balance reject invalid sort code (too few numbers)"() {

        given:
        def url = "/pttg/financialstatus/v1/accounts/12345/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidSortCode
    }

    def "daily balance reject invalid sort code (too many numbers)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/1234567/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidSortCode
    }

    def "daily balance reject invalid sort code (all zeroes)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/000000/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidSortCode
    }

    def "daily balance reject invalid account number (invalid character)"() {

        given:
        def url = "/pttg/financialstatus/v1/accounts/12345/123d5678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isNotFound())
    }

    def "daily balance reject invalid account number (too few numbers)"() {

        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/1234567/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidAccountNumber
    }

    def "daily balance reject invalid account number (too many numbers)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/123456789/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidAccountNumber
    }

    def "daily balance reject invalid account number (all zeroes)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/00000000/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidAccountNumber
    }

    def "daily balance reject invalid minimum value (below zero)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "-2560.23")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidTotalFunds
    }

    def "daily balance reject invalid minimum value (equal to zero)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "0")
                .param("fromDate", "2016-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidTotalFunds
    }

    // Invalid from date tests

    def "daily balance reject invalid from date (invalid year)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2a16-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0002"
        jsonContent.status.message == invalidConversionFromDate
    }

    def "daily balance reject invalid from date (invalid month)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-15-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0002"
        jsonContent.status.message == invalidConversionFromDate
    }

    def "daily balance reject invalid from date (invalid day)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("fromDate", "2016-05-43")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0002"
        jsonContent.status.message == invalidConversionFromDate
    }

    // Invalid to date tests

    def "daily balance reject invalid to date (invalid year)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("toDate", "2a16-05-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0002"
        jsonContent.status.message == invalidConversionToDate
    }

    def "daily balance reject invalid to date (invalid month)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("toDate", "2016-15-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0002"
        jsonContent.status.message == invalidConversionToDate
    }

    def "daily balance reject invalid to date (invalid day)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-06-09")
                .param("minimum", "2560.23")
                .param("toDate", "2016-05-43")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")

        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0002"
        jsonContent.status.message == invalidConversionToDate
    }

    // Invalid range of dates

    def "daily balance reject invalid date ranges (from date after to date)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-08-09")
                .param("minimum", "2560.23")
                .param("toDate", "2016-06-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidDateRange
    }

    def "daily balance reject invalid date ranges (from date equal to date)"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-06-13")
                .param("minimum", "2560.23")
                .param("toDate", "2016-06-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidDateRange
    }

    def "daily balance reject future toDate date"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"
        def toDate = LocalDate.now().plusDays(1)
        def fromDate = toDate.minusDays(27)


        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", fromDate.toString())
                .param("minimum", "2560.23")
                .param("toDate", toDate.toString())
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidToDate
    }

    def "daily balance reject future fromDate date"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"
        def fromDate = LocalDate.now().plusDays(1)
        def toDate = fromDate.minusDays(27)


        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", fromDate.toString())
                .param("minimum", "2560.23")
                .param("toDate", toDate.toString())
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidFromDate
    }

    // Missing values

    def "daily balance reject missing from date value"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "")
                .param("minimum", "2560.23")
                .param("toDate", "2016-06-13")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidFromDate
    }

    def "daily balance reject missing to date value"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-05-13")
                .param("minimum", "2560.23")
                .param("toDate", "")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidToDate
    }

    def "daily balance reject missing minimum value"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("toDate", "2016-06-09")
                .param("fromDate", "2016-05-13")
                .param("minimum", "")
                .param("dob", "2000-01-01")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidTotalFunds
    }

    def "daily balance reject missing to dob value"() {
        given:
        def url = "/pttg/financialstatus/v1/accounts/123456/12345678/dailybalancestatus"

        when:
        def response = mockMvc.perform(
            get(url).param("fromDate", "2016-05-13")
                .param("minimum", "2560.23")
                .param("toDate", "2016-06-09")
                .param("userId", "userid123456")
                .param("accountHolderConsent", "true")
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isBadRequest())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.code == "0004"
        jsonContent.status.message == invalidDob
    }
}
