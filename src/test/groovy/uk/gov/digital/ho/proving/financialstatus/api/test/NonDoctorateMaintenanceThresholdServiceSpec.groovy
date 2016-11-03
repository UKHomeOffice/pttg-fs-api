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

    def thresholdService = new ThresholdService(
        new MaintenanceThresholdCalculator(inLondonMaintenance, notInLondonMaintenance,
            maxMaintenanceAllowance, inLondonDependant, notInLondonDependant,
            nonDoctorateMinCourseLength, nonDoctorateMaxCourseLength, nonDoctorateMinCourseLengthWithDependants,
            pgddSsoMinCourseLength, pgddSsoMaxCourseLength, doctorateFixedCourseLength
        ), getStudentTypeChecker(), serviceMessages, auditor, 12, 2, 4
    )

    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(), serviceMessages))
        .build()


    def url = TestUtils.thresholdUrl

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("continuationEndDate", (continuationEndDate == null) ? "" : continuationEndDate.toString())
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
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | 7307.00     | 0.00            | 0.00                  | 2          || 26652.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                | 5878.00     | 0.00            | 0.00                  | 4          || 38478.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                | 9180.00     | 0.00            | 0.00                  | 10         || 79515.00

    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | 12672.00    | 0.00            | 0.00                  | 13         || 120392.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                | 14618.00    | 0.00            | 0.00                  | 10         || 100788.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                | 11896.00    | 0.00            | 0.00                  | 3          || 46096.00

    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | 9870.00     | 4713.00         | 0.00                  | 12         || 85702.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                | 11031.00    | 395.00          | 0.00                  | 12         || 92196.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                | 12972.00    | 4774.00         | 0.00                  | 5          || 47933.00

    }


    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | 10337.00    | 0.00            | 620.00                | 7          || 59662.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                | 6749.00     | 0.00            | 485.00                | 6          || 51104.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                | 6242.00     | 0.00            | 917.00                | 2          || 26700.00

    }

    def "Tier 4 Non Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())

        assert jsonContent.threshold == threshold
        if (feesCapped > 0) {
            assert jsonContent.cappedValues && jsonContent.cappedValues.accommodationFeesPaid != null
            assert jsonContent.cappedValues.accommodationFeesPaid == feesCapped
        } else {
            assert jsonContent.cappedValues == null || jsonContent.cappedValues.accommodationFeesPaid == null
        }

        if (courseLengthCapped > 0) {
            assert jsonContent.cappedValues && jsonContent.cappedValues.courseLength != null
            assert jsonContent.cappedValues.courseLength == courseLengthCapped
        } else {
            assert jsonContent.cappedValues == null || jsonContent.cappedValues.courseLength == null
        }

        if (continuationLengthCapped > 0) {
            assert jsonContent.cappedValues && jsonContent.cappedValues.continuationLength != null
            assert jsonContent.cappedValues.continuationLength == continuationLengthCapped
        } else {
            assert jsonContent.cappedValues == null || jsonContent.cappedValues.continuationLengthCapped == null
        }


        if (feesCapped == 0 && courseLengthCapped == 0 && continuationLengthCapped == 0) {
            assert jsonContent.cappedvalues == null
        }

        where:
        courseStartDate            | courseEndDate              | continuationEndDate        | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold || feesCapped || courseLengthCapped || continuationLengthCapped
        LocalDate.of(2026, 11, 6)  | LocalDate.of(2027, 3, 30)  | LocalDate.of(2028, 4, 19)  | false    | 8684.70     | 2229.62         | 1023.26               | 2          || 26806.82  || 0          || 0                  || 9
        LocalDate.of(2050, 1, 3)   | LocalDate.of(2050, 4, 1)   | null                       | true     | 2278.82     | 1838.25         | 743.75                | 0          || 3491.82   || 0          || 0                  || 0
        LocalDate.of(2018, 9, 14)  | LocalDate.of(2019, 5, 2)   | LocalDate.of(2020, 1, 3)   | true     | 6859.43     | 8267.82         | 1547.98               | 1          || 17725.00  || 1265       || 0                  || 0
        LocalDate.of(2053, 10, 10) | LocalDate.of(2053, 12, 27) | null                       | true     | 128.44      | 6770.43         | 1978.36               | 0          || 2530.00   || 1265       || 0                  || 0
        LocalDate.of(2033, 8, 22)  | LocalDate.of(2034, 8, 11)  | LocalDate.of(2035, 5, 16)  | true     | 4896.58     | 6128.68         | 1419.93               | 11         || 93775.00  || 1265       || 0                  || 9
        LocalDate.of(2002, 8, 9)   | LocalDate.of(2003, 3, 17)  | LocalDate.of(2003, 4, 20)  | true     | 7220.57     | 2178.83         | 1882.33               | 5          || 23206.74  || 1265       || 0                  || 0
        LocalDate.of(2033, 5, 7)   | LocalDate.of(2034, 5, 4)   | LocalDate.of(2035, 5, 20)  | true     | 7218.40     | 9267.38         | 1296.44               | 11         || 93775.00  || 1265       || 0                  || 9
        LocalDate.of(2021, 1, 5)   | LocalDate.of(2021, 7, 22)  | LocalDate.of(2021, 11, 10) | false    | 6047.41     | 9968.15         | 739.07                | 10         || 44120.93  || 0          || 0                  || 0
        LocalDate.of(2032, 4, 8)   | LocalDate.of(2033, 3, 21)  | null                       | true     | 1413.13     | 95.80           | 1712.24               | 14         || 117907.33 || 1265       || 9                  || 0
        LocalDate.of(2000, 2, 14)  | LocalDate.of(2000, 4, 12)  | null                       | true     | 3512.21     | 7267.58         | 1299.83               | 0          || 1265.00   || 1265       || 0                  || 0
        LocalDate.of(2042, 5, 8)   | LocalDate.of(2042, 5, 14)  | LocalDate.of(2043, 4, 9)   | false    | 5176.87     | 6369.59         | 225.44                | 14         || 94589.56  || 0          || 0                  || 9
        LocalDate.of(2019, 1, 25)  | LocalDate.of(2020, 1, 6)   | null                       | false    | 2021.46     | 8831.97         | 1773.25               | 9          || 62950.00  || 1265       || 9                  || 0
        LocalDate.of(2005, 7, 2)   | LocalDate.of(2005, 8, 8)   | null                       | true     | 2667.28     | 9052.54         | 479.60                | 0          || 2050.40   || 0          || 0                  || 0
        LocalDate.of(2053, 4, 11)  | LocalDate.of(2053, 12, 4)  | LocalDate.of(2054, 9, 4)   | true     | 1425.04     | 2353.40         | 1512.81               | 14         || 116590.00 || 1265       || 0                  || 0
        LocalDate.of(2029, 1, 31)  | LocalDate.of(2029, 9, 15)  | LocalDate.of(2030, 7, 12)  | false    | 7471.76     | 8925.37         | 1061.32               | 6          || 44793.68  || 0          || 0                  || 9
        LocalDate.of(2043, 1, 18)  | LocalDate.of(2043, 9, 12)  | LocalDate.of(2043, 9, 25)  | false    | 5669.49     | 7709.83         | 1085.53               | 5          || 10129.47  || 0          || 0                  || 0
        LocalDate.of(2026, 8, 3)   | LocalDate.of(2027, 7, 27)  | null                       | true     | 5278.71     | 2943.90         | 1601.25               | 12         || 103714.81 || 1265       || 9                  || 0
        LocalDate.of(1988, 7, 26)  | LocalDate.of(1989, 4, 26)  | LocalDate.of(1989, 8, 1)   | false    | 1342.69     | 5396.66         | 1495.96               | 7          || 40875.00  || 1265       || 0                  || 0
        LocalDate.of(2001, 4, 20)  | LocalDate.of(2001, 6, 16)  | LocalDate.of(2001, 12, 10) | false    | 9721.64     | 68.69           | 383.65                | 2          || 26239.30  || 0          || 0                  || 0
        LocalDate.of(2051, 1, 1)   | LocalDate.of(2051, 6, 12)  | LocalDate.of(2051, 7, 1)   | true     | 4452.02     | 9265.89         | 1540.57               | 6          || 15210.00  || 1265       || 0                  || 0
        LocalDate.of(2007, 5, 3)   | LocalDate.of(2007, 5, 24)  | LocalDate.of(2007, 5, 26)  | false    | 5837.16     | 9712.35         | 1122.51               | 9          || 18252.49  || 0          || 0                  || 0
        LocalDate.of(2039, 3, 14)  | LocalDate.of(2040, 4, 14)  | null                       | false    | 9528.85     | 7873.90         | 330.50                | 9          || 65539.45  || 0          || 9                  || 0
        LocalDate.of(2053, 5, 28)  | LocalDate.of(2053, 8, 16)  | null                       | false    | 7647.18     | 9738.94         | 1424.47               | 0          || 1780.00   || 1265       || 0                  || 0
        LocalDate.of(1998, 5, 26)  | LocalDate.of(1998, 11, 14) | LocalDate.of(1999, 1, 15)  | true     | 6873.44     | 9212.10         | 62.80                 | 11         || 50207.20  || 0          || 0                  || 0
        LocalDate.of(1983, 12, 8)  | LocalDate.of(1984, 10, 15) | null                       | false    | 1888.35     | 224.03          | 1725.79               | 8          || 58494.32  || 1265       || 9                  || 0
        LocalDate.of(2041, 6, 1)   | LocalDate.of(2042, 1, 8)   | null                       | true     | 6136.11     | 3533.23         | 1169.03               | 2          || 26763.85  || 0          || 0                  || 0
        LocalDate.of(1986, 3, 17)  | LocalDate.of(1987, 2, 26)  | null                       | false    | 4908.01     | 507.67          | 1811.31               | 10         || 73470.34  || 1265       || 9                  || 0
        LocalDate.of(2024, 8, 18)  | LocalDate.of(2024, 8, 21)  | LocalDate.of(2025, 8, 26)  | true     | 1489.33     | 7655.00         | 1015.07               | 11         || 94024.93  || 0          || 0                  || 9
        LocalDate.of(1973, 11, 18) | LocalDate.of(1974, 8, 4)   | null                       | false    | 3296.05     | 5925.11         | 1271.41               | 10         || 69070.00  || 1265       || 0                  || 0
        LocalDate.of(2043, 10, 27) | LocalDate.of(2044, 8, 16)  | null                       | false    | 7639.35     | 7465.44         | 1497.73               | 13         || 87603.91  || 1265       || 9                  || 0
        LocalDate.of(1978, 6, 4)   | LocalDate.of(1979, 5, 31)  | null                       | true     | 7698.27     | 9909.92         | 1878.56               | 5          || 48145.00  || 1265       || 9                  || 0
        LocalDate.of(1975, 6, 12)  | LocalDate.of(1975, 11, 2)  | LocalDate.of(1976, 5, 7)   | false    | 2307.68     | 18.02           | 505.85                | 10         || 70088.81  || 0          || 0                  || 0
        LocalDate.of(1988, 4, 10)  | LocalDate.of(1988, 5, 3)   | null                       | true     | 3711.66     | 2722.27         | 471.94                | 0          || 1782.45   || 0          || 0                  || 0
        LocalDate.of(2042, 4, 22)  | LocalDate.of(2043, 1, 1)   | null                       | true     | 7788.74     | 601.21          | 765.97                | 8          || 78646.56  || 0          || 0                  || 0
        LocalDate.of(2042, 12, 2)  | LocalDate.of(2042, 12, 7)  | LocalDate.of(2043, 5, 7)   | false    | 9696.12     | 6264.96         | 1506.08               | 7          || 40561.16  || 1265       || 0                  || 0
        LocalDate.of(1998, 7, 14)  | LocalDate.of(1999, 3, 12)  | LocalDate.of(1999, 8, 25)  | true     | 3538.78     | 9002.79         | 712.03                | 11         || 90532.97  || 0          || 0                  || 0
        LocalDate.of(2040, 1, 13)  | LocalDate.of(2040, 10, 9)  | null                       | true     | 1164.49     | 5620.33         | 1106.20               | 13         || 109143.80 || 0          || 0                  || 0
        LocalDate.of(2032, 3, 24)  | LocalDate.of(2032, 7, 24)  | null                       | true     | 4355.88     | 654.41          | 314.79                | 0          || 9711.68   || 0          || 0                  || 0
        LocalDate.of(2006, 5, 9)   | LocalDate.of(2007, 5, 30)  | null                       | false    | 3794.16     | 9979.74         | 878.49                | 4          || 32736.51  || 0          || 9                  || 0
        LocalDate.of(2044, 10, 18) | LocalDate.of(2045, 10, 19) | LocalDate.of(2046, 10, 26) | true     | 7384.45     | 8891.79         | 1929.46               | 13         || 108985.00 || 1265       || 0                  || 9
        LocalDate.of(2006, 7, 9)   | LocalDate.of(2006, 12, 22) | LocalDate.of(2007, 2, 4)   | false    | 5966.78     | 6943.60         | 1051.40               | 1          || 3698.60   || 0          || 0                  || 0
        LocalDate.of(2037, 7, 9)   | LocalDate.of(2038, 6, 6)   | LocalDate.of(2038, 10, 21) | true     | 5977.83     | 448.57          | 1447.56               | 0          || 10589.26  || 1265       || 0                  || 0
        LocalDate.of(2038, 8, 5)   | LocalDate.of(2039, 6, 29)  | LocalDate.of(2039, 7, 28)  | true     | 9034.72     | 6383.53         | 883.23                | 12         || 53732.96  || 0          || 0                  || 0
        LocalDate.of(1984, 11, 20) | LocalDate.of(1985, 1, 27)  | LocalDate.of(1985, 11, 13) | false    | 8691.82     | 3588.33         | 1283.49               | 6          || 49693.49  || 1265       || 0                  || 9
        LocalDate.of(1980, 5, 31)  | LocalDate.of(1980, 7, 24)  | LocalDate.of(1980, 12, 4)  | true     | 1963.52     | 4514.84         | 413.79                | 4          || 29571.21  || 0          || 0                  || 0
        LocalDate.of(1976, 5, 21)  | LocalDate.of(1977, 5, 12)  | LocalDate.of(1977, 9, 29)  | false    | 4339.90     | 905.63          | 1925.52               | 0          || 7244.27   || 1265       || 0                  || 0
        LocalDate.of(2020, 9, 3)   | LocalDate.of(2021, 7, 26)  | LocalDate.of(2022, 8, 13)  | true     | 539.72      | 3481.47         | 840.54                | 14         || 117014.46 || 0          || 0                  || 9
        LocalDate.of(2051, 9, 5)   | LocalDate.of(2052, 8, 31)  | LocalDate.of(2053, 1, 18)  | true     | 6575.07     | 1322.46         | 729.62                | 1          || 18452.99  || 0          || 0                  || 0
        LocalDate.of(1996, 12, 22) | LocalDate.of(1997, 9, 9)   | LocalDate.of(1998, 6, 10)  | false    | 2838.57     | 520.63          | 618.70                | 6          || 47554.24  || 0          || 0                  || 9
        LocalDate.of(1996, 4, 11)  | LocalDate.of(1996, 5, 30)  | LocalDate.of(1997, 2, 16)  | false    | 4982.82     | 8134.18         | 1021.24               | 3          || 26473.76  || 0          || 0                  || 0
    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFees")))

        where:
        inLondon | courseStartDate          | courseEndDate             | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | null                | -2          | 1855.00         | 0          | 454.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                | -0.05       | 4612.00         | 0          | 336.00
    }

    def "Tier 4 Non Doctorate - Check invalid characters intuition fees parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFees")))

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                | "(*&"       | 4612.00         | 0          | 336.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | "hh"        | 2720.00         | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate             | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                | 1855.00     | -2              | 0          | 454.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                | 4612.00     | -0.05           | 0          | 336.00
    }

    def "Tier 4 Non Doctorate - Check invalid characters in tuition fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                | 4612.00     | "*^"            | 0          | 336.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                | 2720.00     | "kk"            | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid accommodation fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate             | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                | 454.00      | 1855.00         | 0          | -2
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                | 336.00      | 4612.00         | 0          | -0.05
    }

    def "Tier 4 Non Doctorate - Check invalid characters accommodation fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                | 336.00      | 4612.00         | 0          | "*(^"
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                | 1044.00     | 2720.00         | 0          | "hh"
    }

    def "Tier 4 Non Doctorate - Check invalid dependants parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                | 454.00      | 1855.00         | -5         | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                | 336.00      | 4612.00         | -99        | 0
    }

    def "Tier 4 Non Doctorate - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate             | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                | 454.00      | 1855.00         | ")(&"      | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                | 336.00      | 4612.00         | "h"        | 0
    }

}
