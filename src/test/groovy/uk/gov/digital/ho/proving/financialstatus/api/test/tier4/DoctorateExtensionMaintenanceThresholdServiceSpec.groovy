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
        true     | 288.69                | 2          | true           || 3380.00   || 0.00
        true     | 1697.96               | 10         | true           || 16900.00  || 1265.00
        true     | 1605.92               | 13         | true           || 21970.00  || 1265.00
        true     | 1268.95               | 7          | true           || 11830.00  || 1265.00
        false    | 0.00                  | 1          | true           || 1360.00   || 0.00
        true     | 663.17                | 4          | false          || 8626.83   || 0.00
        false    | 476.36                | 10         | false          || 15153.64  || 0.00
        false    | 1348.38               | 1          | true           || 1360.00   || 1265.00
        true     | 509.12                | 0          | false          || 2020.88   || 0.00
        true     | 780.71                | 13         | false          || 23719.29  || 0.00
        false    | 0.00                  | 7          | false          || 11550.00  || 0.00
        true     | 0.00                  | 0          | false          || 2530.00   || 0.00
        true     | 0.00                  | 13         | false          || 24500.00  || 0.00
        false    | 1500.17               | 0          | true           || 0.00      || 1265.00
        false    | 141.59                | 2          | true           || 2720.00   || 0.00
        false    | 223.77                | 12         | true           || 16320.00  || 0.00
        false    | 287.59                | 13         | true           || 17680.00  || 0.00
        true     | 912.68                | 13         | false          || 23587.32  || 0.00
        false    | 0.00                  | 14         | true           || 19040.00  || 0.00
        false    | 1142.86               | 0          | true           || 0.00      || 0.00
        false    | 903.68                | 13         | false          || 18806.32  || 0.00
        false    | 0.00                  | 0          | true           || 0.00      || 0.00
        false    | 874.59                | 0          | false          || 1155.41   || 0.00
        true     | 0.00                  | 0          | false          || 2530.00   || 0.00
        true     | 0.00                  | 10         | true           || 16900.00  || 0.00
        false    | 0.00                  | 12         | true           || 16320.00  || 0.00
        true     | 0.00                  | 2          | false          || 5910.00   || 0.00
        true     | 1133.27               | 0          | false          || 1396.73   || 0.00
        true     | 696.89                | 5          | true           || 8450.00   || 0.00
        true     | 99.58                 | 0          | true           || 0.00      || 0.00
        true     | 0.00                  | 0          | false          || 2530.00   || 0.00
        true     | 597.42                | 2          | false          || 5312.58   || 0.00
        false    | 239.39                | 9          | false          || 14030.61  || 0.00
        true     | 929.16                | 11         | true           || 18590.00  || 0.00
        false    | 0.00                  | 0          | true           || 0.00      || 0.00
        true     | 817.71                | 13         | true           || 21970.00  || 0.00
        false    | 1782.35               | 12         | true           || 16320.00  || 1265.00
        false    | 1675.51               | 13         | false          || 18445.00  || 1265.00
        true     | 1591.38               | 1          | true           || 1690.00   || 1265.00
        false    | 0.00                  | 10         | false          || 15630.00  || 0.00
        true     | 1671.22               | 5          | true           || 8450.00   || 1265.00
        false    | 1747.46               | 0          | false          || 765.00    || 1265.00
        true     | 901.79                | 3          | false          || 6698.21   || 0.00
        false    | 419.27                | 0          | false          || 1610.73   || 0.00
        true     | 0.00                  | 12         | false          || 22810.00  || 0.00
        false    | 1564.31               | 6          | true           || 8160.00   || 1265.00
        false    | 0.00                  | 1          | false          || 3390.00   || 0.00
        true     | 330.67                | 10         | true           || 16900.00  || 0.00
        true     | 817.27                | 1          | true           || 1690.00   || 0.00
        false    | 1186.80               | 9          | true           || 12240.00  || 0.00
        false    | 1753.55               | 7          | false          || 10285.00  || 1265.00
        false    | 0.00                  | 0          | true           || 0.00      || 0.00
        true     | 0.00                  | 4          | true           || 6760.00   || 0.00
        false    | 0.00                  | 0          | true           || 0.00      || 0.00
        false    | 1521.10               | 6          | false          || 8925.00   || 1265.00
        true     | 350.03                | 11         | false          || 20769.97  || 0.00
        true     | 873.07                | 7          | true           || 11830.00  || 0.00
        true     | 568.76                | 0          | false          || 1961.24   || 0.00
        false    | 1587.96               | 7          | true           || 9520.00   || 1265.00
        true     | 0.00                  | 2          | false          || 5910.00   || 0.00
        false    | 76.06                 | 14         | false          || 20993.94  || 0.00
        true     | 0.00                  | 0          | true           || 0.00      || 0.00
        false    | 443.70                | 4          | false          || 7026.30   || 0.00
        true     | 421.19                | 14         | false          || 25768.81  || 0.00
        false    | 0.00                  | 7          | true           || 9520.00   || 0.00
        true     | 1867.19               | 14         | false          || 24925.00  || 1265.00
        true     | 0.00                  | 3          | true           || 5070.00   || 0.00
        true     | 581.73                | 4          | true           || 6760.00   || 0.00
        true     | 0.00                  | 9          | true           || 15210.00  || 0.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        false    | 0.00                  | 7          | true           || 9520.00   || 0.00
        false    | 0.00                  | 12         | false          || 18350.00  || 0.00
        true     | 0.00                  | 2          | true           || 3380.00   || 0.00
        false    | 663.46                | 1          | false          || 2726.54   || 0.00
        true     | 0.00                  | 0          | true           || 0.00      || 0.00
        true     | 0.00                  | 11         | false          || 21120.00  || 0.00
        true     | 0.00                  | 1          | false          || 4220.00   || 0.00
        true     | 0.00                  | 1          | false          || 4220.00   || 0.00
        true     | 839.26                | 9          | true           || 15210.00  || 0.00
        false    | 1012.50               | 4          | false          || 6457.50   || 0.00
        true     | 232.38                | 2          | true           || 3380.00   || 0.00
        false    | 0.00                  | 1          | true           || 1360.00   || 0.00
        true     | 1003.08               | 4          | false          || 8286.92   || 0.00
        false    | 435.34                | 0          | true           || 0.00      || 0.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        false    | 0.00                  | 11         | true           || 14960.00  || 0.00
        false    | 0.00                  | 0          | true           || 0.00      || 0.00
        true     | 1922.98               | 11         | true           || 18590.00  || 1265.00
        true     | 679.58                | 4          | true           || 6760.00   || 0.00
        true     | 1387.28               | 0          | false          || 1265.00   || 1265.00
        false    | 539.02                | 8          | true           || 10880.00  || 0.00
        true     | 1278.25               | 8          | true           || 13520.00  || 1265.00
        false    | 0.00                  | 5          | true           || 6800.00   || 0.00
        true     | 0.00                  | 5          | false          || 10980.00  || 0.00
        false    | 1129.91               | 3          | true           || 4080.00   || 0.00
        false    | 730.88                | 5          | false          || 8099.12   || 0.00
        false    | 0.00                  | 0          | false          || 2030.00   || 0.00
        false    | 1281.45               | 2          | false          || 3485.00   || 1265.00
        true     | 0.00                  | 13         | false          || 24500.00  || 0.00
        false    | 0.00                  | 0          | true           || 0.00      || 0.00
        false    | 478.86                | 8          | true           || 10880.00  || 0.00
        true     | 0.00                  | 0          | true           || 0.00      || 0.00
        true     | 850.12                | 5          | true           || 8450.00   || 0.00
        false    | 1164.99               | 0          | true           || 0.00      || 0.00
        true     | 1019.09               | 14         | false          || 25170.91  || 0.00
        false    | 1824.03               | 11         | false          || 15725.00  || 1265.00
        true     | 0.00                  | 6          | true           || 10140.00  || 0.00
        true     | 0.00                  | 4          | true           || 6760.00   || 0.00
        false    | 609.76                | 11         | false          || 16380.24  || 0.00
        true     | 0.00                  | 5          | true           || 8450.00   || 0.00
        true     | 0.00                  | 8          | true           || 13520.00  || 0.00
        true     | 0.00                  | 1          | false          || 4220.00   || 0.00
        false    | 975.76                | 4          | false          || 6494.24   || 0.00
        false    | 0.00                  | 3          | false          || 6110.00   || 0.00
        false    | 1390.69               | 3          | false          || 4845.00   || 1265.00
        true     | 385.68                | 13         | false          || 24114.32  || 0.00
        false    | 75.11                 | 10         | true           || 13600.00  || 0.00
        false    | 0.00                  | 0          | true           || 0.00      || 0.00
        true     | 0.00                  | 0          | false          || 2530.00   || 0.00
        false    | 1991.90               | 0          | false          || 765.00    || 1265.00
        true     | 0.00                  | 3          | false          || 7600.00   || 0.00
        false    | 1585.27               | 12         | false          || 17085.00  || 1265.00
        false    | 0.00                  | 8          | true           || 10880.00  || 0.00
        false    | 1475.27               | 0          | true           || 0.00      || 1265.00
        false    | 0.00                  | 0          | true           || 0.00      || 0.00
        false    | 0.00                  | 9          | false          || 14270.00  || 0.00
        false    | 1957.27               | 9          | true           || 12240.00  || 1265.00
        true     | 909.44                | 0          | true           || 0.00      || 0.00
        false    | 8.24                  | 8          | true           || 10880.00  || 0.00
        true     | 1518.28               | 9          | false          || 16475.00  || 1265.00
        false    | 0.00                  | 6          | false          || 10190.00  || 0.00
        true     | 1119.17               | 3          | false          || 6480.83   || 0.00
        true     | 618.96                | 7          | false          || 13741.04  || 0.00
        false    | 1634.13               | 2          | true           || 2720.00   || 1265.00
        true     | 0.00                  | 14         | true           || 23660.00  || 0.00
        false    | 1929.72               | 3          | true           || 4080.00   || 1265.00
        true     | 1173.87               | 3          | false          || 6426.13   || 0.00
        false    | 970.24                | 0          | true           || 0.00      || 0.00
        true     | 0.00                  | 0          | true           || 0.00      || 0.00
        false    | 0.00                  | 13         | false          || 19710.00  || 0.00
        false    | 632.31                | 0          | false          || 1397.69   || 0.00
        true     | 0.00                  | 1          | false          || 4220.00   || 0.00
        true     | 1784.17               | 9          | false          || 16475.00  || 1265.00
        false    | 386.98                | 13         | false          || 19323.02  || 0.00
        false    | 1250.59               | 1          | true           || 1360.00   || 0.00
        false    | 0.00                  | 12         | false          || 18350.00  || 0.00
        false    | 206.94                | 6          | true           || 8160.00   || 0.00
        true     | 989.55                | 0          | true           || 0.00      || 0.00
        true     | 0.00                  | 9          | true           || 15210.00  || 0.00
        true     | 0.00                  | 7          | true           || 11830.00  || 0.00
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
