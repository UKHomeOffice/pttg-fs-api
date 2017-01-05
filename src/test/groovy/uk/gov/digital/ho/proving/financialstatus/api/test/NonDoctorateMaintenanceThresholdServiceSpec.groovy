package uk.gov.digital.ho.proving.financialstatus.api.test

import groovy.json.JsonSlurper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdService
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

import java.time.LocalDate

import static org.hamcrest.core.StringContains.containsString
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
import static uk.gov.digital.ho.proving.financialstatus.api.test.TestUtils.*

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = ServiceConfiguration.class)
class NonDoctorateMaintenanceThresholdServiceSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(getMessageSource())

    ApplicationEventPublisher auditor = Mock()
    Authentication authenticator = Mock()

    def thresholdService = new ThresholdService(
        maintenanceThresholdServiceBuilder(), getStudentTypeChecker(),
        getCourseTypeChecker(), serviceMessages, auditor, authenticator
    )

    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(), serviceMessages))
        .build()


    def url = TestUtils.thresholdUrl

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("originalCourseStartDate", (originalCourseStartDate == null) ? "" : originalCourseStartDate.toString())
                .param("tuitionFees", tuitionFees.toString())
                .param("tuitionFeesPaid", tuitionFeesPaid.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
                .param("courseType", courseType)
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Tier 4 Non Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2025, 9, 19)  | LocalDate.of(2026, 5, 10)  | null                      | false    | 9913.79     | 8361.39         | 0.00                  | 3          | "main"          || 28032.40  || 0.00       || 0            || LocalDate.of(2026, 7, 10)
        LocalDate.of(2049, 6, 28)  | LocalDate.of(2049, 9, 21)  | LocalDate.of(2048, 11, 8) | false    | 540.29      | 0.00            | 1102.86               | 0          | "main"          || 2482.43   || 0.00       || 0            || LocalDate.of(2049, 11, 21)
        LocalDate.of(2017, 2, 24)  | LocalDate.of(2017, 12, 22) | null                      | false    | 6945.02     | 0.00            | 0.00                  | 5          | "main"          || 46680.02  || 0.00       || 9            || LocalDate.of(2018, 2, 22)
        LocalDate.of(2012, 11, 28) | LocalDate.of(2013, 12, 16) | null                      | false    | 4023.16     | 0.00            | 0.00                  | 13         | "pre-sessional" || 92718.16  || 0.00       || 9            || LocalDate.of(2014, 4, 16)
        LocalDate.of(2046, 2, 28)  | LocalDate.of(2046, 10, 28) | null                      | false    | 9546.08     | 9241.17         | 305.42                | 6          | "pre-sessional" || 45854.49  || 0.00       || 0            || LocalDate.of(2046, 12, 28)
    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate           | courseEndDate              | originalCourseStartDate   | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2045, 4, 5)  | LocalDate.of(2046, 3, 11)  | null                      | true     | 7948.65     | 0.00            | 1936.85               | 14         | "main"          || 124538.65 || 1265.00    || 9            || LocalDate.of(2046, 5, 11)
        LocalDate.of(1987, 1, 27) | LocalDate.of(1987, 9, 26)  | null                      | true     | 3374.60     | 0.00            | 0.00                  | 14         | "pre-sessional" || 119964.60 || 0.00       || 0            || LocalDate.of(1987, 11, 26)
        LocalDate.of(2052, 3, 30) | LocalDate.of(2052, 10, 31) | null                      | true     | 9842.10     | 0.00            | 0.00                  | 9          | "pre-sessional" || 88407.10  || 0.00       || 0            || LocalDate.of(2052, 12, 31)
        LocalDate.of(1998, 9, 1)  | LocalDate.of(1999, 9, 19)  | null                      | true     | 5598.88     | 9321.15         | 0.00                  | 8          | "pre-sessional" || 72225.00  || 0.00       || 9            || LocalDate.of(2000, 1, 19)
        LocalDate.of(2024, 10, 7) | LocalDate.of(2025, 3, 13)  | LocalDate.of(2023, 11, 2) | true     | 7355.01     | 0.00            | 0.00                  | 11         | "main"          || 98600.01  || 0.00       || 0            || LocalDate.of(2025, 7, 13)
    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2026, 5, 20)  | LocalDate.of(2026, 7, 5)   | null                      | true     | 5848.39     | 1550.61         | 1226.56               | 0          | "pre-sessional" || 5601.22   || 0.00       || 0            || LocalDate.of(2026, 8, 5)
        LocalDate.of(2011, 2, 14)  | LocalDate.of(2011, 11, 5)  | null                      | true     | 1904.64     | 6104.90         | 0.00                  | 4          | "pre-sessional" || 41805.00  || 0.00       || 0            || LocalDate.of(2012, 1, 5)
        LocalDate.of(1989, 2, 19)  | LocalDate.of(1989, 11, 17) | null                      | true     | 4448.59     | 693.45          | 549.17                | 8          | "main"          || 75430.97  || 0.00       || 0            || LocalDate.of(1990, 1, 17)
        LocalDate.of(2022, 11, 22) | LocalDate.of(2022, 11, 25) | LocalDate.of(2022, 2, 7)  | true     | 8655.07     | 7931.61         | 1442.91               | 1          | "main"          || 3258.46   || 1265.00    || 0            || LocalDate.of(2023, 1, 25)
        LocalDate.of(2041, 12, 13) | LocalDate.of(2042, 5, 11)  | LocalDate.of(2041, 9, 18) | true     | 3714.25     | 2078.81         | 1387.64               | 3          | "main"          || 24440.44  || 1265.00    || 0            || LocalDate.of(2042, 7, 11)
    }


    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2020, 12, 21) | LocalDate.of(2021, 1, 13)  | LocalDate.of(2020, 6, 10)  | false    | 7782.74     | 5673.53         | 273.57                | 5          | "main"          || 13050.64  || 0.00       || 0            || LocalDate.of(2021, 3, 13)
        LocalDate.of(2032, 10, 29) | LocalDate.of(2032, 12, 26) | LocalDate.of(2032, 7, 21)  | true     | 8282.28     | 9884.50         | 772.95                | 5          | "main"          || 14432.05  || 0.00       || 0            || LocalDate.of(2033, 1, 2)
        LocalDate.of(2001, 3, 11)  | LocalDate.of(2001, 12, 11) | LocalDate.of(2000, 10, 23) | false    | 8791.47     | 7608.61         | 864.05                | 4          | "main"          || 33933.81  || 0.00       || 9            || LocalDate.of(2002, 4, 11)
        LocalDate.of(2043, 12, 19) | LocalDate.of(2044, 11, 25) | null                       | true     | 8292.34     | 8637.29         | 945.42                | 6          | "main"          || 56069.58  || 0.00       || 9            || LocalDate.of(2045, 1, 25)
        LocalDate.of(2021, 3, 5)   | LocalDate.of(2021, 6, 4)   | null                       | false    | 6585.10     | 0.00            | 1076.32               | 0          | "pre-sessional" || 8553.78   || 0.00       || 0            || LocalDate.of(2021, 7, 4)
    }

    def "Tier 4 Non Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())

        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        if (feesCapped > 0) {
            assert jsonContent.cappedValues && jsonContent.cappedValues.accommodationFeesPaid != null
            assert jsonContent.cappedValues.accommodationFeesPaid == feesCapped
        } else {
            assert jsonContent.cappedValues == null || jsonContent.cappedValues.accommodationFeesPaid == null
        }

        if (courseCapped > 0) {
            assert jsonContent.cappedValues && jsonContent.cappedValues.courseLength != null
            assert jsonContent.cappedValues.courseLength == courseCapped
        } else {
            assert jsonContent.cappedValues == null || jsonContent.cappedValues.courseLength == null
        }

        if (feesCapped == 0 && courseCapped == 0) {
            assert jsonContent.cappedValues == null
        }

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1982, 1, 14)  | LocalDate.of(1982, 1, 30)  | null                       | true     | 3366.13     | 0.00            | 1815.20               | 0          | "pre-sessional" || 3366.13   || 1265.00    || 0            || LocalDate.of(1982, 2, 28)
        LocalDate.of(2006, 6, 18)  | LocalDate.of(2006, 12, 27) | LocalDate.of(2005, 7, 13)  | false    | 8263.55     | 0.00            | 0.00                  | 12         | "main"          || 88808.55  || 0.00       || 0            || LocalDate.of(2007, 4, 27)
        LocalDate.of(2024, 6, 16)  | LocalDate.of(2024, 6, 22)  | null                       | false    | 5260.11     | 0.00            | 0.00                  | 0          | "main"          || 6275.11   || 0.00       || 0            || LocalDate.of(2024, 6, 29)
        LocalDate.of(2033, 12, 30) | LocalDate.of(2034, 3, 29)  | LocalDate.of(2033, 7, 16)  | false    | 9201.12     | 3524.31         | 0.00                  | 6          | "main"          || 29121.81  || 0.00       || 0            || LocalDate.of(2034, 5, 29)
        LocalDate.of(2021, 8, 14)  | LocalDate.of(2021, 9, 4)   | null                       | false    | 1799.66     | 1590.73         | 662.13                | 0          | "main"          || 561.80    || 0.00       || 0            || LocalDate.of(2021, 9, 11)
        LocalDate.of(2018, 1, 21)  | LocalDate.of(2019, 1, 10)  | null                       | true     | 9414.74     | 0.00            | 0.00                  | 2          | "main"          || 36009.74  || 0.00       || 9            || LocalDate.of(2019, 3, 10)
        LocalDate.of(2022, 1, 29)  | LocalDate.of(2023, 1, 26)  | null                       | true     | 3879.58     | 0.00            | 1690.63               | 5          | "main"          || 52024.58  || 1265.00    || 9            || LocalDate.of(2023, 3, 26)
        LocalDate.of(2014, 9, 21)  | LocalDate.of(2015, 5, 17)  | LocalDate.of(2013, 12, 21) | true     | 7685.98     | 0.00            | 1892.59               | 12         | "main"          || 107800.98 || 1265.00    || 0            || LocalDate.of(2015, 9, 17)
        LocalDate.of(1993, 8, 6)   | LocalDate.of(1994, 8, 6)   | null                       | true     | 4615.15     | 0.00            | 972.39                | 9          | "pre-sessional" || 83472.76  || 0.00       || 9            || LocalDate.of(1994, 12, 6)
        LocalDate.of(1997, 6, 26)  | LocalDate.of(1997, 11, 22) | LocalDate.of(1996, 8, 3)   | true     | 2035.31     | 0.00            | 971.97                | 10         | "main"          || 83438.34  || 0.00       || 0            || LocalDate.of(1998, 3, 22)
        LocalDate.of(2041, 4, 16)  | LocalDate.of(2041, 10, 28) | null                       | false    | 7711.95     | 0.00            | 0.00                  | 5          | "pre-sessional" || 45416.95  || 0.00       || 0            || LocalDate.of(2041, 12, 28)
        LocalDate.of(1998, 7, 1)   | LocalDate.of(1999, 1, 18)  | null                       | true     | 5985.04     | 0.00            | 0.00                  | 0          | "main"          || 14840.04  || 0.00       || 0            || LocalDate.of(1999, 3, 18)
        LocalDate.of(1975, 1, 18)  | LocalDate.of(1975, 4, 12)  | LocalDate.of(1974, 10, 21) | true     | 3784.75     | 4635.35         | 1389.95               | 1          | "main"          || 5910.00   || 1265.00    || 0            || LocalDate.of(1975, 4, 19)
        LocalDate.of(1981, 4, 22)  | LocalDate.of(1981, 10, 23) | null                       | true     | 7535.53     | 9370.19         | 0.00                  | 2          | "pre-sessional" || 24065.00  || 0.00       || 0            || LocalDate.of(1981, 12, 23)
        LocalDate.of(2021, 10, 20) | LocalDate.of(2022, 11, 9)  | LocalDate.of(2020, 11, 3)  | false    | 215.85      | 4862.61         | 0.00                  | 0          | "main"          || 9135.00   || 0.00       || 9            || LocalDate.of(2023, 3, 9)
        LocalDate.of(1981, 10, 13) | LocalDate.of(1982, 1, 21)  | LocalDate.of(1981, 2, 28)  | true     | 4231.18     | 0.00            | 0.00                  | 1          | "main"          || 14361.18  || 0.00       || 0            || LocalDate.of(1982, 3, 21)
        LocalDate.of(1989, 8, 25)  | LocalDate.of(1990, 4, 11)  | LocalDate.of(1989, 5, 31)  | false    | 9746.65     | 0.00            | 460.95                | 10         | "main"          || 78605.70  || 0.00       || 0            || LocalDate.of(1990, 6, 11)
        LocalDate.of(2045, 6, 4)   | LocalDate.of(2045, 9, 18)  | null                       | false    | 9505.43     | 8466.91         | 0.00                  | 0          | "main"          || 5098.52   || 0.00       || 0            || LocalDate.of(2045, 9, 25)
        LocalDate.of(2038, 1, 1)   | LocalDate.of(2038, 5, 2)   | LocalDate.of(2037, 8, 5)   | true     | 6582.54     | 0.00            | 0.00                  | 10         | "main"          || 72057.54  || 0.00       || 0            || LocalDate.of(2038, 7, 2)
        LocalDate.of(1976, 4, 6)   | LocalDate.of(1976, 8, 28)  | null                       | true     | 1010.16     | 7019.97         | 0.00                  | 0          | "main"          || 6325.00   || 0.00       || 0            || LocalDate.of(1976, 9, 4)
    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN")
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFees")))

        where:
        inLondon | courseStartDate          | courseEndDate             | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | null                    | -2          | 1855.00         | 0          | 454.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                    | -0.05       | 4612.00         | 0          | 336.00
    }

    def "Tier 4 Non Doctorate - Check invalid characters intuition fees parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN")
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFees")))

        where:
        inLondon | courseStartDate          | courseEndDate            | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                    | "(*&"       | 4612.00         | 0          | 336.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                    | "hh"        | 2720.00         | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN")
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate             | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                    | 1855.00     | -2              | 0          | 454.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                    | 4612.00     | -0.05           | 0          | 336.00
    }

    def "Tier 4 Non Doctorate - Check invalid characters in tuition fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN")
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate            | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                    | 4612.00     | "*^"            | 0          | 336.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                    | 2720.00     | "kk"            | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid accommodation fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN")
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate             | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                    | 454.00      | 1855.00         | 0          | -2
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                    | 336.00      | 4612.00         | 0          | -0.05
    }

    def "Tier 4 Non Doctorate - Check invalid characters accommodation fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN")
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate            | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                    | 336.00      | 4612.00         | 0          | "*(^"
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                    | 1044.00     | 2720.00         | 0          | "hh"
    }

    def "Tier 4 Non Doctorate - Check invalid dependants parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN")
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate            | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                    | 454.00      | 1855.00         | -5         | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                    | 336.00      | 4612.00         | -99        | 0
    }

    def "Tier 4 Non Doctorate - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN")
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate             | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                    | 454.00      | 1855.00         | ")(&"      | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                    | 336.00      | 4612.00         | "h"        | 0
    }

}
