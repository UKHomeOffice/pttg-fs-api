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
        new MaintenanceThresholdCalculator(
            inLondonMaintenance,
            notInLondonMaintenance,
            maxMaintenanceAllowance,
            inLondonDependant,
            notInLondonDependant,
            nonDoctorateMinCourseLength,
            nonDoctorateMaxCourseLength,
            pgddSsoMinCourseLength,
            pgddSsoMaxCourseLength,
            doctorateFixedCourseLength
        ),
        getStudentTypeChecker(), getCourseTypeChecker(), serviceMessages, auditor, authenticator, 12, 2, 4
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
        jsonContent.threshold == threshold

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped
        LocalDate.of(2054, 11, 3)  | LocalDate.of(2055, 9, 25)  | null                       | false    | 2647.39     | 1824.03         | 111.69                | 12         | "pre-sessional" || 83286.67  || 0.00       || 9
        LocalDate.of(2019, 7, 9)   | LocalDate.of(2020, 3, 5)   | null                       | false    | 6971.95     | 1506.03         | 631.15                | 8          | "MAIN"          || 61914.77  || 0.00       || 0
        LocalDate.of(2046, 2, 1)   | LocalDate.of(2046, 6, 30)  | LocalDate.of(2045, 4, 26)  | false    | 2440.54     | 3462.02         | 1428.99               | 6          | "MAIN"          || 40530.00  || 1265.00    || 0
        LocalDate.of(2021, 9, 21)  | LocalDate.of(2022, 9, 2)   | null                       | false    | 5319.46     | 4610.38         | 1072.18               | 14         | "pre-sessional" || 94451.90  || 0.00       || 9
        LocalDate.of(1980, 9, 24)  | LocalDate.of(1980, 11, 26) | null                       | false    | 863.92      | 7560.20         | 1022.75               | 0          | "MAIN"          || 2022.25   || 0.00       || 0
        LocalDate.of(1992, 3, 4)   | LocalDate.of(1992, 11, 27) | LocalDate.of(1991, 7, 15)  | false    | 7899.81     | 9302.17         | 1523.80               | 4          | "MAIN"          || 32350.00  || 1265.00    || 0
        LocalDate.of(1988, 2, 24)  | LocalDate.of(1988, 10, 9)  | null                       | false    | 6530.41     | 3348.66         | 779.18                | 2          | "pre-sessional" || 22762.57  || 0.00       || 0
        LocalDate.of(2022, 9, 16)  | LocalDate.of(2023, 3, 20)  | null                       | false    | 4744.19     | 6510.78         | 1919.08               | 2          | "pre-sessional" || 18080.00  || 1265.00    || 0
        LocalDate.of(2007, 11, 24) | LocalDate.of(2008, 4, 15)  | null                       | false    | 2311.25     | 4481.28         | 875.12                | 0          | "pre-sessional" || 4199.88   || 0.00       || 0
        LocalDate.of(2036, 3, 4)   | LocalDate.of(2036, 10, 23) | LocalDate.of(2035, 12, 5)  | false    | 6091.41     | 3117.24         | 58.82                 | 13         | "MAIN"          || 90595.35  || 0.00       || 0
        LocalDate.of(2009, 2, 13)  | LocalDate.of(2009, 11, 20) | LocalDate.of(2008, 3, 7)   | false    | 4220.61     | 4242.61         | 1622.62               | 2          | "MAIN"          || 20110.00  || 1265.00    || 9
        LocalDate.of(2052, 12, 29) | LocalDate.of(2053, 12, 19) | LocalDate.of(2052, 10, 19) | false    | 1347.65     | 6229.42         | 1869.26               | 3          | "MAIN"          || 26230.00  || 1265.00    || 9
        LocalDate.of(1977, 4, 29)  | LocalDate.of(1977, 9, 3)   | LocalDate.of(1976, 5, 21)  | false    | 373.26      | 6945.01         | 1042.24               | 4          | "MAIN"          || 28512.76  || 0.00       || 0
    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped
        LocalDate.of(1996, 8, 25)  | LocalDate.of(1997, 7, 21)  | null                       | true     | 2823.28     | 9013.39         | 1416.13               | 6          | "pre-sessional" || 55750.00  || 1265.00    || 9
        LocalDate.of(2019, 3, 15)  | LocalDate.of(2019, 9, 11)  | null                       | true     | 9491.47     | 6708.87         | 1453.33               | 0          | "pre-sessional" || 9107.60   || 1265.00    || 0
        LocalDate.of(2007, 12, 6)  | LocalDate.of(2008, 2, 16)  | LocalDate.of(2007, 4, 14)  | true     | 9843.80     | 545.74          | 411.11                | 7          | "MAIN"          || 42256.95  || 0.00       || 0
        LocalDate.of(2014, 10, 17) | LocalDate.of(2015, 2, 21)  | LocalDate.of(2014, 9, 24)  | true     | 4475.87     | 288.91          | 278.04                | 7          | "MAIN"          || 39808.92  || 0.00       || 0
        LocalDate.of(2004, 10, 8)  | LocalDate.of(2004, 11, 15) | LocalDate.of(2003, 11, 29) | true     | 1626.34     | 5714.44         | 797.08                | 14         | "MAIN"          || 49052.92  || 0.00       || 0
        LocalDate.of(1986, 11, 7)  | LocalDate.of(1987, 9, 17)  | null                       | true     | 7110.72     | 4064.43         | 1986.92               | 3          | "MAIN"          || 35981.29  || 1265.00    || 9
        LocalDate.of(1977, 8, 27)  | LocalDate.of(1978, 1, 13)  | null                       | true     | 125.38      | 6579.99         | 1150.86               | 0          | "MAIN"          || 5174.14   || 0.00       || 0
    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped
        LocalDate.of(1996, 8, 25)  | LocalDate.of(1997, 7, 21)  | null                       | true     | 2823.28     | 9013.39         | 1416.13               | 6          | "pre-sessional" || 55750.00  || 1265.00    || 9
        LocalDate.of(2054, 11, 3)  | LocalDate.of(2055, 9, 25)  | null                       | false    | 2647.39     | 1824.03         | 111.69                | 12         | "pre-sessional" || 83286.67  || 0.00       || 9
        LocalDate.of(2019, 3, 15)  | LocalDate.of(2019, 9, 11)  | null                       | true     | 9491.47     | 6708.87         | 1453.33               | 0          | "pre-sessional" || 9107.60   || 1265.00    || 0
        LocalDate.of(2019, 7, 9)   | LocalDate.of(2020, 3, 5)   | null                       | false    | 6971.95     | 1506.03         | 631.15                | 8          | "MAIN"          || 61914.77  || 0.00       || 0
        LocalDate.of(1986, 11, 7)  | LocalDate.of(1987, 9, 17)  | null                       | true     | 7110.72     | 4064.43         | 1986.92               | 3          | "MAIN"          || 35981.29  || 1265.00    || 9
        LocalDate.of(2052, 12, 29) | LocalDate.of(2053, 12, 19) | LocalDate.of(2052, 10, 19) | false    | 1347.65     | 6229.42         | 1869.26               | 3          | "MAIN"          || 26230.00  || 1265.00    || 9
        LocalDate.of(1977, 8, 27)  | LocalDate.of(1978, 1, 13)  | null                       | true     | 125.38      | 6579.99         | 1150.86               | 0          | "MAIN"          || 5174.14   || 0.00       || 0
        LocalDate.of(1977, 4, 29)  | LocalDate.of(1977, 9, 3)   | LocalDate.of(1976, 5, 21)  | false    | 373.26      | 6945.01         | 1042.24               | 4          | "MAIN"          || 28512.76  || 0.00       || 0
    }


    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped
        LocalDate.of(1978, 6, 15)  | LocalDate.of(1978, 8, 28)  | null                       | true     | 4611.60     | 7857.51         | 1421.57               | 0          | "pre-sessional" || 2530.00   || 1265.00    || 0
        LocalDate.of(1976, 9, 25)  | LocalDate.of(1976, 12, 20) | LocalDate.of(1976, 4, 28)  | true     | 3684.19     | 2185.65         | 250.16                | 14         | "MAIN"          || 64193.38  || 0.00       || 0
        LocalDate.of(1980, 7, 2)   | LocalDate.of(1981, 4, 23)  | LocalDate.of(1980, 6, 11)  | false    | 3298.51     | 7408.46         | 121.11                | 10         | "MAIN"          || 70213.89  || 0.00       || 9
        LocalDate.of(2037, 11, 23) | LocalDate.of(2038, 3, 21)  | LocalDate.of(2037, 5, 22)  | true     | 2701.81     | 3514.02         | 1841.44               | 2          | "MAIN"          || 13935.00  || 1265.00    || 0
        LocalDate.of(2015, 6, 24)  | LocalDate.of(2016, 7, 25)  | null                       | true     | 5185.40     | 7062.12         | 860.11                | 13         | "MAIN"          || 109389.89 || 0.00       || 9
        LocalDate.of(2023, 4, 25)  | LocalDate.of(2023, 5, 1)   | LocalDate.of(2023, 4, 20)  | true     | 4535.25     | 4095.34         | 915.72                | 0          | "MAIN"          || 789.19    || 0.00       || 0
        LocalDate.of(1986, 1, 6)   | LocalDate.of(1987, 1, 30)  | LocalDate.of(1984, 12, 20) | true     | 3307.48     | 1283.33         | 1042.50               | 9          | "MAIN"          || 80811.65  || 0.00       || 9
        LocalDate.of(1998, 11, 25) | LocalDate.of(1999, 3, 31)  | null                       | false    | 5013.42     | 3390.23         | 1733.52               | 0          | "MAIN"          || 5433.19   || 1265.00    || 0
        LocalDate.of(2051, 8, 19)  | LocalDate.of(2052, 5, 24)  | LocalDate.of(2051, 4, 22)  | false    | 5978.71     | 5946.69         | 1854.43               | 6          | "MAIN"          || 44622.02  || 1265.00    || 9
        LocalDate.of(2054, 8, 14)  | LocalDate.of(2055, 1, 18)  | null                       | true     | 1657.49     | 9.51            | 601.39                | 0          | "pre-sessional" || 8636.59   || 0.00       || 0
        LocalDate.of(1989, 2, 20)  | LocalDate.of(1989, 11, 1)  | null                       | false    | 9368.28     | 1717.03         | 986.92                | 4          | "MAIN"          || 40279.33  || 0.00       || 0
        LocalDate.of(2007, 2, 26)  | LocalDate.of(2008, 2, 4)   | LocalDate.of(2006, 12, 18) | false    | 477.01      | 664.94          | 403.93                | 8          | "MAIN"          || 57691.07  || 0.00       || 9
    }

    def "Tier 4 Non Doctorate - Check 'Leave to remain values'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveToRemain == leaveToRemain.toString()

        where:
        courseStartDate           | courseEndDate              | originalCourseStartDate   | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1987, 1, 11) | LocalDate.of(1987, 6, 22)  | null                      | false    | 7237.58     | 9560.93         | 1716.10               | 0          | "main"     || 4825.00   || 1265.00    || 0            || LocalDate.of(1987, 6, 29)
        LocalDate.of(2044, 4, 9)  | LocalDate.of(2045, 3, 5)   | LocalDate.of(2043, 3, 16) | false    | 3332.32     | 0.00            | 0.00                  | 4          | "main"     || 36947.32  || 0.00       || 9            || LocalDate.of(2045, 7, 5)
        LocalDate.of(2024, 1, 5)  | LocalDate.of(2024, 5, 26)  | null                      | false    | 777.00      | 0.00            | 0.00                  | 0          | "main"     || 5852.00   || 0.00       || 0            || LocalDate.of(2024, 6, 2)
        LocalDate.of(2051, 6, 7)  | LocalDate.of(2052, 3, 27)  | LocalDate.of(2050, 8, 5)  | false    | 8883.53     | 0.00            | 654.79                | 13         | "main"     || 96923.74  || 0.00       || 9            || LocalDate.of(2052, 7, 27)
        LocalDate.of(2051, 2, 25) | LocalDate.of(2051, 3, 23)  | LocalDate.of(2051, 2, 13) | true     | 7945.40     | 0.00            | 0.00                  | 3          | "main"     || 14280.40  || 0.00       || 0            || LocalDate.of(2051, 3, 30)
        LocalDate.of(2052, 7, 17) | LocalDate.of(2052, 8, 1)   | null                      | true     | 3820.53     | 0.00            | 0.00                  | 0          | "main"     || 5085.53   || 0.00       || 0            || LocalDate.of(2052, 8, 8)
        LocalDate.of(1974, 8, 15) | LocalDate.of(1974, 11, 24) | null                      | true     | 1306.75     | 9751.17         | 1872.37               | 0          | "main"     || 3795.00   || 1265.00    || 0            || LocalDate.of(1974, 12, 1)
        LocalDate.of(1985, 2, 17) | LocalDate.of(1986, 1, 22)  | LocalDate.of(1985, 1, 26) | false    | 8356.47     | 7002.99         | 0.00                  | 1          | "main"     || 16608.48  || 0.00       || 9            || LocalDate.of(1986, 3, 22)
        LocalDate.of(2004, 2, 13) | LocalDate.of(2004, 12, 19) | LocalDate.of(2003, 9, 23) | true     | 8276.69     | 9238.26         | 811.12                | 1          | "main"     || 18178.88  || 0.00       || 9            || LocalDate.of(2005, 4, 19)
    }

    def "Tier 4 Non Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())

        assert jsonContent.threshold == threshold
        assert jsonContent.leaveToRemain == leaveToRemain.toString()

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
            assert jsonContent.cappedvalues == null
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
