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
class DoctorateExtensionMaintenanceThresholdServiceSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    ApplicationEventPublisher auditor = Mock()
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


    def callApi(studentType, inLondon, accommodationFeesPaid, dependants, dependantsOnly) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
                .param("dependantsOnly", dependantsOnly.toString())
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }


    def "Tier 4 Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        false    | 0.00                  | 5          | false          || 8830.00   || 0.00
        false    | 1425.78               | 1          | false          || 2125.00   || 1265.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        false    | 0.00                  | 11         | false          || 16990.00  || 0.00
        false    | 0.00                  | 7          | false          || 11550.00  || 0.00
        false    | 0.00                  | 8          | false          || 12910.00  || 0.00
        false    | 0.00                  | 11         | false          || 16990.00  || 0.00
        false    | 1382.86               | 0          | false          || 765.00    || 1265.00
        false    | 0.00                  | 12         | false          || 18350.00  || 0.00
        false    | 0.00                  | 10         | false          || 15630.00  || 0.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        false    | 0.00                  | 8          | false          || 12910.00  || 0.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        false    | 1916.83               | 0          | false          || 765.00    || 1265.00
        false    | 0.00                  | 3          | false          || 6110.00   || 0.00
        false    | 0.00                  | 11         | false          || 16990.00  || 0.00
        false    | 0.00                  | 1          | false          || 3390.00   || 0.00
        false    | 0.00                  | 3          | false          || 6110.00   || 0.00
    }

    def "Tier 4 Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        true     | 1862.30               | 10         | false          || 18165.00  || 1265.00
        true     | 0.00                  | 5          | false          || 10980.00  || 0.00
        true     | 0.00                  | 6          | false          || 12670.00  || 0.00
        true     | 1525.24               | 8          | false          || 14785.00  || 1265.00
        true     | 0.00                  | 0          | false          || 2530.00   || 0.00
        true     | 815.23                | 9          | false          || 16924.77  || 0.00
        true     | 1717.40               | 0          | false          || 1265.00   || 1265.00
        true     | 0.00                  | 11         | false          || 21120.00  || 0.00
        true     | 202.77                | 11         | false          || 20917.23  || 0.00
        true     | 0.00                  | 3          | false          || 7600.00   || 0.00
        true     | 0.00                  | 14         | false          || 26190.00  || 0.00
        true     | 1275.86               | 13         | false          || 23235.00  || 1265.00
        true     | 1431.84               | 8          | false          || 14785.00  || 1265.00
        true     | 0.00                  | 5          | false          || 10980.00  || 0.00
        true     | 0.00                  | 2          | false          || 5910.00   || 0.00
        true     | 0.00                  | 4          | false          || 9290.00   || 0.00
        true     | 0.00                  | 8          | false          || 16050.00  || 0.00
        true     | 0.00                  | 14         | false          || 26190.00  || 0.00
        true     | 1283.85               | 2          | false          || 4645.00   || 1265.00
        true     | 1626.90               | 1          | false          || 2955.00   || 1265.00

    }

    def "Tier 4 Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        false    | 1047.30               | 7          | false          || 10502.70  || 0.00
        false    | 1625.15               | 8          | false          || 11645.00  || 1265.00
        false    | 1705.18               | 11         | false          || 15725.00  || 1265.00
        false    | 1806.68               | 0          | false          || 765.00    || 1265.00
        false    | 1119.36               | 4          | false          || 6350.64   || 0.00
        true     | 1680.09               | 4          | false          || 8025.00   || 1265.00
        false    | 1523.17               | 10         | false          || 14365.00  || 1265.00
        true     | 59.84                 | 5          | false          || 10920.16  || 0.00
        true     | 386.06                | 9          | false          || 17353.94  || 0.00
        false    | 1830.69               | 1          | false          || 2125.00   || 1265.00
        true     | 1783.51               | 13         | false          || 23235.00  || 1265.00
        true     | 500.67                | 14         | false          || 25689.33  || 0.00
        true     | 628.59                | 3          | false          || 6971.41   || 0.00
        true     | 255.37                | 0          | false          || 2274.63   || 0.00
        false    | 1784.27               | 0          | false          || 765.00    || 1265.00
        true     | 1220.05               | 0          | false          || 1309.95   || 0.00
        true     | 347.32                | 6          | false          || 12322.68  || 0.00
        false    | 1909.73               | 6          | false          || 8925.00   || 1265.00
        true     | 486.06                | 2          | false          || 5423.94   || 0.00
        false    | 835.36                | 5          | false          || 7994.64   || 0.00

    }

    def "Tier 4 Doctorate - Check invalid accommodation fees parameters"() {
        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        inLondon | dependants | accommodationFeesPaid
        false    | 14         | -1
        true     | 11         | -7
    }


    def "Tier 4 Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, dependants, dependantsOnly)
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
        inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped
        true     | 1743.30               | 10         | true           || 16900.00  || 1265.00
        false    | 1020.68               | 4          | false          || 6449.32   || 0.00
        true     | 0.00                  | 7          | false          || 14360.00  || 0.00
        false    | 1522.03               | 0          | true           || 0.00      || 1265.00
        true     | 1649.28               | 0          | true           || 0.00      || 1265.00
        true     | 1610.89               | 13         | false          || 23235.00  || 1265.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        false    | 290.68                | 13         | false          || 19419.32  || 0.00
        true     | 1238.32               | 14         | true           || 23660.00  || 0.00
        false    | 0.00                  | 8          | true           || 10880.00  || 0.00
        false    | 817.69                | 0          | true           || 0.00      || 0.00
        false    | 0.00                  | 2          | false          || 4750.00   || 0.00
        false    | 0.00                  | 11         | false          || 16990.00  || 0.00
        true     | 1894.65               | 10         | false          || 18165.00  || 1265.00
        true     | 0.00                  | 0          | true           || 0.00      || 0.00
        false    | 1739.61               | 14         | false          || 19805.00  || 1265.00
        true     | 0.00                  | 9          | false          || 17740.00  || 0.00
        false    | 1575.15               | 11         | true           || 14960.00  || 1265.00
        true     | 641.22                | 12         | true           || 20280.00  || 0.00
        false    | 1699.62               | 12         | true           || 16320.00  || 1265.00

    }


    def "Tier 4 Doctorate - Check invalid characters accommodation fees parameters"() {
        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        inLondon | dependants | accommodationFeesPaid
        false    | 14         | "(&"
        true     | 11         | "ddd"
    }

    def "Tier 4 Doctorate - Check rounding accommodation fees parameters"() {
        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, dependants, false)
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
        def response = callApi("des", inLondon, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        inLondon | dependants | accommodationFeesPaid
        false    | -5         | 0
        true     | -986       | 0
    }

    def "Tier 4 Doctorate - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        inLondon | dependants | accommodationFeesPaid
        false    | "(*&66"    | 0
        true     | "h"        | 0
    }


    def "Tier 4 Doctorate - Check invalid dependantsOnly parameters"() {
        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, 0, dependantsOnly)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependantsOnly")))

        where:
        inLondon | dependantsOnly | accommodationFeesPaid
        false    | -5             | 0
        true     | -986           | 0
    }

    def "Tier 4 Doctorate - Check invalid characters dependantsOnly parameters"() {
        expect:
        def response = callApi("des", inLondon, accommodationFeesPaid, 0, dependantsOnly)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependantsOnly")))

        where:
        inLondon | dependantsOnly | accommodationFeesPaid
        false    | "(*&66"        | 0
        true     | "h"            | 0
    }

}
