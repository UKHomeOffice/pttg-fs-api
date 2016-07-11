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

    }

    def "Tier 4 Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("doctorate", innerLondon, courseLengthInMonths, accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid || threshold
        true        | 1                    | 1989.00               || 0.00
        true        | 2                    | 246.00                || 2284.00
        true        | 1                    | 774.00                || 491.00
        true        | 2                    | 822.00                || 1708.00
        true        | 1                    | 280.00                || 985.00
        false       | 2                    | 1722.00               || 765.00
        false       | 1                    | 255.00                || 760.00
        false       | 2                    | 908.00                || 1122.00
        false       | 1                    | 1203.00               || 0.00

    }

    def "Tier 4 Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("doctorate", innerLondon, courseLengthInMonths, accommodationFeesPaid)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid || threshold
        false       | 2                    | 823.00                || 1207.00
        true        | 2                    | 1747.00               || 1265.00
        true        | 1                    | 1203.00               || 62.00
        false       | 1                    | 1732.00               || 0.00
        false       | 1                    | 1783.00               || 0.00
        false       | 1                    | 1397.00               || 0.00
        true        | 1                    | 205.00                || 1060.00
        true        | 2                    | 1249.00               || 1281.00
        false       | 2                    | 611.00                || 1419.00
        true        | 2                    | 309.00                || 2221.00
        false       | 2                    | 1425.00               || 765.00
        true        | 2                    | 1547.00               || 1265.00
        true        | 2                    | 1006.00               || 1524.00
        true        | 2                    | 1848.00               || 1265.00
        true        | 1                    | 752.00                || 513.00
        true        | 2                    | 1762.00               || 1265.00
        true        | 2                    | 1643.00               || 1265.00
        true        | 2                    | 1676.00               || 1265.00
    }

    def "Tier 4 Doctorate - Check invalid course length parameters"() {
        expect:
        def response = callApi("doctorate", innerLondon, courseLengthInMonths, accommodationFeesPaid)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid courseLength")))

        where:
        innerLondon | courseLengthInMonths | accommodationFeesPaid || threshold
        false       | 3                    | 454.00                || 10995.00
        true        | -1                   | 336.00                || 13318.00
        false       | 0                    | 1044.00               || 7487.00
        false       | "bb"                 | 1044.00               || 7487.00
    }
}
