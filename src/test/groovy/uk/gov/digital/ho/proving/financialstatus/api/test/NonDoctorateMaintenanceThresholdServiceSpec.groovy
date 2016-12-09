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
        getStudentTypeChecker(), serviceMessages, auditor, authenticator, 12, 2, 4
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
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(2054, 11, 3)  | LocalDate.of(2055, 9, 25)  | null                       | false    | 2647.39     | 1824.03         | 111.69                | 12         | "PRE"      || 83286.67  || 0.00       || 9
        LocalDate.of(2019, 7, 9)   | LocalDate.of(2020, 3, 5)   | null                       | false    | 6971.95     | 1506.03         | 631.15                | 8          | "MAIN"     || 61914.77  || 0.00       || 0
        LocalDate.of(2046, 2, 1)   | LocalDate.of(2046, 6, 30)  | LocalDate.of(2045, 4, 26)  | false    | 2440.54     | 3462.02         | 1428.99               | 6          | "MAIN"     || 40530.00  || 1265.00    || 0
        LocalDate.of(2021, 9, 21)  | LocalDate.of(2022, 9, 2)   | null                       | false    | 5319.46     | 4610.38         | 1072.18               | 14         | "PRE"      || 94451.90  || 0.00       || 9
        LocalDate.of(1980, 9, 24)  | LocalDate.of(1980, 11, 26) | null                       | false    | 863.92      | 7560.20         | 1022.75               | 0          | "MAIN"     || 2022.25   || 0.00       || 0
        LocalDate.of(1992, 3, 4)   | LocalDate.of(1992, 11, 27) | LocalDate.of(1991, 7, 15)  | false    | 7899.81     | 9302.17         | 1523.80               | 4          | "MAIN"     || 32350.00  || 1265.00    || 0
        LocalDate.of(1988, 2, 24)  | LocalDate.of(1988, 10, 9)  | null                       | false    | 6530.41     | 3348.66         | 779.18                | 2          | "PRE"      || 22762.57  || 0.00       || 0
        LocalDate.of(2022, 9, 16)  | LocalDate.of(2023, 3, 20)  | null                       | false    | 4744.19     | 6510.78         | 1919.08               | 2          | "PRE"      || 18080.00  || 1265.00    || 0
        LocalDate.of(2007, 11, 24) | LocalDate.of(2008, 4, 15)  | null                       | false    | 2311.25     | 4481.28         | 875.12                | 0          | "PRE"      || 4199.88   || 0.00       || 0
        LocalDate.of(2036, 3, 4)   | LocalDate.of(2036, 10, 23) | LocalDate.of(2035, 12, 5)  | false    | 6091.41     | 3117.24         | 58.82                 | 13         | "MAIN"     || 90595.35  || 0.00       || 0
        LocalDate.of(2009, 2, 13)  | LocalDate.of(2009, 11, 20) | LocalDate.of(2008, 3, 7)   | false    | 4220.61     | 4242.61         | 1622.62               | 2          | "MAIN"     || 20110.00  || 1265.00    || 9
        LocalDate.of(2052, 12, 29) | LocalDate.of(2053, 12, 19) | LocalDate.of(2052, 10, 19) | false    | 1347.65     | 6229.42         | 1869.26               | 3          | "MAIN"     || 26230.00  || 1265.00    || 9
        LocalDate.of(1977, 4, 29)  | LocalDate.of(1977, 9, 3)   | LocalDate.of(1976, 5, 21)  | false    | 373.26      | 6945.01         | 1042.24               | 4          | "MAIN"     || 28512.76  || 0.00       || 0
    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(1996, 8, 25)  | LocalDate.of(1997, 7, 21)  | null                       | true     | 2823.28     | 9013.39         | 1416.13               | 6          | "PRE"      || 55750.00  || 1265.00    || 9
        LocalDate.of(2019, 3, 15)  | LocalDate.of(2019, 9, 11)  | null                       | true     | 9491.47     | 6708.87         | 1453.33               | 0          | "PRE"      || 9107.60   || 1265.00    || 0
        LocalDate.of(2007, 12, 6)  | LocalDate.of(2008, 2, 16)  | LocalDate.of(2007, 4, 14)  | true     | 9843.80     | 545.74          | 411.11                | 7          | "MAIN"     || 42256.95  || 0.00       || 0
        LocalDate.of(2014, 10, 17) | LocalDate.of(2015, 2, 21)  | LocalDate.of(2014, 9, 24)  | true     | 4475.87     | 288.91          | 278.04                | 7          | "MAIN"     || 39808.92  || 0.00       || 0
        LocalDate.of(2004, 10, 8)  | LocalDate.of(2004, 11, 15) | LocalDate.of(2003, 11, 29) | true     | 1626.34     | 5714.44         | 797.08                | 14         | "MAIN"     || 49052.92  || 0.00       || 0
        LocalDate.of(1986, 11, 7)  | LocalDate.of(1987, 9, 17)  | null                       | true     | 7110.72     | 4064.43         | 1986.92               | 3          | "MAIN"     || 35981.29  || 1265.00    || 9
        LocalDate.of(1977, 8, 27)  | LocalDate.of(1978, 1, 13)  | null                       | true     | 125.38      | 6579.99         | 1150.86               | 0          | "MAIN"     || 5174.14   || 0.00       || 0
    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(1996, 8, 25)  | LocalDate.of(1997, 7, 21)  | null                       | true     | 2823.28     | 9013.39         | 1416.13               | 6          | "PRE"      || 55750.00  || 1265.00    || 9
        LocalDate.of(2054, 11, 3)  | LocalDate.of(2055, 9, 25)  | null                       | false    | 2647.39     | 1824.03         | 111.69                | 12         | "PRE"      || 83286.67  || 0.00       || 9
        LocalDate.of(2019, 3, 15)  | LocalDate.of(2019, 9, 11)  | null                       | true     | 9491.47     | 6708.87         | 1453.33               | 0          | "PRE"      || 9107.60   || 1265.00    || 0
        LocalDate.of(2019, 7, 9)   | LocalDate.of(2020, 3, 5)   | null                       | false    | 6971.95     | 1506.03         | 631.15                | 8          | "MAIN"     || 61914.77  || 0.00       || 0
        LocalDate.of(1986, 11, 7)  | LocalDate.of(1987, 9, 17)  | null                       | true     | 7110.72     | 4064.43         | 1986.92               | 3          | "MAIN"     || 35981.29  || 1265.00    || 9
        LocalDate.of(2052, 12, 29) | LocalDate.of(2053, 12, 19) | LocalDate.of(2052, 10, 19) | false    | 1347.65     | 6229.42         | 1869.26               | 3          | "MAIN"     || 26230.00  || 1265.00    || 9
        LocalDate.of(1977, 8, 27)  | LocalDate.of(1978, 1, 13)  | null                       | true     | 125.38      | 6579.99         | 1150.86               | 0          | "MAIN"     || 5174.14   || 0.00       || 0
        LocalDate.of(1977, 4, 29)  | LocalDate.of(1977, 9, 3)   | LocalDate.of(1976, 5, 21)  | false    | 373.26      | 6945.01         | 1042.24               | 4          | "MAIN"     || 28512.76  || 0.00       || 0
    }


    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(1978, 6, 15)  | LocalDate.of(1978, 8, 28)  | null                       | true     | 4611.60     | 7857.51         | 1421.57               | 0          | "PRE"      || 2530.00   || 1265.00    || 0
        LocalDate.of(1976, 9, 25)  | LocalDate.of(1976, 12, 20) | LocalDate.of(1976, 4, 28)  | true     | 3684.19     | 2185.65         | 250.16                | 14         | "MAIN"     || 64193.38  || 0.00       || 0
        LocalDate.of(1980, 7, 2)   | LocalDate.of(1981, 4, 23)  | LocalDate.of(1980, 6, 11)  | false    | 3298.51     | 7408.46         | 121.11                | 10         | "MAIN"     || 70213.89  || 0.00       || 9
        LocalDate.of(2037, 11, 23) | LocalDate.of(2038, 3, 21)  | LocalDate.of(2037, 5, 22)  | true     | 2701.81     | 3514.02         | 1841.44               | 2          | "MAIN"     || 13935.00  || 1265.00    || 0
        LocalDate.of(2015, 6, 24)  | LocalDate.of(2016, 7, 25)  | null                       | true     | 5185.40     | 7062.12         | 860.11                | 13         | "MAIN"     || 109389.89 || 0.00       || 9
        LocalDate.of(2023, 4, 25)  | LocalDate.of(2023, 5, 1)   | LocalDate.of(2023, 4, 20)  | true     | 4535.25     | 4095.34         | 915.72                | 0          | "MAIN"     || 789.19    || 0.00       || 0
        LocalDate.of(1986, 1, 6)   | LocalDate.of(1987, 1, 30)  | LocalDate.of(1984, 12, 20) | true     | 3307.48     | 1283.33         | 1042.50               | 9          | "MAIN"     || 80811.65  || 0.00       || 9
        LocalDate.of(1998, 11, 25) | LocalDate.of(1999, 3, 31)  | null                       | false    | 5013.42     | 3390.23         | 1733.52               | 0          | "MAIN"     || 5433.19   || 1265.00    || 0
        LocalDate.of(2051, 8, 19)  | LocalDate.of(2052, 5, 24)  | LocalDate.of(2051, 4, 22)  | false    | 5978.71     | 5946.69         | 1854.43               | 6          | "MAIN"     || 44622.02  || 1265.00    || 9
        LocalDate.of(2054, 8, 14)  | LocalDate.of(2055, 1, 18)  | null                       | true     | 1657.49     | 9.51            | 601.39                | 0          | "PRE"      || 8636.59   || 0.00       || 0
        LocalDate.of(1989, 2, 20)  | LocalDate.of(1989, 11, 1)  | null                       | false    | 9368.28     | 1717.03         | 986.92                | 4          | "MAIN"     || 40279.33  || 0.00       || 0
        LocalDate.of(2007, 2, 26)  | LocalDate.of(2008, 2, 4)   | LocalDate.of(2006, 12, 18) | false    | 477.01      | 664.94          | 403.93                | 8          | "MAIN"     || 57691.07  || 0.00       || 9
    }

    def "Tier 4 Non Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())

        assert jsonContent.threshold == threshold
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
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType || threshold || feesCapped || courseCapped
        LocalDate.of(2016, 5, 23)  | LocalDate.of(2016, 7, 16)  | null                       | false    | 1202.93     | 8524.70         | 459.76                | 0          | "PRE"      || 1570.24   || 0.00       || 0
        LocalDate.of(2051, 1, 19)  | LocalDate.of(2051, 5, 17)  | null                       | true     | 9404.17     | 6202.90         | 42.50                 | 0          | "MAIN"     || 8218.77   || 0.00       || 0
        LocalDate.of(2024, 6, 15)  | LocalDate.of(2025, 3, 28)  | null                       | true     | 6625.77     | 5488.51         | 1926.50               | 12         | "MAIN"     || 102517.26 || 1265.00    || 9
        LocalDate.of(2036, 9, 21)  | LocalDate.of(2037, 1, 31)  | null                       | false    | 5857.66     | 1593.17         | 773.20                | 0          | "MAIN"     || 8566.29   || 0.00       || 0
        LocalDate.of(2052, 12, 17) | LocalDate.of(2053, 3, 23)  | LocalDate.of(2052, 9, 9)   | true     | 9289.43     | 5115.15         | 845.77                | 3          | "MAIN"     || 23598.51  || 0.00       || 0
        LocalDate.of(2022, 6, 3)   | LocalDate.of(2023, 3, 7)   | null                       | false    | 5078.11     | 7002.48         | 813.95                | 9          | "PRE"      || 63401.05  || 0.00       || 9
        LocalDate.of(1978, 10, 7)  | LocalDate.of(1979, 3, 13)  | LocalDate.of(1977, 12, 15) | true     | 2704.55     | 4944.65         | 1580.10               | 10         | "MAIN"     || 82375.00  || 1265.00    || 0
        LocalDate.of(2011, 11, 21) | LocalDate.of(2012, 11, 17) | LocalDate.of(2011, 8, 3)   | true     | 9357.66     | 5841.72         | 966.37                | 9          | "MAIN"     || 82379.57  || 0.00       || 9
        LocalDate.of(2020, 4, 22)  | LocalDate.of(2020, 7, 6)   | null                       | true     | 4203.55     | 7454.66         | 1995.55               | 0          | "PRE"      || 2530.00   || 1265.00    || 0
        LocalDate.of(2006, 1, 22)  | LocalDate.of(2006, 3, 20)  | LocalDate.of(2005, 9, 12)  | false    | 6724.65     | 7414.85         | 1694.33               | 0          | "MAIN"     || 765.00    || 1265.00    || 0
        LocalDate.of(1977, 5, 3)   | LocalDate.of(1978, 5, 29)  | LocalDate.of(1977, 1, 3)   | true     | 1041.55     | 8163.97         | 803.81                | 6          | "MAIN"     || 56211.19  || 0.00       || 9
        LocalDate.of(2049, 8, 8)   | LocalDate.of(2049, 9, 17)  | null                       | false    | 8399.40     | 2138.03         | 397.88                | 0          | "MAIN"     || 7893.49   || 0.00       || 0
        LocalDate.of(2051, 12, 23) | LocalDate.of(2052, 11, 7)  | null                       | false    | 4643.63     | 7510.33         | 425.94                | 14         | "PRE"      || 94389.06  || 0.00       || 9
        LocalDate.of(1995, 10, 12) | LocalDate.of(1996, 11, 13) | LocalDate.of(1994, 11, 8)  | false    | 6023.32     | 3160.73         | 808.28                | 11         | "MAIN"     || 78509.31  || 0.00       || 9
        LocalDate.of(1999, 4, 28)  | LocalDate.of(1999, 10, 21) | LocalDate.of(1998, 11, 16) | false    | 3884.10     | 3295.11         | 1182.13               | 4          | "MAIN"     || 27256.86  || 0.00       || 0
        LocalDate.of(2022, 10, 29) | LocalDate.of(2023, 4, 18)  | null                       | false    | 6552.48     | 6142.47         | 1228.08               | 0          | "MAIN"     || 5271.93   || 0.00       || 0
        LocalDate.of(1975, 10, 12) | LocalDate.of(1976, 4, 28)  | null                       | true     | 4185.34     | 3673.35         | 1127.70               | 13         | "PRE"      || 107104.29 || 0.00       || 0
        LocalDate.of(2050, 11, 25) | LocalDate.of(2051, 2, 21)  | LocalDate.of(2050, 10, 5)  | true     | 9618.60     | 4838.89         | 569.06                | 11         | "MAIN"     || 45185.65  || 0.00       || 0
        LocalDate.of(1973, 12, 12) | LocalDate.of(1974, 8, 6)   | null                       | true     | 2274.30     | 9034.58         | 483.78                | 11         | "PRE"      || 93291.22  || 0.00       || 0
        LocalDate.of(2040, 7, 6)   | LocalDate.of(2040, 10, 25) | LocalDate.of(2039, 10, 20) | false    | 8007.26     | 9882.86         | 808.99                | 12         | "MAIN"     || 68531.01  || 0.00       || 0
        LocalDate.of(1975, 6, 21)  | LocalDate.of(1976, 4, 6)   | null                       | false    | 5146.43     | 3295.64         | 1820.98               | 4          | "PRE"      || 34200.79  || 1265.00    || 9
        LocalDate.of(2005, 3, 1)   | LocalDate.of(2005, 6, 21)  | null                       | true     | 7826.07     | 7232.60         | 1450.09               | 0          | "PRE"      || 4388.47   || 1265.00    || 0
        LocalDate.of(2006, 11, 18) | LocalDate.of(2007, 11, 17) | null                       | true     | 1481.30     | 8921.47         | 1571.84               | 10         | "MAIN"     || 86170.00  || 1265.00    || 9
        LocalDate.of(1978, 8, 3)   | LocalDate.of(1979, 6, 24)  | null                       | false    | 7915.21     | 1655.20         | 322.88                | 7          | "PRE"      || 57912.13  || 0.00       || 9
        LocalDate.of(2013, 5, 1)   | LocalDate.of(2013, 5, 17)  | null                       | false    | 846.71      | 4200.54         | 1046.32               | 0          | "PRE"      || 0.00      || 0.00       || 0
        LocalDate.of(2005, 11, 15) | LocalDate.of(2006, 6, 5)   | null                       | false    | 6379.37     | 5625.59         | 74.54                 | 4          | "PRE"      || 32264.24  || 0.00       || 0
        LocalDate.of(2025, 10, 9)  | LocalDate.of(2026, 4, 3)   | LocalDate.of(2025, 6, 2)   | true     | 6369.19     | 3672.01         | 351.61                | 12         | "MAIN"     || 91055.57  || 0.00       || 0
        LocalDate.of(2037, 6, 28)  | LocalDate.of(2038, 6, 11)  | null                       | true     | 4256.02     | 1088.19         | 1143.76               | 14         | "PRE"      || 119879.07 || 0.00       || 9
        LocalDate.of(2047, 3, 21)  | LocalDate.of(2048, 1, 7)   | LocalDate.of(2046, 3, 10)  | false    | 6990.28     | 3083.97         | 1991.97               | 9          | "MAIN"     || 66856.31  || 1265.00    || 9
        LocalDate.of(2042, 2, 8)   | LocalDate.of(2043, 1, 19)  | null                       | true     | 2858.06     | 8068.43         | 341.54                | 2          | "PRE"      || 26253.46  || 0.00       || 9
        LocalDate.of(2022, 4, 29)  | LocalDate.of(2023, 2, 7)   | LocalDate.of(2021, 4, 16)  | false    | 8194.70     | 5203.25         | 758.43                | 6          | "MAIN"     || 48088.02  || 0.00       || 9
        LocalDate.of(2013, 6, 5)   | LocalDate.of(2013, 10, 24) | null                       | false    | 1618.41     | 5835.70         | 513.69                | 0          | "MAIN"     || 4561.31   || 0.00       || 0
        LocalDate.of(1979, 11, 27) | LocalDate.of(1980, 6, 13)  | LocalDate.of(1978, 12, 29) | false    | 9721.43     | 9552.73         | 598.27                | 6          | "MAIN"     || 43395.43  || 0.00       || 0
        LocalDate.of(1987, 6, 15)  | LocalDate.of(1987, 8, 25)  | null                       | false    | 6183.25     | 4344.86         | 1893.46               | 0          | "PRE"      || 3618.39   || 1265.00    || 0
        LocalDate.of(2035, 7, 15)  | LocalDate.of(2036, 5, 2)   | LocalDate.of(2035, 3, 15)  | false    | 9865.12     | 2806.53         | 325.59                | 5          | "MAIN"     || 46468.00  || 0.00       || 9
        LocalDate.of(2053, 6, 27)  | LocalDate.of(2054, 2, 8)   | LocalDate.of(2052, 9, 25)  | true     | 4807.17     | 5537.04         | 1648.78               | 7          | "MAIN"     || 62090.00  || 1265.00    || 0
        LocalDate.of(1997, 8, 25)  | LocalDate.of(1997, 8, 31)  | null                       | false    | 1733.82     | 5717.65         | 221.02                | 0          | "PRE"      || 793.98    || 0.00       || 0
        LocalDate.of(2038, 3, 27)  | LocalDate.of(2038, 10, 3)  | LocalDate.of(2037, 9, 13)  | true     | 7235.10     | 6325.79         | 1300.82               | 7          | "MAIN"     || 61734.31  || 1265.00    || 0
        LocalDate.of(2020, 7, 16)  | LocalDate.of(2021, 8, 16)  | LocalDate.of(2019, 12, 12) | true     | 4848.73     | 7215.97         | 604.35                | 2          | "MAIN"     || 25990.65  || 0.00       || 9
        LocalDate.of(2050, 9, 1)   | LocalDate.of(2051, 4, 10)  | LocalDate.of(2049, 9, 22)  | true     | 3814.25     | 3489.58         | 1295.44               | 10         | "MAIN"     || 85229.67  || 1265.00    || 0
        LocalDate.of(2015, 5, 3)   | LocalDate.of(2015, 12, 30) | null                       | false    | 1855.66     | 2817.56         | 895.89                | 11         | "MAIN"     || 74544.11  || 0.00       || 0
        LocalDate.of(2015, 11, 15) | LocalDate.of(2016, 5, 3)   | null                       | true     | 5447.13     | 2820.30         | 852.07                | 0          | "PRE"      || 9364.76   || 0.00       || 0
        LocalDate.of(2035, 10, 14) | LocalDate.of(2036, 8, 29)  | null                       | true     | 870.21      | 4719.52         | 860.11                | 1          | "MAIN"     || 18129.89  || 0.00       || 9
        LocalDate.of(2048, 9, 7)   | LocalDate.of(2049, 1, 26)  | LocalDate.of(2048, 5, 6)   | false    | 971.53      | 9984.12         | 611.62                | 6          | "MAIN"     || 33023.38  || 0.00       || 0
        LocalDate.of(2003, 1, 7)   | LocalDate.of(2003, 4, 11)  | LocalDate.of(2002, 12, 31) | true     | 56.77       | 1015.07         | 846.66                | 4          | "MAIN"     || 17733.34  || 0.00       || 0
        LocalDate.of(2047, 8, 10)  | LocalDate.of(2047, 12, 5)  | null                       | true     | 3573.31     | 4583.26         | 74.20                 | 0          | "PRE"      || 4985.80   || 0.00       || 0
        LocalDate.of(2044, 8, 19)  | LocalDate.of(2044, 9, 19)  | null                       | false    | 5957.10     | 5344.60         | 870.33                | 0          | "MAIN"     || 1772.17   || 0.00       || 0
        LocalDate.of(2002, 12, 24) | LocalDate.of(2003, 10, 24) | LocalDate.of(2002, 2, 2)   | false    | 5870.14     | 3950.77         | 573.24                | 7          | "MAIN"     || 53321.13  || 0.00       || 9
        LocalDate.of(1996, 6, 4)   | LocalDate.of(1996, 8, 23)  | LocalDate.of(1996, 4, 21)  | true     | 7866.04     | 2027.89         | 1062.11               | 3          | "MAIN"     || 16176.04  || 0.00       || 0
        LocalDate.of(2051, 4, 29)  | LocalDate.of(2051, 5, 17)  | null                       | true     | 2524.83     | 7617.61         | 1863.06               | 0          | "MAIN"     || 0.00      || 1265.00    || 0
        LocalDate.of(1981, 8, 11)  | LocalDate.of(1982, 8, 12)  | null                       | false    | 4546.60     | 9156.59         | 1968.64               | 8          | "MAIN"     || 56830.00  || 1265.00    || 9
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
