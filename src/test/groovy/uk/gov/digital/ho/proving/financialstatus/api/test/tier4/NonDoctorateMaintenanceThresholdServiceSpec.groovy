package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdServiceTier4
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventPublisher
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication

import java.time.LocalDate

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

    @Unroll
    def "Tier 4 Non Doctorate - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2053, 7, 26)  | LocalDate.of(2054, 2, 13)  | LocalDate.of(2052, 7, 18)  | false    | 4636.96     | 4019.96         | 0.00                  | 1          | "main"          || 13842.00  || 0.00       || 0            || LocalDate.of(2054, 6, 13)
        LocalDate.of(2019, 5, 16)  | LocalDate.of(2020, 2, 22)  | null                       | false    | 2482.89     | 0.00            | 1912.79               | 10         | "main"          || 71552.89  || 1265.00    || 9            || LocalDate.of(2020, 4, 22)
        LocalDate.of(1973, 2, 8)   | LocalDate.of(1973, 9, 28)  | null                       | false    | 9658.01     | 0.00            | 1008.34               | 5          | "pre-sessional" || 47369.67  || 0.00       || 0            || LocalDate.of(1973, 11, 28)
        LocalDate.of(2042, 2, 24)  | LocalDate.of(2042, 4, 18)  | LocalDate.of(2041, 4, 22)  | false    | 8605.96     | 0.00            | 971.19                | 6          | "main"          || 25984.77  || 0.00       || 0            || LocalDate.of(2042, 6, 18)
        LocalDate.of(2048, 11, 17) | LocalDate.of(2049, 7, 20)  | null                       | false    | 8634.03     | 8619.85         | 0.00                  | 11         | "pre-sessional" || 76469.18  || 0.00       || 0            || LocalDate.of(2049, 9, 20)
        LocalDate.of(2039, 8, 10)  | LocalDate.of(2040, 5, 6)   | null                       | false    | 4128.11     | 3441.81         | 0.00                  | 9          | "pre-sessional" || 64901.30  || 0.00       || 0            || LocalDate.of(2040, 7, 6)
        LocalDate.of(2041, 3, 7)   | LocalDate.of(2042, 4, 7)   | LocalDate.of(2040, 10, 9)  | false    | 8350.30     | 0.00            | 1131.31               | 14         | "main"          || 102033.99 || 0.00       || 9            || LocalDate.of(2042, 8, 7)
        LocalDate.of(2031, 1, 15)  | LocalDate.of(2031, 11, 10) | null                       | false    | 1169.98     | 5026.17         | 1965.30               | 5          | "pre-sessional" || 38470.00  || 1265.00    || 9            || LocalDate.of(2032, 1, 10)
        LocalDate.of(2032, 8, 20)  | LocalDate.of(2033, 2, 5)   | null                       | false    | 2592.96     | 0.00            | 450.37                | 0          | "pre-sessional" || 8232.59   || 0.00       || 0            || LocalDate.of(2033, 3, 5)
        LocalDate.of(1977, 12, 5)  | LocalDate.of(1978, 7, 22)  | null                       | false    | 587.53      | 3844.44         | 0.00                  | 8          | "main"          || 57080.00  || 0.00       || 0            || LocalDate.of(1978, 9, 22)
        LocalDate.of(2043, 3, 11)  | LocalDate.of(2043, 5, 26)  | null                       | false    | 8212.08     | 7093.03         | 1094.33               | 0          | "pre-sessional" || 3069.72   || 0.00       || 0            || LocalDate.of(2043, 6, 26)
        LocalDate.of(2015, 2, 4)   | LocalDate.of(2015, 8, 3)   | LocalDate.of(2014, 6, 15)  | false    | 6817.74     | 767.60          | 0.00                  | 1          | "main"          || 18260.14  || 0.00       || 0            || LocalDate.of(2015, 12, 3)
        LocalDate.of(2001, 7, 16)  | LocalDate.of(2001, 7, 25)  | null                       | false    | 6995.38     | 0.00            | 0.00                  | 0          | "pre-sessional" || 8010.38   || 0.00       || 0            || LocalDate.of(2001, 8, 25)
        LocalDate.of(1974, 5, 27)  | LocalDate.of(1974, 8, 24)  | null                       | false    | 1096.34     | 0.00            | 0.00                  | 0          | "pre-sessional" || 4141.34   || 0.00       || 0            || LocalDate.of(1974, 9, 24)
        LocalDate.of(1998, 9, 27)  | LocalDate.of(1999, 4, 4)   | LocalDate.of(1997, 10, 27) | false    | 8600.07     | 2749.75         | 879.95                | 3          | "main"          || 30435.37  || 0.00       || 0            || LocalDate.of(1999, 8, 4)
        LocalDate.of(1984, 6, 9)   | LocalDate.of(1984, 7, 25)  | null                       | false    | 4097.97     | 0.00            | 0.00                  | 0          | "main"          || 6127.97   || 0.00       || 0            || LocalDate.of(1984, 8, 1)
        LocalDate.of(2008, 11, 17) | LocalDate.of(2009, 4, 5)   | null                       | false    | 6352.10     | 8727.02         | 0.00                  | 0          | "main"          || 5075.00   || 0.00       || 0            || LocalDate.of(2009, 4, 12)
        LocalDate.of(1987, 6, 5)   | LocalDate.of(1987, 9, 30)  | LocalDate.of(1986, 7, 8)   | false    | 7627.96     | 0.00            | 1549.42               | 14         | "main"          || 86582.96  || 1265.00    || 0            || LocalDate.of(1988, 1, 30)
        LocalDate.of(1982, 11, 21) | LocalDate.of(1983, 3, 27)  | null                       | false    | 6660.12     | 0.00            | 1824.31               | 0          | "pre-sessional" || 10470.12  || 1265.00    || 0            || LocalDate.of(1983, 4, 27)
        LocalDate.of(1979, 5, 31)  | LocalDate.of(1979, 7, 3)   | null                       | false    | 8289.14     | 0.00            | 1559.83               | 0          | "pre-sessional" || 9054.14   || 1265.00    || 0            || LocalDate.of(1979, 8, 3)
        LocalDate.of(1990, 8, 3)   | LocalDate.of(1990, 9, 1)   | null                       | false    | 5660.73     | 0.00            | 0.00                  | 0          | "main"          || 6675.73   || 0.00       || 0            || LocalDate.of(1990, 9, 8)
        LocalDate.of(2017, 5, 2)   | LocalDate.of(2018, 5, 26)  | LocalDate.of(2016, 9, 3)   | false    | 6226.75     | 1159.80         | 1573.49               | 5          | "main"          || 43536.95  || 1265.00    || 9            || LocalDate.of(2018, 9, 26)
        LocalDate.of(2001, 7, 27)  | LocalDate.of(2001, 11, 19) | null                       | false    | 9744.63     | 0.00            | 0.00                  | 0          | "pre-sessional" || 13804.63  || 0.00       || 0            || LocalDate.of(2001, 12, 19)
        LocalDate.of(1975, 7, 8)   | LocalDate.of(1975, 9, 14)  | LocalDate.of(1975, 6, 21)  | false    | 3258.68     | 0.00            | 1853.82               | 4          | "main"          || 13198.68  || 1265.00    || 0            || LocalDate.of(1975, 9, 21)
        LocalDate.of(1977, 6, 4)   | LocalDate.of(1977, 10, 10) | null                       | false    | 6326.34     | 0.00            | 1497.56               | 0          | "main"          || 10136.34  || 1265.00    || 0            || LocalDate.of(1977, 10, 17)
    }

    @Unroll
    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2006, 1, 13)  | LocalDate.of(2006, 11, 18) | null                       | true     | 1295.12     | 0.00            | 1770.87               | 0          | "pre-sessional" || 11415.12  || 1265.00    || 9            || LocalDate.of(2007, 1, 18)
        LocalDate.of(2001, 12, 4)  | LocalDate.of(2002, 7, 19)  | LocalDate.of(2001, 10, 6)  | true     | 9102.55     | 442.78          | 1179.99               | 4          | "main"          || 48019.78  || 0.00       || 0            || LocalDate.of(2002, 9, 19)
        LocalDate.of(1977, 7, 10)  | LocalDate.of(1977, 9, 28)  | LocalDate.of(1977, 1, 20)  | true     | 7748.02     | 7324.26         | 0.00                  | 12         | "main"          || 54918.76  || 0.00       || 0            || LocalDate.of(1977, 11, 28)
        LocalDate.of(2045, 2, 16)  | LocalDate.of(2045, 7, 4)   | LocalDate.of(2044, 8, 12)  | true     | 6951.08     | 7259.58         | 0.00                  | 10         | "main"          || 65475.00  || 0.00       || 0            || LocalDate.of(2045, 9, 4)
        LocalDate.of(2020, 6, 7)   | LocalDate.of(2021, 6, 7)   | LocalDate.of(2019, 9, 10)  | true     | 9888.26     | 0.00            | 1915.90               | 3          | "main"          || 42823.26  || 1265.00    || 9            || LocalDate.of(2021, 10, 7)
        LocalDate.of(1989, 5, 18)  | LocalDate.of(1990, 1, 19)  | LocalDate.of(1989, 2, 7)   | true     | 917.80      | 0.00            | 676.74                | 14         | "main"          || 118096.06 || 0.00       || 0            || LocalDate.of(1990, 3, 19)
        LocalDate.of(1981, 12, 4)  | LocalDate.of(1983, 1, 3)   | null                       | true     | 3019.02     | 0.00            | 0.00                  | 6          | "main"          || 60034.02  || 0.00       || 9            || LocalDate.of(1983, 5, 3)
        LocalDate.of(2035, 3, 31)  | LocalDate.of(2036, 3, 12)  | LocalDate.of(2035, 3, 7)   | true     | 8725.26     | 0.00            | 598.45                | 8          | "main"          || 80351.81  || 0.00       || 9            || LocalDate.of(2036, 7, 12)
        LocalDate.of(2028, 12, 21) | LocalDate.of(2029, 7, 29)  | null                       | true     | 8700.57     | 0.00            | 816.40                | 3          | "pre-sessional" || 40819.17  || 0.00       || 0            || LocalDate.of(2029, 9, 29)
        LocalDate.of(2052, 9, 24)  | LocalDate.of(2053, 2, 24)  | LocalDate.of(2052, 6, 7)   | true     | 1639.72     | 8198.80         | 0.00                  | 7          | "main"          || 54910.00  || 0.00       || 0            || LocalDate.of(2053, 4, 24)
        LocalDate.of(2034, 5, 1)   | LocalDate.of(2034, 10, 19) | LocalDate.of(2033, 10, 11) | true     | 2547.92     | 0.00            | 1002.79               | 11         | "main"          || 92790.13  || 0.00       || 0            || LocalDate.of(2035, 2, 19)
        LocalDate.of(2045, 1, 10)  | LocalDate.of(2045, 12, 15) | null                       | true     | 727.54      | 7802.05         | 0.00                  | 13         | "pre-sessional" || 110250.00 || 0.00       || 9            || LocalDate.of(2046, 2, 15)
        LocalDate.of(2007, 2, 16)  | LocalDate.of(2007, 5, 29)  | null                       | true     | 8674.88     | 451.99          | 0.00                  | 0          | "pre-sessional" || 13282.89  || 0.00       || 0            || LocalDate.of(2007, 6, 29)
        LocalDate.of(1984, 9, 17)  | LocalDate.of(1985, 1, 11)  | LocalDate.of(1984, 4, 1)   | true     | 6670.72     | 0.00            | 1730.95               | 9          | "main"          || 56095.72  || 1265.00    || 0            || LocalDate.of(1985, 3, 11)
        LocalDate.of(2043, 11, 1)  | LocalDate.of(2044, 4, 10)  | LocalDate.of(2042, 10, 16) | true     | 958.61      | 5158.50         | 832.13                | 9          | "main"          || 75202.87  || 0.00       || 0            || LocalDate.of(2044, 8, 10)
        LocalDate.of(2054, 10, 1)  | LocalDate.of(2055, 4, 27)  | null                       | true     | 7303.91     | 0.00            | 0.00                  | 9          | "pre-sessional" || 84603.91  || 0.00       || 0            || LocalDate.of(2055, 6, 27)
        LocalDate.of(2016, 10, 22) | LocalDate.of(2016, 11, 16) | LocalDate.of(2016, 2, 1)   | true     | 5663.43     | 8052.37         | 1566.40               | 3          | "main"          || 7605.00   || 1265.00    || 0            || LocalDate.of(2017, 1, 16)
        LocalDate.of(1988, 6, 9)   | LocalDate.of(1989, 4, 22)  | LocalDate.of(1987, 9, 6)   | true     | 9467.45     | 1229.21         | 0.00                  | 13         | "main"          || 118488.24 || 0.00       || 9            || LocalDate.of(1989, 8, 22)
        LocalDate.of(2004, 9, 9)   | LocalDate.of(2005, 8, 11)  | null                       | true     | 4524.03     | 3900.04         | 0.00                  | 7          | "main"          || 65243.99  || 0.00       || 9            || LocalDate.of(2005, 10, 11)
        LocalDate.of(1998, 6, 19)  | LocalDate.of(1999, 3, 13)  | LocalDate.of(1997, 6, 28)  | true     | 4303.42     | 0.00            | 0.00                  | 10         | "main"          || 91738.42  || 0.00       || 0            || LocalDate.of(1999, 7, 13)
        LocalDate.of(2015, 5, 30)  | LocalDate.of(2016, 5, 19)  | null                       | true     | 4905.62     | 4342.51         | 557.30                | 14         | "pre-sessional" || 117860.81 || 0.00       || 9            || LocalDate.of(2016, 7, 19)
        LocalDate.of(2042, 1, 15)  | LocalDate.of(2042, 9, 6)   | null                       | true     | 5559.11     | 1251.64         | 344.57                | 11         | "pre-sessional" || 97737.90  || 0.00       || 0            || LocalDate.of(2042, 11, 6)
        LocalDate.of(2023, 11, 23) | LocalDate.of(2024, 8, 28)  | LocalDate.of(2023, 1, 22)  | true     | 3414.07     | 3786.14         | 0.00                  | 4          | "main"          || 41805.00  || 0.00       || 9            || LocalDate.of(2024, 12, 28)
        LocalDate.of(1985, 3, 18)  | LocalDate.of(1985, 10, 10) | LocalDate.of(1985, 2, 16)  | true     | 3809.47     | 6569.13         | 1506.22               | 11         | "main"          || 91245.00  || 1265.00    || 0            || LocalDate.of(1985, 12, 10)
        LocalDate.of(2011, 6, 20)  | LocalDate.of(2011, 11, 5)  | null                       | true     | 3811.15     | 0.00            | 0.00                  | 0          | "main"          || 10136.15  || 0.00       || 0            || LocalDate.of(2011, 11, 12)
    }

    @Unroll
    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2037, 2, 13)  | LocalDate.of(2037, 3, 26)  | LocalDate.of(2036, 10, 10) | true     | 3057.93     | 4717.03         | 0.00                  | 0          | "main"          || 2530.00   || 0.00       || 0            || LocalDate.of(2037, 4, 2)
        LocalDate.of(1986, 9, 1)   | LocalDate.of(1987, 8, 5)   | null                       | true     | 8900.70     | 6168.19         | 0.00                  | 4          | "pre-sessional" || 44537.51  || 0.00       || 9            || LocalDate.of(1987, 10, 5)
        LocalDate.of(2048, 5, 1)   | LocalDate.of(2048, 12, 6)  | null                       | true     | 6752.54     | 2173.29         | 1120.75               | 4          | "main"          || 43998.50  || 0.00       || 0            || LocalDate.of(2049, 2, 6)
        LocalDate.of(2024, 4, 7)   | LocalDate.of(2024, 10, 21) | LocalDate.of(2023, 8, 19)  | false    | 7885.48     | 5230.09         | 104.06                | 3          | "main"          || 28016.33  || 0.00       || 0            || LocalDate.of(2025, 2, 21)
        LocalDate.of(2024, 10, 13) | LocalDate.of(2025, 6, 16)  | null                       | false    | 6414.89     | 1439.68         | 891.79                | 0          | "pre-sessional" || 13218.42  || 0.00       || 0            || LocalDate.of(2025, 8, 16)
        LocalDate.of(2010, 4, 22)  | LocalDate.of(2010, 7, 8)   | LocalDate.of(2009, 6, 6)   | false    | 5261.31     | 5158.96         | 0.00                  | 5          | "main"          || 26947.35  || 0.00       || 0            || LocalDate.of(2010, 11, 8)
        LocalDate.of(2018, 2, 28)  | LocalDate.of(2019, 1, 7)   | LocalDate.of(2017, 6, 4)   | false    | 8175.94     | 3260.00         | 1320.10               | 8          | "main"          || 61745.94  || 1265.00    || 9            || LocalDate.of(2019, 5, 7)
        LocalDate.of(2022, 3, 14)  | LocalDate.of(2022, 8, 26)  | null                       | true     | 444.27      | 7443.13         | 314.67                | 0          | "main"          || 7275.33   || 0.00       || 0            || LocalDate.of(2022, 9, 2)
        LocalDate.of(2015, 9, 6)   | LocalDate.of(2015, 11, 18) | LocalDate.of(2014, 12, 11) | true     | 6618.97     | 8518.92         | 0.00                  | 6          | "main"          || 29145.00  || 0.00       || 0            || LocalDate.of(2016, 1, 18)
        LocalDate.of(1998, 10, 16) | LocalDate.of(1999, 2, 22)  | null                       | false    | 1983.45     | 3299.93         | 0.00                  | 0          | "pre-sessional" || 5075.00   || 0.00       || 0            || LocalDate.of(1999, 3, 22)
        LocalDate.of(1973, 12, 9)  | LocalDate.of(1973, 12, 10) | null                       | true     | 2001.43     | 6557.91         | 1789.89               | 0          | "pre-sessional" || 0.00      || 1265.00    || 0            || LocalDate.of(1974, 1, 10)
        LocalDate.of(2054, 8, 25)  | LocalDate.of(2055, 5, 15)  | LocalDate.of(2054, 6, 29)  | false    | 4656.91     | 8186.96         | 0.00                  | 0          | "main"          || 9135.00   || 0.00       || 0            || LocalDate.of(2055, 7, 15)
        LocalDate.of(2007, 11, 16) | LocalDate.of(2008, 12, 18) | null                       | false    | 1133.84     | 6624.24         | 0.00                  | 3          | "main"          || 27495.00  || 0.00       || 9            || LocalDate.of(2009, 4, 18)
        LocalDate.of(1990, 11, 29) | LocalDate.of(1991, 2, 6)   | null                       | false    | 7667.48     | 2703.22         | 0.00                  | 0          | "main"          || 8009.26   || 0.00       || 0            || LocalDate.of(1991, 2, 13)
        LocalDate.of(2031, 12, 14) | LocalDate.of(2032, 7, 20)  | null                       | false    | 7952.89     | 9814.98         | 0.00                  | 0          | "pre-sessional" || 8120.00   || 0.00       || 0            || LocalDate.of(2032, 9, 20)
        LocalDate.of(2050, 2, 2)   | LocalDate.of(2051, 2, 10)  | LocalDate.of(2049, 4, 8)   | false    | 831.25      | 1893.39         | 0.00                  | 0          | "main"          || 9135.00   || 0.00       || 9            || LocalDate.of(2051, 6, 10)
        LocalDate.of(2024, 12, 19) | LocalDate.of(2025, 7, 6)   | null                       | true     | 9472.82     | 9068.10         | 0.00                  | 6          | "pre-sessional" || 54889.72  || 0.00       || 0            || LocalDate.of(2025, 9, 6)
        LocalDate.of(2025, 5, 27)  | LocalDate.of(2025, 9, 14)  | null                       | true     | 5795.83     | 7810.93         | 1693.29               | 0          | "pre-sessional" || 3795.00   || 1265.00    || 0            || LocalDate.of(2025, 10, 14)
        LocalDate.of(1995, 6, 3)   | LocalDate.of(1996, 4, 18)  | null                       | true     | 4828.78     | 9370.70         | 0.00                  | 14         | "main"          || 117855.00 || 0.00       || 9            || LocalDate.of(1996, 6, 18)
        LocalDate.of(2029, 2, 27)  | LocalDate.of(2030, 1, 22)  | LocalDate.of(2028, 2, 18)  | true     | 2343.91     | 5065.62         | 1334.58               | 4          | "main"          || 40540.00  || 1265.00    || 9            || LocalDate.of(2030, 5, 22)
        LocalDate.of(2016, 12, 4)  | LocalDate.of(2017, 10, 27) | LocalDate.of(2016, 4, 27)  | false    | 2347.42     | 9799.63         | 0.00                  | 8          | "main"          || 58095.00  || 0.00       || 9            || LocalDate.of(2018, 2, 27)
        LocalDate.of(2018, 1, 20)  | LocalDate.of(2018, 6, 2)   | LocalDate.of(2017, 6, 2)   | true     | 8088.92     | 1412.15         | 1337.49               | 2          | "main"          || 26946.77  || 1265.00    || 0            || LocalDate.of(2018, 10, 2)
        LocalDate.of(2040, 5, 30)  | LocalDate.of(2040, 6, 27)  | LocalDate.of(2039, 8, 19)  | false    | 9741.02     | 6064.99         | 1014.90               | 8          | "main"          || 19996.13  || 0.00       || 0            || LocalDate.of(2040, 8, 27)
        LocalDate.of(1994, 5, 27)  | LocalDate.of(1994, 6, 17)  | null                       | false    | 8482.01     | 623.45          | 131.27                | 0          | "main"          || 8742.29   || 0.00       || 0            || LocalDate.of(1994, 6, 24)
        LocalDate.of(2040, 12, 24) | LocalDate.of(2041, 1, 25)  | null                       | false    | 5906.66     | 7912.87         | 0.00                  | 0          | "main"          || 2030.00   || 0.00       || 0            || LocalDate.of(2041, 2, 1)
    }

    @Unroll
    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2032, 12, 9)  | LocalDate.of(2032, 12, 27) | null                       | true     | 6270.78     | 0.00            | 1850.41               | 0          | "pre-sessional" || 6270.78   || 1265.00    || 0            || LocalDate.of(2033, 1, 27)
        LocalDate.of(2024, 8, 30)  | LocalDate.of(2025, 6, 15)  | LocalDate.of(2024, 1, 15)  | false    | 5144.34     | 4888.30         | 325.14                | 9          | "main"          || 64145.90  || 0.00       || 9            || LocalDate.of(2025, 10, 15)
        LocalDate.of(1987, 8, 31)  | LocalDate.of(1988, 9, 9)   | null                       | false    | 8674.39     | 790.37          | 1249.81               | 13         | "pre-sessional" || 95329.21  || 0.00       || 9            || LocalDate.of(1989, 1, 9)
        LocalDate.of(2043, 11, 11) | LocalDate.of(2044, 10, 7)  | LocalDate.of(2043, 6, 15)  | true     | 8016.16     | 6437.42         | 762.08                | 4          | "main"          || 42621.66  || 0.00       || 9            || LocalDate.of(2045, 2, 7)
        LocalDate.of(1985, 4, 27)  | LocalDate.of(1986, 1, 8)   | LocalDate.of(1985, 3, 13)  | false    | 1540.51     | 0.00            | 1864.60               | 4          | "main"          || 33890.51  || 1265.00    || 0            || LocalDate.of(1986, 3, 8)
        LocalDate.of(1990, 5, 26)  | LocalDate.of(1991, 1, 19)  | LocalDate.of(1990, 4, 27)  | true     | 9176.65     | 0.00            | 801.96                | 13         | "main"          || 117359.69 || 0.00       || 0            || LocalDate.of(1991, 3, 19)
        LocalDate.of(2005, 3, 11)  | LocalDate.of(2006, 3, 20)  | null                       | false    | 5022.08     | 6325.12         | 1243.78               | 9          | "pre-sessional" || 62971.22  || 0.00       || 9            || LocalDate.of(2006, 7, 20)
        LocalDate.of(2011, 8, 12)  | LocalDate.of(2012, 8, 9)   | LocalDate.of(2011, 3, 2)   | false    | 3180.10     | 0.00            | 611.11                | 0          | "main"          || 11703.99  || 0.00       || 9            || LocalDate.of(2012, 12, 9)
        LocalDate.of(1990, 6, 5)   | LocalDate.of(1990, 8, 29)  | null                       | false    | 6333.96     | 0.00            | 519.34                | 0          | "pre-sessional" || 8859.62   || 0.00       || 0            || LocalDate.of(1990, 9, 29)
        LocalDate.of(1998, 8, 5)   | LocalDate.of(1999, 1, 24)  | LocalDate.of(1998, 1, 19)  | true     | 4210.81     | 1575.28         | 1122.89               | 1          | "main"          || 16707.64  || 0.00       || 0            || LocalDate.of(1999, 5, 24)
        LocalDate.of(1993, 12, 1)  | LocalDate.of(1994, 10, 16) | LocalDate.of(1993, 9, 12)  | false    | 9598.13     | 3073.13         | 1438.00               | 10         | "main"          || 75595.00  || 1265.00    || 9            || LocalDate.of(1995, 2, 16)
        LocalDate.of(2047, 9, 4)   | LocalDate.of(2048, 8, 4)   | null                       | true     | 5864.67     | 2350.78         | 1032.03               | 1          | "pre-sessional" || 21471.86  || 0.00       || 9            || LocalDate.of(2048, 10, 4)
        LocalDate.of(2049, 10, 3)  | LocalDate.of(2050, 10, 22) | null                       | true     | 4645.09     | 0.00            | 1537.15               | 4          | "pre-sessional" || 45185.09  || 1265.00    || 9            || LocalDate.of(2051, 2, 22)
        LocalDate.of(2037, 1, 20)  | LocalDate.of(2037, 3, 21)  | null                       | true     | 2440.13     | 7015.79         | 1528.12               | 0          | "main"          || 2530.00   || 1265.00    || 0            || LocalDate.of(2037, 3, 28)
        LocalDate.of(1996, 12, 19) | LocalDate.of(1997, 12, 31) | LocalDate.of(1996, 6, 5)   | false    | 3096.30     | 8726.91         | 470.16                | 10         | "main"          || 69864.84  || 0.00       || 9            || LocalDate.of(1998, 4, 30)
        LocalDate.of(1978, 7, 6)   | LocalDate.of(1978, 8, 26)  | LocalDate.of(1978, 6, 2)   | true     | 1581.94     | 0.00            | 974.52                | 6          | "main"          || 13277.42  || 0.00       || 0            || LocalDate.of(1978, 9, 2)
        LocalDate.of(2006, 8, 28)  | LocalDate.of(2007, 5, 30)  | LocalDate.of(2006, 8, 5)   | true     | 8638.29     | 5826.77         | 1017.72               | 8          | "main"          || 74018.80  || 0.00       || 9            || LocalDate.of(2007, 7, 30)
        LocalDate.of(2017, 4, 16)  | LocalDate.of(2018, 4, 23)  | null                       | true     | 5064.31     | 8067.03         | 944.87                | 13         | "pre-sessional" || 109305.13 || 0.00       || 9            || LocalDate.of(2018, 8, 23)
        LocalDate.of(2054, 2, 24)  | LocalDate.of(2054, 11, 17) | LocalDate.of(2053, 8, 1)   | true     | 2485.11     | 2559.67         | 1116.73               | 3          | "main"          || 33083.27  || 0.00       || 0            || LocalDate.of(2055, 3, 17)
        LocalDate.of(1976, 10, 13) | LocalDate.of(1977, 4, 24)  | LocalDate.of(1975, 12, 18) | false    | 5638.02     | 4680.47         | 1997.47               | 7          | "main"          || 49637.55  || 1265.00    || 0            || LocalDate.of(1977, 8, 24)
        LocalDate.of(1996, 2, 22)  | LocalDate.of(1996, 7, 5)   | LocalDate.of(1995, 6, 4)   | false    | 2012.90     | 0.00            | 294.17                | 12         | "main"          || 80233.73  || 0.00       || 0            || LocalDate.of(1996, 11, 5)
        LocalDate.of(2041, 11, 15) | LocalDate.of(2042, 4, 5)   | null                       | false    | 3957.06     | 2825.96         | 1411.58               | 0          | "main"          || 4941.10   || 1265.00    || 0            || LocalDate.of(2042, 4, 12)
        LocalDate.of(2025, 6, 22)  | LocalDate.of(2026, 4, 7)   | null                       | true     | 1972.43     | 7529.27         | 1965.78               | 8          | "main"          || 70960.00  || 1265.00    || 9            || LocalDate.of(2026, 6, 7)
        LocalDate.of(2039, 3, 30)  | LocalDate.of(2039, 7, 15)  | LocalDate.of(2038, 8, 21)  | false    | 2536.17     | 0.00            | 1865.00               | 10         | "main"          || 46131.17  || 1265.00    || 0            || LocalDate.of(2039, 9, 15)
        LocalDate.of(2042, 11, 3)  | LocalDate.of(2043, 6, 5)   | LocalDate.of(2042, 1, 14)  | true     | 4725.26     | 2682.28         | 1374.45               | 4          | "main"          || 41317.98  || 1265.00    || 0            || LocalDate.of(2043, 10, 5)
    }

    @Unroll
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
        LocalDate.of(1973, 9, 5)   | LocalDate.of(1973, 11, 22) | null                       | false    | 6205.42     | 0.00            | 1451.35               | 0          | "pre-sessional" || 7985.42   || 1265.00    || 0            || LocalDate.of(1973, 12, 22)
        LocalDate.of(2025, 3, 28)  | LocalDate.of(2025, 4, 14)  | null                       | false    | 9845.71     | 0.00            | 106.62                | 0          | "main"          || 10754.09  || 0.00       || 0            || LocalDate.of(2025, 4, 21)
        LocalDate.of(2049, 1, 11)  | LocalDate.of(2049, 12, 26) | LocalDate.of(2048, 1, 14)  | false    | 3113.95     | 2584.29         | 1737.54               | 1          | "main"          || 14519.66  || 1265.00    || 9            || LocalDate.of(2050, 4, 26)
        LocalDate.of(2042, 3, 25)  | LocalDate.of(2042, 4, 28)  | LocalDate.of(2041, 8, 6)   | true     | 6551.88     | 0.00            | 943.14                | 2          | "main"          || 14898.74  || 0.00       || 0            || LocalDate.of(2042, 6, 28)
        LocalDate.of(2002, 4, 23)  | LocalDate.of(2003, 5, 3)   | LocalDate.of(2001, 5, 21)  | true     | 778.90      | 0.00            | 1950.45               | 9          | "main"          || 79343.90  || 1265.00    || 9            || LocalDate.of(2003, 9, 3)
        LocalDate.of(2008, 5, 18)  | LocalDate.of(2009, 3, 16)  | null                       | true     | 1200.90     | 0.00            | 1593.13               | 8          | "pre-sessional" || 72160.90  || 1265.00    || 9            || LocalDate.of(2009, 5, 16)
        LocalDate.of(1985, 9, 29)  | LocalDate.of(1986, 6, 28)  | LocalDate.of(1985, 5, 23)  | false    | 5892.96     | 807.80          | 0.00                  | 3          | "main"          || 32580.16  || 0.00       || 0            || LocalDate.of(1986, 10, 28)
        LocalDate.of(2019, 1, 26)  | LocalDate.of(2019, 6, 14)  | LocalDate.of(2018, 10, 26) | false    | 6964.34     | 6797.84         | 0.00                  | 13         | "main"          || 67121.50  || 0.00       || 0            || LocalDate.of(2019, 8, 14)
        LocalDate.of(1999, 5, 12)  | LocalDate.of(1999, 10, 2)  | null                       | true     | 2302.03     | 0.00            | 0.00                  | 0          | "main"          || 8627.03   || 0.00       || 0            || LocalDate.of(1999, 10, 9)
        LocalDate.of(2036, 12, 15) | LocalDate.of(2037, 8, 21)  | null                       | false    | 2346.20     | 0.00            | 0.00                  | 9          | "pre-sessional" || 66561.20  || 0.00       || 0            || LocalDate.of(2037, 10, 21)
        LocalDate.of(1991, 5, 14)  | LocalDate.of(1991, 6, 5)   | null                       | false    | 3357.57     | 5708.47         | 1265.62               | 0          | "main"          || 0.00      || 1265.00    || 0            || LocalDate.of(1991, 6, 12)
        LocalDate.of(1974, 5, 21)  | LocalDate.of(1974, 8, 22)  | null                       | true     | 1467.45     | 0.00            | 0.00                  | 0          | "main"          || 6527.45   || 0.00       || 0            || LocalDate.of(1974, 8, 29)
        LocalDate.of(1998, 4, 19)  | LocalDate.of(1999, 2, 18)  | null                       | false    | 8229.20     | 0.00            | 0.00                  | 3          | "pre-sessional" || 35724.20  || 0.00       || 9            || LocalDate.of(1999, 4, 18)
        LocalDate.of(2043, 6, 5)   | LocalDate.of(2044, 1, 7)   | LocalDate.of(2042, 7, 13)  | false    | 5293.58     | 3563.74         | 956.25                | 10         | "main"          || 70093.59  || 0.00       || 0            || LocalDate.of(2044, 5, 7)
        LocalDate.of(2052, 3, 4)   | LocalDate.of(2052, 11, 15) | LocalDate.of(2051, 7, 19)  | true     | 5391.95     | 248.74          | 1607.65               | 11         | "main"          || 98918.21  || 1265.00    || 0            || LocalDate.of(2053, 3, 15)
        LocalDate.of(2024, 1, 14)  | LocalDate.of(2025, 1, 12)  | null                       | true     | 1109.28     | 0.00            | 0.00                  | 5          | "main"          || 50519.28  || 0.00       || 9            || LocalDate.of(2025, 3, 12)
        LocalDate.of(1975, 7, 20)  | LocalDate.of(1975, 11, 23) | LocalDate.of(1975, 7, 4)   | true     | 1086.70     | 0.00            | 0.00                  | 10         | "main"          || 49661.70  || 0.00       || 0            || LocalDate.of(1975, 11, 30)
        LocalDate.of(2038, 7, 28)  | LocalDate.of(2039, 5, 25)  | LocalDate.of(2038, 2, 24)  | true     | 5322.19     | 4977.75         | 0.00                  | 9          | "main"          || 80174.44  || 0.00       || 9            || LocalDate.of(2039, 9, 25)
        LocalDate.of(2051, 1, 4)   | LocalDate.of(2052, 1, 21)  | LocalDate.of(2050, 8, 17)  | false    | 9596.74     | 6257.43         | 0.00                  | 9          | "main"          || 67554.31  || 0.00       || 9            || LocalDate.of(2052, 5, 21)
        LocalDate.of(2025, 4, 30)  | LocalDate.of(2026, 2, 4)   | LocalDate.of(2024, 4, 14)  | false    | 3699.10     | 1853.76         | 267.41                | 9          | "main"          || 65792.93  || 0.00       || 9            || LocalDate.of(2026, 6, 4)
        LocalDate.of(1986, 2, 13)  | LocalDate.of(1986, 7, 11)  | LocalDate.of(1985, 12, 9)  | false    | 974.53      | 8330.34         | 0.00                  | 1          | "main"          || 9835.00   || 0.00       || 0            || LocalDate.of(1986, 9, 11)
        LocalDate.of(2014, 12, 7)  | LocalDate.of(2015, 6, 29)  | LocalDate.of(2014, 11, 10) | true     | 7892.28     | 5939.02         | 0.00                  | 0          | "main"          || 10808.26  || 0.00       || 0            || LocalDate.of(2015, 8, 29)
        LocalDate.of(1998, 9, 6)   | LocalDate.of(1999, 2, 9)   | LocalDate.of(1998, 3, 19)  | true     | 7515.87     | 1649.06         | 0.00                  | 5          | "main"          || 47256.81  || 0.00       || 0            || LocalDate.of(1999, 4, 9)
        LocalDate.of(2005, 6, 28)  | LocalDate.of(2005, 10, 19) | LocalDate.of(2005, 2, 2)   | false    | 6934.75     | 3487.95         | 0.00                  | 8          | "main"          || 40146.80  || 0.00       || 0            || LocalDate.of(2005, 12, 19)
        LocalDate.of(2003, 5, 1)   | LocalDate.of(2003, 12, 3)  | null                       | true     | 5174.76     | 0.00            | 0.00                  | 11         | "pre-sessional" || 98949.76  || 0.00       || 0            || LocalDate.of(2004, 2, 3)
    }

    @Unroll
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

    @Unroll
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

    @Unroll
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

    @Unroll
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

    @Unroll
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

    @Unroll
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

    @Unroll
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

    @Unroll
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
