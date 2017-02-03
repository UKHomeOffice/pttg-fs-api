package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import groovy.json.JsonSlurper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdServiceTier4
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventPublisher
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication

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

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    AuditEventPublisher auditor = Mock()
    Authentication authenticator = Mock()

    def thresholdService = new ThresholdServiceTier4(
        TestUtilsTier4.maintenanceThresholdServiceBuilder(), TestUtilsTier4.getStudentTypeChecker(),
        TestUtilsTier4.getCourseTypeChecker(), serviceMessages, auditor, authenticator
    )

    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(), serviceMessages))
        .build()


    def url = TestUtilsTier4.thresholdUrl


    def callApi(studentType, inLondon, accommodationFeesPaid, dependants) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }


    def "Tier 4 Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("doctorate", inLondon, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | accommodationFeesPaid | dependants || threshold
        false    | 0.00                  | 5          || 8830.00
        false    | 0.00                  | 7          || 11550.00
    }

    def "Tier 4 Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("doctorate", inLondon, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | accommodationFeesPaid | dependants || threshold
        true     | 0.00                  | 4          || 9290.00
        true     | 0.00                  | 15         || 27880.00

    }

    def "Tier 4 Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("doctorate", inLondon, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | accommodationFeesPaid | dependants || threshold
        true     | 1039.00               | 14         || 25151.00
        true     | 692.00                | 11         || 20428.00
        true     | 622.00                | 3          || 6978.00
        true     | 154.00                | 9          || 17586.00
        true     | 869.00                | 10         || 18561.00
        false    | 860.00                | 12         || 17490.00
        false    | 206.00                | 9          || 14064.00
        false    | 106.00                | 11         || 16884.00
        false    | 1245.00               | 0          || 785.00

    }

    def "Tier 4 Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("doctorate", inLondon, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        if (feesCapped > 0) {
            assert jsonContent.cappedValues && jsonContent.cappedValues.accommodationFeesPaid != null
            assert jsonContent.cappedValues.accommodationFeesPaid == feesCapped
        } else {
            assert jsonContent.cappedValues == null || jsonContent.cappedValues.accommodationFeesPaid == null
        }

        where:
        inLondon | accommodationFeesPaid | dependants || threshold || feesCapped
        false    | 2627.00               | 15         || 21165.00  || 1265.00
        false    | 270.00                | 10         || 15360.00  || 0
        true     | 22.00                 | 1          || 4198.00   || 0
        true     | 636.00                | 9          || 17104.00  || 0
        false    | 1018.00               | 3          || 5092.00   || 0
        true     | 446.00                | 6          || 12224.00  || 0
        false    | 372.00                | 6          || 9818.00   || 0
        true     | 657.00                | 13         || 23843.00  || 0
        true     | 953.00                | 6          || 11717.00  || 0
        true     | 229.00                | 12         || 22581.00  || 0
        true     | 23.00                 | 12         || 22787.00  || 0
        false    | 182.00                | 14         || 20888.00  || 0
        false    | 738.00                | 12         || 17612.00  || 0
        true     | 73.00                 | 9          || 17667.00  || 0
        false    | 970.00                | 6          || 9220.00   || 0
        true     | 4934.00               | 5          || 9715.00   || 1265.00
        true     | 223.00                | 4          || 9067.00   || 0
        true     | 1078.00               | 14         || 25112.00  || 0
        true     | 250.50                | 3          || 7349.50   || 0


    }

    def "Tier 4 Doctorate - Check invalid accommodation fees parameters"() {
        expect:
        def response = callApi("doctorate", inLondon, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        inLondon | dependants | accommodationFeesPaid
        false    | 14         | -1
        true     | 11         | -7
    }

    def "Tier 4 Doctorate - Check invalid characters accommodation fees parameters"() {
        expect:
        def response = callApi("doctorate", inLondon, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        inLondon | dependants | accommodationFeesPaid
        false    | 14         | "(&"
        true     | 11         | "ddd"
    }

    def "Tier 4 Doctorate - Check rounding accommodation fees parameters"() {
        expect:
        def response = callApi("doctorate", inLondon, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | dependants | accommodationFeesPaid || threshold
        false    | 0          | 0.0000                || 2030.00
        false    | 0          | 0.010                 || 2029.99
        false    | 0          | 0.0010                || 2030.00
        false    | 0          | 0.005                 || 2029.99
        false    | 0          | 0.004                 || 2030.00
        false    | 0          | -0.004                || 2030.00
    }

    def "Tier 4 Doctorate - Check invalid dependants parameters"() {
        expect:
        def response = callApi("doctorate", inLondon, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        inLondon | dependants | accommodationFeesPaid
        false    | -5         | 0
        true     | -986       | 0
    }

    def "Tier 4 Doctorate - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("doctorate", inLondon, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        inLondon | dependants | accommodationFeesPaid
        false    | "(*&66"    | 0
        true     | "h"        | 0
    }

}
