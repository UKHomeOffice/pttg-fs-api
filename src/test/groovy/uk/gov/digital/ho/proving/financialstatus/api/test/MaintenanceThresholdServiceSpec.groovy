package uk.gov.digital.ho.proving.financialstatus.api.test

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.DailyBalanceService
import uk.gov.digital.ho.proving.financialstatus.api.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdService

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = ServiceConfiguration.class)
class MaintenanceThresholdServiceSpec extends Specification {

    def thresholdService = new ThresholdService()
    MockMvc mockMvc = standaloneSetup(thresholdService).setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter()).build()

    def url = "/pttg/financialstatusservice/v1/maintenance/threshold"

    def callApi(innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid) {
        def response = mockMvc.perform(
            get(url)
                .param("innerLondon", innerLondon.toString())
                .param("courseLength", courseLengthInMonths.toString())
                .param("tuitionFees", tuitionFees.toString())
                .param("tuitionFeesPaid", tuitionFeesPaid.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi(innerLondon , courseLengthInMonths , tuitionFees , tuitionFeesPaid , accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        false       | 1                    | 8647.00     | 0.00            | 0.00                  || 9662.00
        false       | 2                    | 5224.00     | 0.00            | 0.00                  || 7254.00
        false       | 3                    | 7850.00     | 0.00            | 0.00                  || 10895.00
        false       | 4                    | 14962.00    | 0.00            | 0.00                  || 19022.00
        false       | 5                    | 10962.00    | 0.00            | 0.00                  || 16037.00
        false       | 6                    | 11413.00    | 0.00            | 0.00                  || 17503.00
        false       | 7                    | 13898.00    | 0.00            | 0.00                  || 21003.00
        false       | 8                    | 8468.00     | 0.00            | 0.00                  || 16588.00
        false       | 9                    | 10032.00    | 0.00            | 0.00                  || 19167.00

    }

    def "Check 'Inner London Borough'"() {

        expect:
        def response = callApi(innerLondon , courseLengthInMonths , tuitionFees , tuitionFeesPaid , accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        true        | 1                    | 11033.00    | 0.00            | 0.00                  || 12298.00
        true        | 2                    | 10679.00    | 0.00            | 0.00                  || 13209.00
        true        | 3                    | 12163.00    | 0.00            | 0.00                  || 15958.00
        true        | 4                    | 11829.00    | 0.00            | 0.00                  || 16889.00
        true        | 5                    | 8842.00     | 0.00            | 0.00                  || 15167.00
        true        | 6                    | 5332.00     | 0.00            | 0.00                  || 12922.00
        true        | 7                    | 13248.00    | 0.00            | 0.00                  || 22103.00
        true        | 8                    | 6233.00     | 0.00            | 0.00                  || 16353.00
        true        | 9                    | 10654.00    | 0.00            | 0.00                  || 22039.00

    }

    def "Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi(innerLondon , courseLengthInMonths , tuitionFees , tuitionFeesPaid , accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        true        | 1                    | 13575.00    | 2604.00         | 0.00                  || 12236.00
        true        | 2                    | 8493.00     | 784.00          | 0.00                  || 10239.00
        true        | 3                    | 14541.00    | 2264.00         | 0.00                  || 16072.00
        true        | 4                    | 5733.00     | 2262.00         | 0.00                  || 8531.00
        true        | 5                    | 5792.00     | 3829.00         | 0.00                  || 8288.00
        false       | 6                    | 5440.00     | 1725.00         | 0.00                  || 9805.00
        false       | 7                    | 12370.00    | 2635.00         | 0.00                  || 16840.00
        false       | 8                    | 9308.00     | 3768.00         | 0.00                  || 13660.00
        false       | 9                    | 12398.00    | 370.00          | 0.00                  || 21163.00

    }


    def "Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi(innerLondon , courseLengthInMonths , tuitionFees , tuitionFeesPaid , accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        true        | 1                    | 6022.00     | 0.00            | 1108.00               || 6179.00
        true        | 2                    | 9538.00     | 0.00            | 965.00                || 11103.00
        true        | 3                    | 14095.00    | 0.00            | 601.00                || 17289.00
        true        | 4                    | 6845.00     | 0.00            | 800.00                || 11105.00
        true        | 5                    | 8724.00     | 0.00            | 621.00                || 14428.00
        false       | 6                    | 5821.00     | 0.00            | 430.00                || 11481.00
        false       | 7                    | 10536.00    | 0.00            | 892.00                || 16749.00
        false       | 8                    | 6696.00     | 0.00            | 241.00                || 14575.00
        false       | 9                    | 6613.00     | 0.00            | 1277.00               || 14483.00

    }

    def "Check 'All variants'"() {
        expect:
        def response = callApi(innerLondon , courseLengthInMonths , tuitionFees , tuitionFeesPaid , accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid || threshold
        false       | 4                    | 9244.00     | 1855.00         | 454.00                || 10995.00
        true        | 7                    | 9411.00     | 4612.00         | 336.00                || 13318.00
        false       | 4                    | 7191.00     | 2720.00         | 1044.00               || 7487.00
        true        | 1                    | 14867.00    | 1938.00         | 1739.00               || 12929.00
        false       | 7                    | 13722.00    | 1593.00         | 1613.00               || 17969.00
        false       | 5                    | 8155.00     | 998.00          | 1715.00               || 10967.00
        false       | 5                    | 7776.00     | 4101.00         | 1382.00               || 7485.00
        false       | 6                    | 9627.00     | 3153.00         | 224.00                || 12340.00
        true        | 2                    | 13479.00    | 221.00          | 1036.00               || 14752.00
        true        | 3                    | 6360.00     | 4823.00         | 1915.00               || 4067.00
        false       | 3                    | 10986.00    | 2023.00         | 1926.00               || 10743.00
        false       | 7                    | 12188.00    | 4338.00         | 1824.00               || 13690.00
        true        | 5                    | 8809.00     | 3050.00         | 1581.00               || 10819.00
        true        | 9                    | 12511.00    | 1233.00         | 1831.00               || 21398.00
        true        | 2                    | 11505.00    | 2486.00         | 401.00                || 11148.00
        true        | 2                    | 10700.00    | 1392.00         | 1670.00               || 10573.00
        false       | 5                    | 5589.00     | 4090.00         | 720.00                || 5854.00
        false       | 5                    | 5889.00     | 2017.00         | 312.00                || 8635.00

    }

}
