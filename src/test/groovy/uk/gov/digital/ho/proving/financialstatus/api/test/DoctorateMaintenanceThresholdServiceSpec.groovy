package uk.gov.digital.ho.proving.financialstatus.api.test

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdService

import static org.hamcrest.core.StringContains.containsString
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = ServiceConfiguration.class)
class DoctorateMaintenanceThresholdServiceSpec extends Specification {

    def thresholdService = new ThresholdService()
    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper()))
        .build()


    def url = "/pttg/financialstatusservice/v1/maintenance/threshold"

    def callApi(studentType, innerLondon, courseLengthInMonths, accommodationFeesPaid) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("innerLondon", innerLondon.toString())
                .param("courseLength", courseLengthInMonths.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Tier 4 Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("doctorate", innerLondon, courseLengthInMonths, accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid || threshold
        false       | 1                    | 0.00                  || 1015.00
        false       | 2                    | 0.00                  || 2030.00
        false       | 3                    | 0.00                  || 2030.00
        false       | 4                    | 0.00                  || 2030.00
        false       | 5                    | 0.00                  || 2030.00
        false       | 6                    | 0.00                  || 2030.00
        false       | 7                    | 0.00                  || 2030.00
        false       | 8                    | 0.00                  || 2030.00
        false       | 9                    | 0.00                  || 2030.00


    }

    def "Tier 4 Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("doctorate", innerLondon, courseLengthInMonths, accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid || threshold
        true        | 1                    | 0.00                  || 1265.00
        true        | 2                    | 0.00                  || 2530.00
        true        | 3                    | 0.00                  || 2530.00
        true        | 4                    | 0.00                  || 2530.00
        true        | 5                    | 0.00                  || 2530.00
        true        | 6                    | 0.00                  || 2530.00
        true        | 7                    | 0.00                  || 2530.00
        true        | 8                    | 0.00                  || 2530.00
        true        | 9                    | 0.00                  || 2530.00

    }

    def "Tier 4 Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("doctorate", innerLondon, courseLengthInMonths, accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid || threshold
        true        | 1                    | 1184.00               || 81.00
        true        | 2                    | 655.00                || 1875.00
        true        | 3                    | 896.00                || 1634.00
        true        | 4                    | 893.00                || 1637.00
        true        | 5                    | 979.00                || 1551.00
        false       | 6                    | 531.00                || 1499.00
        false       | 7                    | 1619.00               || 765.00
        false       | 8                    | 1808.00               || 765.00
        false       | 9                    | 1674.00               || 765.00

    }

    def "Tier 4 Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("doctorate", innerLondon, courseLengthInMonths, accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid || threshold
        true        | 4                    | 1115.00               || 1415.00
        true        | 4                    | 1139.00               || 1391.00
        false       | 1                    | 1997.00               || 0.00
        true        | 6                    | 661.00                || 1869.00
        false       | 8                    | 541.00                || 1489.00
        true        | 2                    | 1286.00               || 1265.00
        true        | 9                    | 1263.00               || 1267.00
        false       | 8                    | 1887.00               || 765.00
        false       | 5                    | 1634.00               || 765.00
        true        | 5                    | 967.00                || 1563.00
        true        | 3                    | 781.00                || 1749.00
        false       | 7                    | 1858.00               || 765.00
        true        | 6                    | 1826.00               || 1265.00
        true        | 5                    | 1223.00               || 1307.00
        false       | 6                    | 547.00                || 1483.00
        false       | 4                    | 1209.00               || 821.00
        true        | 6                    | 1260.00               || 1270.00
        false       | 1                    | 202.00                || 813.00
    }

    def "Tier 4 Doctorate - Check invalid course length parameters"() {
        expect:
        def response = callApi("doctorate", innerLondon, courseLengthInMonths, accommodationFeesPaid)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid courseLength")))

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid || threshold
        false       | 10                   | 454.00                || 10995.00
        true        | -1                   | 336.00                || 13318.00
        false       | 20                   | 1044.00               || 7487.00
        false       | "bb"                 | 1044.00               || 7487.00
    }
}
