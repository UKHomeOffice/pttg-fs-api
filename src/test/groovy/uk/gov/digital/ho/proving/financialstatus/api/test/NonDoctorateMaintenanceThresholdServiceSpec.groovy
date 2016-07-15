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
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

import static TestUtils.getMessageSource
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
class NonDoctorateMaintenanceThresholdServiceSpec extends Specification {

    def thresholdService = new ThresholdService(
        new MaintenanceThresholdCalculator(TestUtils.innerLondonMaintenance, TestUtils.nonInnerLondonMaintenance,
            TestUtils.maxMaintenanceAllowance, TestUtils.maxDoctorateMonths, TestUtils.innerLondonDependant,
            TestUtils.nonInnerLondonDependant), getMessageSource()
    )

    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper()))
        .build()


    def url = TestUtils.thresholdUrl

    def callApi(studentType, innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("innerLondon", innerLondon.toString())
                .param("courseLength", courseLengthInMonths.toString())
                .param("tuitionFees", tuitionFees.toString())
                .param("tuitionFeesPaid", tuitionFeesPaid.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Tier 4 Non Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false       | 1                    | 10047.00    | 0.00            | 0.00                  | 1          || 11742.00
        false       | 2                    | 11682.00    | 0.00            | 0.00                  | 14         || 32752.00
        false       | 3                    | 6160.00     | 0.00            | 0.00                  | 2          || 13285.00
        false       | 4                    | 7560.00     | 0.00            | 0.00                  | 6          || 27940.00
        false       | 5                    | 7482.00     | 0.00            | 0.00                  | 10         || 46557.00
        false       | 6                    | 8713.00     | 0.00            | 0.00                  | 9          || 51523.00
        false       | 7                    | 7307.00     | 0.00            | 0.00                  | 2          || 23932.00
        false       | 8                    | 5878.00     | 0.00            | 0.00                  | 4          || 35758.00
        false       | 9                    | 9180.00     | 0.00            | 0.00                  | 10         || 79515.00

    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true        | 1                    | 6305.00     | 0.00            | 0.00                  | 14         || 19400.00
        true        | 2                    | 13788.00    | 0.00            | 0.00                  | 7          || 28148.00
        true        | 3                    | 13890.00    | 0.00            | 0.00                  | 2          || 22755.00
        true        | 4                    | 9781.00     | 0.00            | 0.00                  | 9          || 45261.00
        true        | 5                    | 9601.00     | 0.00            | 0.00                  | 12         || 66626.00
        true        | 6                    | 14502.00    | 0.00            | 0.00                  | 0          || 22092.00
        true        | 7                    | 12672.00    | 0.00            | 0.00                  | 13         || 98422.00
        true        | 8                    | 14618.00    | 0.00            | 0.00                  | 10         || 92338.00
        true        | 9                    | 11896.00    | 0.00            | 0.00                  | 3          || 46096.00

    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true        | 1                    | 13693.00    | 2214.00         | 0.00                  | 13         || 23729.00
        true        | 2                    | 12710.00    | 2065.00         | 0.00                  | 4          || 19935.00
        true        | 3                    | 10591.00    | 2435.00         | 0.00                  | 4          || 22091.00
        true        | 4                    | 14897.00    | 1287.00         | 0.00                  | 3          || 28810.00
        true        | 5                    | 13362.00    | 626.00          | 0.00                  | 12         || 69761.00
        false       | 6                    | 9905.00     | 4601.00         | 0.00                  | 0          || 11394.00
        false       | 7                    | 9870.00     | 4713.00         | 0.00                  | 12         || 69382.00
        false       | 8                    | 11031.00    | 395.00          | 0.00                  | 12         || 84036.00
        false       | 9                    | 12972.00    | 4774.00         | 0.00                  | 5          || 47933.00

    }


    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true        | 1                    | 8072.00     | 0.00            | 876.00                | 11         || 17756.00
        true        | 2                    | 9714.00     | 0.00            | 201.00                | 0          || 12043.00
        true        | 3                    | 10426.00    | 0.00            | 903.00                | 11         || 41203.00
        true        | 4                    | 5266.00     | 0.00            | 566.00                | 15         || 60460.00
        true        | 5                    | 13156.00    | 0.00            | 560.00                | 8          || 52721.00
        false       | 6                    | 5693.00     | 0.00            | 1136.00               | 7          || 39207.00
        false       | 7                    | 10337.00    | 0.00            | 620.00                | 7          || 50142.00
        false       | 8                    | 6749.00     | 0.00            | 485.00                | 6          || 47024.00
        false       | 9                    | 6242.00     | 0.00            | 917.00                | 2          || 26700.00

    }

    def "Tier 4 Non Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold.toString()

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true        | 3                    | 6751.00     | 1508.00         | 325.00                | 13         || 41668.00
        true        | 4                    | 8087.00     | 506.00          | 598.00                | 2          || 18803.00
        false       | 4                    | 6546.00     | 3997.00         | 948.00                | 0          || 5661.00
        false       | 8                    | 10265.00    | 4912.00         | 95.00                 | 14         || 89538.00
        true        | 2                    | 6624.00     | 3054.00         | 3.00                  | 8          || 19617.00
        false       | 4                    | 8476.00     | 1758.00         | 652.00                | 11         || 40046.00
        false       | 4                    | 11555.00    | 4773.00         | 602.00                | 1          || 12960.00
        false       | 9                    | 14248.00    | 2354.00         | 1260.00               | 1          || 25889.00
        true        | 6                    | 12883.00    | 1081.00         | 547.00                | 6          || 49265.00
        false       | 7                    | 9428.00     | 1688.00         | 126.00                | 10         || 62319.00
        false       | 6                    | 12320.00    | 4379.00         | 1011.00               | 12         || 61980.00
        true        | 9                    | 9202.00     | 487.00          | 618.00                | 13         || 118347.00
        false       | 7                    | 13171.00    | 403.00          | 77.00                 | 13         || 81676.00
        false       | 5                    | 5669.00     | 3209.00         | 999.00                | 3          || 16736.00
        false       | 7                    | 10095.00    | 2564.00         | 236.00                | 4          || 33440.00
        false       | 6                    | 13104.00    | 2056.00         | 977.00                | 5          || 36561.00
        true        | 7                    | 8187.00     | 3318.00         | 805.00                | 14         || 95729.00
        true        | 8                    | 10169.00    | 2731.00         | 1204.00               | 8          || 70434.00

    }

    def "Tier 4 Non Doctorate - Check invalid course length parameters"() {
        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid courseLength")))

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false       | 10                   | 9244.00     | 1855.00         | 0          | 454.00
        true        | -1                   | 9411.00     | 4612.00         | 0          | 336.00
        false       | 20                   | 7191.00     | 2720.00         | 0          | 1044.00
        false       | "bb"                 | 7191.00     | 2720.00         | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees parameters"() {
        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFees")))

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false       | 1                    | -2          | 1855.00         | 0          | 454.00
        true        | 2                    | -0.05       | 4612.00         | 0          | 336.00
        false       | 3                    | "hh"        | 2720.00         | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFeesPaid")))

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false       | 1                    | 1855.00     | -2              | 0          | 454.00
        true        | 2                    | 4612.00     | -0.05           | 0          | 336.00
        false       | 3                    | 2720.00     | "kk"            | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid accommodation fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false       | 1                    | 454.00      | 1855.00         | 0          | -2
        true        | 2                    | 336.00      | 4612.00         | 0          | -0.05
        false       | 3                    | 1044.00     | 2720.00         | 0          | "hh"
    }

    def "Tier 4 Non Doctorate - Check invalid dependants parameters"() {
        expect:
        def response = callApi("nondoctorate", innerLondon, courseLengthInMonths, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        innerLondon | courseLengthInMonths | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false       | 1                    | 454.00      | 1855.00         | -5         | 0
        true        | 2                    | 336.00      | 4612.00         | "h"        | 0
    }

}
