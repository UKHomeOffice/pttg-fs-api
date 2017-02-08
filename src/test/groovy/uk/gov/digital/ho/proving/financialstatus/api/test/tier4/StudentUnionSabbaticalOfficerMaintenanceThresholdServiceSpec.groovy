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
class StudentUnionSabbaticalOfficerMaintenanceThresholdServiceSpec extends Specification {

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

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("originalCourseStartDate", (originalCourseStartDate == null) ? "" : originalCourseStartDate.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
                .param("dependantsOnly", dependantsOnly.toString())
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Tier 4 Student Sabbatical Office - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2025, 4, 27)  | LocalDate.of(2026, 3, 18)  | LocalDate.of(2025, 2, 22) | false    | 0.00                  | 9          | false          || 14270.00  || 0.00       || 2            || LocalDate.of(2026, 7, 18)
        LocalDate.of(1990, 6, 28)  | LocalDate.of(1990, 10, 22) | LocalDate.of(1989, 8, 23) | false    | 1317.00               | 5          | false          || 7565.00   || 1265.00    || 2            || LocalDate.of(1991, 2, 22)
        LocalDate.of(2027, 8, 31)  | LocalDate.of(2028, 2, 10)  | null                      | false    | 1108.24               | 0          | false          || 921.76    || 0.00       || 2            || LocalDate.of(2028, 2, 17)
        LocalDate.of(1995, 7, 4)   | LocalDate.of(1995, 12, 4)  | LocalDate.of(1994, 11, 3) | false    | 1627.84               | 6          | false          || 8925.00   || 1265.00    || 2            || LocalDate.of(1996, 4, 4)
        LocalDate.of(2037, 3, 15)  | LocalDate.of(2037, 4, 21)  | null                      | false    | 63.41                 | 0          | false          || 1966.59   || 0.00       || 0            || LocalDate.of(2037, 4, 28)
        LocalDate.of(1980, 10, 31) | LocalDate.of(1981, 8, 18)  | LocalDate.of(1979, 9, 29) | false    | 0.00                  | 6          | false          || 10190.00  || 0.00       || 2            || LocalDate.of(1981, 12, 18)
        LocalDate.of(2010, 2, 21)  | LocalDate.of(2010, 12, 29) | null                      | false    | 0.00                  | 6          | false          || 10190.00  || 0.00       || 2            || LocalDate.of(2011, 2, 28)
        LocalDate.of(2012, 2, 28)  | LocalDate.of(2013, 2, 17)  | LocalDate.of(2011, 5, 5)  | false    | 0.00                  | 12         | false          || 18350.00  || 0.00       || 2            || LocalDate.of(2013, 6, 17)
        LocalDate.of(1993, 10, 11) | LocalDate.of(1994, 3, 15)  | LocalDate.of(1993, 7, 6)  | false    | 979.82                | 11         | false          || 16010.18  || 0.00       || 2            || LocalDate.of(1994, 5, 15)
        LocalDate.of(2034, 9, 28)  | LocalDate.of(2035, 4, 18)  | LocalDate.of(2034, 6, 24) | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2035, 6, 18)
        LocalDate.of(2019, 7, 23)  | LocalDate.of(2020, 5, 31)  | null                      | false    | 0.00                  | 5          | false          || 8830.00   || 0.00       || 2            || LocalDate.of(2020, 7, 31)
        LocalDate.of(1981, 6, 21)  | LocalDate.of(1981, 12, 11) | null                      | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1981, 12, 18)
        LocalDate.of(2004, 11, 9)  | LocalDate.of(2005, 10, 10) | LocalDate.of(2004, 5, 28) | false    | 0.00                  | 9          | false          || 14270.00  || 0.00       || 2            || LocalDate.of(2006, 2, 10)
        LocalDate.of(1972, 10, 21) | LocalDate.of(1972, 12, 9)  | null                      | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 0            || LocalDate.of(1972, 12, 16)
        LocalDate.of(2019, 4, 19)  | LocalDate.of(2019, 6, 16)  | LocalDate.of(2018, 4, 20) | false    | 0.00                  | 11         | false          || 16990.00  || 0.00       || 0            || LocalDate.of(2019, 10, 16)
        LocalDate.of(2036, 7, 23)  | LocalDate.of(2037, 5, 1)   | LocalDate.of(2036, 3, 7)  | false    | 1234.14               | 4          | false          || 6235.86   || 0.00       || 2            || LocalDate.of(2037, 9, 1)
        LocalDate.of(1992, 4, 16)  | LocalDate.of(1992, 9, 25)  | null                      | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1992, 10, 2)
        LocalDate.of(1986, 12, 21) | LocalDate.of(1987, 8, 5)   | LocalDate.of(1986, 3, 14) | false    | 0.00                  | 13         | false          || 19710.00  || 0.00       || 2            || LocalDate.of(1987, 12, 5)
        LocalDate.of(2017, 9, 17)  | LocalDate.of(2018, 5, 17)  | null                      | false    | 0.00                  | 1          | false          || 3390.00   || 0.00       || 2            || LocalDate.of(2018, 7, 17)
        LocalDate.of(2014, 5, 9)   | LocalDate.of(2015, 4, 26)  | null                      | false    | 549.95                | 3          | false          || 5560.05   || 0.00       || 2            || LocalDate.of(2015, 6, 26)
    }

    def "Tier 4 Student Sabbatical Office - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2034, 10, 23) | LocalDate.of(2034, 11, 20) | LocalDate.of(2033, 12, 31) | true     | 428.13                | 0          | false          || 836.87    || 0.00       || 0            || LocalDate.of(2035, 1, 20)
        LocalDate.of(1985, 11, 30) | LocalDate.of(1986, 3, 9)   | LocalDate.of(1985, 10, 28) | true     | 0.00                  | 14         | false          || 26190.00  || 0.00       || 2            || LocalDate.of(1986, 3, 16)
        LocalDate.of(2004, 11, 23) | LocalDate.of(2005, 3, 31)  | LocalDate.of(2004, 9, 15)  | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 2            || LocalDate.of(2005, 5, 31)
        LocalDate.of(2001, 8, 14)  | LocalDate.of(2001, 10, 29) | LocalDate.of(2001, 3, 14)  | true     | 0.00                  | 6          | false          || 12670.00  || 0.00       || 2            || LocalDate.of(2001, 12, 29)
        LocalDate.of(2003, 10, 8)  | LocalDate.of(2003, 12, 12) | null                       | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 2            || LocalDate.of(2003, 12, 19)
        LocalDate.of(2035, 1, 24)  | LocalDate.of(2035, 10, 20) | LocalDate.of(2034, 6, 23)  | true     | 0.00                  | 5          | false          || 10980.00  || 0.00       || 2            || LocalDate.of(2036, 2, 20)
        LocalDate.of(2006, 8, 18)  | LocalDate.of(2007, 8, 16)  | LocalDate.of(2005, 9, 13)  | true     | 837.59                | 7          | false          || 13522.41  || 0.00       || 2            || LocalDate.of(2007, 12, 16)
        LocalDate.of(2045, 4, 18)  | LocalDate.of(2045, 10, 31) | LocalDate.of(2044, 4, 2)   | true     | 0.00                  | 14         | false          || 26190.00  || 0.00       || 2            || LocalDate.of(2046, 2, 28)
        LocalDate.of(1992, 9, 6)   | LocalDate.of(1992, 11, 11) | LocalDate.of(1992, 7, 14)  | true     | 960.97                | 8          | false          || 15089.03  || 0.00       || 2            || LocalDate.of(1992, 11, 18)
        LocalDate.of(2050, 5, 30)  | LocalDate.of(2050, 8, 21)  | null                       | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 2            || LocalDate.of(2050, 8, 28)
        LocalDate.of(2013, 11, 20) | LocalDate.of(2014, 9, 15)  | null                       | true     | 601.76                | 3          | false          || 6998.24   || 0.00       || 2            || LocalDate.of(2014, 11, 15)
        LocalDate.of(1975, 8, 17)  | LocalDate.of(1976, 6, 1)   | LocalDate.of(1974, 10, 25) | true     | 0.00                  | 8          | false          || 16050.00  || 0.00       || 2            || LocalDate.of(1976, 10, 1)
        LocalDate.of(2033, 3, 30)  | LocalDate.of(2033, 11, 14) | LocalDate.of(2032, 4, 24)  | true     | 178.53                | 3          | false          || 7421.47   || 0.00       || 2            || LocalDate.of(2034, 3, 14)
        LocalDate.of(2038, 10, 20) | LocalDate.of(2038, 12, 28) | null                       | true     | 874.25                | 0          | false          || 1655.75   || 0.00       || 2            || LocalDate.of(2039, 1, 4)
        LocalDate.of(1980, 7, 4)   | LocalDate.of(1980, 10, 23) | LocalDate.of(1979, 5, 31)  | true     | 283.34                | 6          | false          || 12386.66  || 0.00       || 2            || LocalDate.of(1981, 2, 23)
        LocalDate.of(1995, 1, 11)  | LocalDate.of(1995, 9, 21)  | LocalDate.of(1994, 8, 25)  | true     | 0.00                  | 11         | false          || 21120.00  || 0.00       || 2            || LocalDate.of(1996, 1, 21)
        LocalDate.of(2028, 3, 27)  | LocalDate.of(2029, 1, 7)   | LocalDate.of(2027, 8, 9)   | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 2            || LocalDate.of(2029, 5, 7)
        LocalDate.of(1998, 9, 8)   | LocalDate.of(1998, 11, 22) | LocalDate.of(1997, 10, 10) | true     | 1152.46               | 14         | false          || 25037.54  || 0.00       || 2            || LocalDate.of(1999, 3, 22)
//        LocalDate.of(1992, 10, 9)  | LocalDate.of(1992, 11, 2)  | LocalDate.of(1992, 10, 6)  | true     | 549.89                | 7          | false          || 6630.11   || 0.00       || 0            || LocalDate.of(1992, 11, 9)
        LocalDate.of(2033, 5, 25)  | LocalDate.of(2034, 6, 8)   | LocalDate.of(2032, 11, 3)  | true     | 0.00                  | 5          | false          || 10980.00  || 0.00       || 2            || LocalDate.of(2034, 10, 8)
    }

    def "Tier 4 Student Sabbatical Office - Check 'Continuation courses'"() {

        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1984, 7, 29)  | LocalDate.of(1985, 1, 5)   | LocalDate.of(1984, 1, 6)   | false    | 1112.66               | 12         | false          || 17237.34  || 0.00       || 2            || LocalDate.of(1985, 5, 5)
        LocalDate.of(1989, 3, 28)  | LocalDate.of(1989, 11, 26) | LocalDate.of(1988, 7, 28)  | true     | 0.00                  | 11         | false          || 21120.00  || 0.00       || 2            || LocalDate.of(1990, 3, 26)
        LocalDate.of(1973, 5, 3)   | LocalDate.of(1973, 6, 28)  | LocalDate.of(1973, 3, 8)   | true     | 1112.23               | 14         | false          || 25077.77  || 0.00       || 0            || LocalDate.of(1973, 7, 5)
        LocalDate.of(2024, 5, 1)   | LocalDate.of(2024, 7, 4)   | LocalDate.of(2023, 11, 24) | true     | 107.96                | 13         | false          || 24392.04  || 0.00       || 2            || LocalDate.of(2024, 9, 4)
        LocalDate.of(2015, 9, 13)  | LocalDate.of(2016, 2, 13)  | LocalDate.of(2015, 4, 10)  | false    | 1115.96               | 9          | false          || 13154.04  || 0.00       || 2            || LocalDate.of(2016, 4, 13)
        LocalDate.of(2007, 9, 19)  | LocalDate.of(2008, 1, 10)  | LocalDate.of(2007, 5, 27)  | false    | 4.73                  | 0          | false          || 2025.27   || 0.00       || 2            || LocalDate.of(2008, 3, 10)
        LocalDate.of(2025, 11, 18) | LocalDate.of(2026, 2, 18)  | LocalDate.of(2025, 4, 27)  | false    | 1974.04               | 1          | false          || 2125.00   || 1265.00    || 2            || LocalDate.of(2026, 4, 18)
        LocalDate.of(1991, 2, 1)   | LocalDate.of(1991, 3, 3)   | LocalDate.of(1990, 3, 8)   | true     | 802.36                | 13         | false          || 23697.64  || 0.00       || 0            || LocalDate.of(1991, 5, 3)
        LocalDate.of(2022, 3, 26)  | LocalDate.of(2022, 6, 15)  | LocalDate.of(2022, 2, 5)   | true     | 0.00                  | 4          | false          || 9290.00   || 0.00       || 2            || LocalDate.of(2022, 6, 22)
        LocalDate.of(2026, 10, 16) | LocalDate.of(2027, 4, 22)  | LocalDate.of(2026, 2, 7)   | true     | 963.87                | 13         | false          || 23536.13  || 0.00       || 2            || LocalDate.of(2027, 8, 22)
        LocalDate.of(2020, 11, 30) | LocalDate.of(2021, 1, 27)  | LocalDate.of(2020, 8, 22)  | true     | 583.67                | 14         | false          || 25606.33  || 0.00       || 0            || LocalDate.of(2021, 2, 3)
        LocalDate.of(1991, 3, 22)  | LocalDate.of(1991, 5, 7)   | LocalDate.of(1991, 1, 25)  | true     | 1482.75               | 1          | false          || 2955.00   || 1265.00    || 0            || LocalDate.of(1991, 5, 14)
        LocalDate.of(2052, 4, 26)  | LocalDate.of(2052, 8, 10)  | LocalDate.of(2051, 6, 12)  | false    | 0.00                  | 6          | false          || 10190.00  || 0.00       || 2            || LocalDate.of(2052, 12, 10)
        LocalDate.of(1974, 9, 6)   | LocalDate.of(1975, 7, 18)  | LocalDate.of(1973, 12, 15) | true     | 1298.83               | 11         | false          || 19855.00  || 1265.00    || 2            || LocalDate.of(1975, 11, 18)
        LocalDate.of(2003, 6, 13)  | LocalDate.of(2004, 4, 9)   | LocalDate.of(2003, 3, 30)  | true     | 486.54                | 10         | false          || 18943.46  || 0.00       || 2            || LocalDate.of(2004, 8, 9)
        LocalDate.of(2048, 12, 28) | LocalDate.of(2049, 7, 10)  | LocalDate.of(2047, 12, 27) | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 2            || LocalDate.of(2049, 11, 10)
        LocalDate.of(2021, 12, 1)  | LocalDate.of(2022, 9, 11)  | LocalDate.of(2020, 11, 29) | false    | 1088.90               | 0          | false          || 941.10    || 0.00       || 2            || LocalDate.of(2023, 1, 11)
        LocalDate.of(2038, 1, 26)  | LocalDate.of(2038, 6, 16)  | LocalDate.of(2037, 10, 15) | true     | 0.00                  | 7          | false          || 14360.00  || 0.00       || 2            || LocalDate.of(2038, 8, 16)
        LocalDate.of(2031, 2, 15)  | LocalDate.of(2031, 8, 13)  | LocalDate.of(2030, 3, 5)   | true     | 1340.53               | 12         | false          || 21545.00  || 1265.00    || 2            || LocalDate.of(2031, 12, 13)
        LocalDate.of(2001, 8, 16)  | LocalDate.of(2002, 4, 10)  | LocalDate.of(2001, 2, 11)  | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 2            || LocalDate.of(2002, 8, 10)
    }

    def "Tier 4 Student Sabbatical Office - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1991, 3, 2)   | LocalDate.of(1992, 2, 25)  | LocalDate.of(1990, 12, 2)  | false    | 1475.58               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(1992, 6, 25)
        LocalDate.of(2038, 10, 28) | LocalDate.of(2039, 10, 19) | LocalDate.of(2038, 2, 4)   | false    | 1004.46               | 2          | false          || 3745.54   || 0.00       || 2            || LocalDate.of(2040, 2, 19)
        LocalDate.of(2009, 6, 28)  | LocalDate.of(2010, 7, 2)   | LocalDate.of(2008, 12, 14) | true     | 69.70                 | 7          | false          || 14290.30  || 0.00       || 2            || LocalDate.of(2010, 11, 2)
        LocalDate.of(2027, 12, 6)  | LocalDate.of(2028, 5, 3)   | LocalDate.of(2027, 7, 14)  | true     | 1744.83               | 4          | false          || 8025.00   || 1265.00    || 2            || LocalDate.of(2028, 7, 3)
        LocalDate.of(1992, 7, 5)   | LocalDate.of(1993, 3, 18)  | LocalDate.of(1991, 9, 16)  | true     | 1770.71               | 4          | false          || 8025.00   || 1265.00    || 2            || LocalDate.of(1993, 7, 18)
        LocalDate.of(2031, 8, 24)  | LocalDate.of(2032, 2, 12)  | null                       | false    | 628.41                | 0          | false          || 1401.59   || 0.00       || 2            || LocalDate.of(2032, 2, 19)
        LocalDate.of(1975, 7, 1)   | LocalDate.of(1975, 10, 31) | null                       | false    | 215.08                | 0          | false          || 1814.92   || 0.00       || 2            || LocalDate.of(1975, 11, 7)
        LocalDate.of(1990, 9, 20)  | LocalDate.of(1991, 1, 30)  | LocalDate.of(1989, 9, 23)  | false    | 291.79                | 7          | false          || 11258.21  || 0.00       || 2            || LocalDate.of(1991, 5, 30)
        LocalDate.of(2021, 5, 14)  | LocalDate.of(2022, 6, 3)   | LocalDate.of(2021, 3, 19)  | false    | 1196.43               | 11         | false          || 15793.57  || 0.00       || 2            || LocalDate.of(2022, 10, 3)
        LocalDate.of(1976, 2, 2)   | LocalDate.of(1976, 7, 7)   | LocalDate.of(1975, 9, 4)   | true     | 1391.57               | 5          | false          || 9715.00   || 1265.00    || 2            || LocalDate.of(1976, 9, 7)
        LocalDate.of(1985, 4, 18)  | LocalDate.of(1985, 8, 26)  | LocalDate.of(1984, 7, 27)  | true     | 1061.72               | 7          | false          || 13298.28  || 0.00       || 2            || LocalDate.of(1985, 12, 26)
        LocalDate.of(2054, 1, 19)  | LocalDate.of(2054, 3, 16)  | null                       | true     | 203.00                | 0          | false          || 2327.00   || 0.00       || 0            || LocalDate.of(2054, 3, 23)
        LocalDate.of(2047, 2, 11)  | LocalDate.of(2047, 12, 14) | null                       | true     | 1004.18               | 8          | false          || 15045.82  || 0.00       || 2            || LocalDate.of(2048, 2, 14)
        LocalDate.of(1976, 11, 25) | LocalDate.of(1977, 12, 21) | LocalDate.of(1976, 3, 23)  | true     | 529.51                | 14         | false          || 25660.49  || 0.00       || 2            || LocalDate.of(1978, 4, 21)
        LocalDate.of(2013, 12, 1)  | LocalDate.of(2014, 8, 4)   | LocalDate.of(2013, 11, 22) | false    | 1373.01               | 7          | false          || 10285.00  || 1265.00    || 2            || LocalDate.of(2014, 10, 4)
        LocalDate.of(1977, 5, 2)   | LocalDate.of(1978, 4, 11)  | LocalDate.of(1976, 8, 24)  | true     | 805.10                | 12         | false          || 22004.90  || 0.00       || 2            || LocalDate.of(1978, 8, 11)
        LocalDate.of(2029, 10, 31) | LocalDate.of(2030, 7, 30)  | LocalDate.of(2028, 12, 31) | false    | 613.64                | 12         | false          || 17736.36  || 0.00       || 2            || LocalDate.of(2030, 11, 30)
        LocalDate.of(1995, 10, 10) | LocalDate.of(1996, 7, 21)  | null                       | false    | 491.83                | 0          | false          || 1538.17   || 0.00       || 2            || LocalDate.of(1996, 9, 21)
        LocalDate.of(2054, 4, 20)  | LocalDate.of(2054, 11, 26) | LocalDate.of(2053, 4, 28)  | false    | 143.05                | 4          | false          || 7326.95   || 0.00       || 2            || LocalDate.of(2055, 3, 26)
        LocalDate.of(2016, 9, 28)  | LocalDate.of(2017, 9, 10)  | LocalDate.of(2016, 4, 9)   | false    | 1543.70               | 12         | false          || 17085.00  || 1265.00    || 2            || LocalDate.of(2018, 1, 10)
    }

    // Dependants only

    def "Tier 4 Student Sabbatical Office - Check 'Non Inner London Borough'  dependants only"() {

        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1995, 7, 2)   | LocalDate.of(1996, 5, 6)   | null                       | false    | 0.00                  | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(1996, 7, 6)
        LocalDate.of(2010, 10, 30) | LocalDate.of(2011, 5, 24)  | LocalDate.of(2010, 2, 1)   | false    | 1855.58               | 12         | true           || 16320.00  || 1265.00    || 2            || LocalDate.of(2011, 9, 24)
        LocalDate.of(2020, 11, 1)  | LocalDate.of(2021, 2, 20)  | null                       | false    | 1407.61               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(2021, 2, 27)
        LocalDate.of(2041, 3, 23)  | LocalDate.of(2041, 11, 4)  | LocalDate.of(2040, 10, 21) | false    | 0.00                  | 4          | true           || 5440.00   || 0.00       || 2            || LocalDate.of(2042, 3, 4)
        LocalDate.of(2015, 5, 2)   | LocalDate.of(2015, 6, 7)   | LocalDate.of(2015, 4, 23)  | false    | 643.45                | 11         | true           || 14960.00  || 0.00       || 0            || LocalDate.of(2015, 6, 14)
        LocalDate.of(2020, 1, 8)   | LocalDate.of(2020, 3, 28)  | LocalDate.of(2019, 9, 11)  | false    | 0.00                  | 3          | true           || 4080.00   || 0.00       || 2            || LocalDate.of(2020, 5, 28)
        LocalDate.of(2046, 2, 12)  | LocalDate.of(2047, 3, 16)  | LocalDate.of(2046, 1, 10)  | false    | 0.00                  | 9          | true           || 12240.00  || 0.00       || 2            || LocalDate.of(2047, 7, 16)
        LocalDate.of(2006, 8, 6)   | LocalDate.of(2007, 4, 12)  | LocalDate.of(2005, 11, 26) | false    | 601.38                | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(2007, 8, 12)
        LocalDate.of(1997, 5, 15)  | LocalDate.of(1997, 10, 16) | LocalDate.of(1997, 4, 22)  | false    | 0.00                  | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(1997, 10, 23)
        LocalDate.of(2000, 10, 30) | LocalDate.of(2001, 4, 14)  | LocalDate.of(2000, 4, 27)  | false    | 892.86                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2001, 6, 14)
        LocalDate.of(1989, 7, 21)  | LocalDate.of(1990, 7, 2)   | null                       | false    | 103.23                | 5          | true           || 6800.00   || 0.00       || 2            || LocalDate.of(1990, 9, 2)
        LocalDate.of(2009, 12, 23) | LocalDate.of(2010, 9, 9)   | null                       | false    | 0.00                  | 5          | true           || 6800.00   || 0.00       || 2            || LocalDate.of(2010, 11, 9)
        LocalDate.of(1992, 2, 26)  | LocalDate.of(1992, 4, 22)  | LocalDate.of(1992, 1, 3)   | false    | 980.59                | 11         | true           || 14960.00  || 0.00       || 0            || LocalDate.of(1992, 4, 29)
        LocalDate.of(1979, 6, 4)   | LocalDate.of(1980, 1, 19)  | LocalDate.of(1979, 1, 20)  | false    | 713.93                | 3          | true           || 4080.00   || 0.00       || 2            || LocalDate.of(1980, 5, 19)
        LocalDate.of(2031, 11, 25) | LocalDate.of(2032, 1, 8)   | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2032, 1, 15)
        LocalDate.of(2044, 3, 17)  | LocalDate.of(2045, 2, 10)  | LocalDate.of(2043, 11, 7)  | false    | 1880.50               | 1          | true           || 1360.00   || 1265.00    || 2            || LocalDate.of(2045, 6, 10)
        LocalDate.of(1983, 1, 20)  | LocalDate.of(1983, 6, 5)   | LocalDate.of(1982, 2, 4)   | false    | 0.00                  | 13         | true           || 17680.00  || 0.00       || 2            || LocalDate.of(1983, 10, 5)
        LocalDate.of(1999, 4, 21)  | LocalDate.of(1999, 12, 30) | LocalDate.of(1998, 12, 24) | false    | 0.00                  | 14         | true           || 19040.00  || 0.00       || 2            || LocalDate.of(2000, 4, 30)
        LocalDate.of(1981, 4, 17)  | LocalDate.of(1981, 10, 6)  | LocalDate.of(1980, 12, 18) | false    | 690.62                | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(1981, 12, 6)
        LocalDate.of(1976, 1, 5)   | LocalDate.of(1976, 6, 29)  | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1976, 7, 6)
    }

    def "Tier 4 Student Sabbatical Office - Check 'Inner London Borough' dependants only"() {

        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2047, 1, 17)  | LocalDate.of(2047, 7, 13) | LocalDate.of(2046, 6, 10)  | true     | 0.00                  | 10         | true           || 16900.00  || 0.00       || 2            || LocalDate.of(2047, 11, 13)
        LocalDate.of(1984, 9, 24)  | LocalDate.of(1984, 12, 9) | LocalDate.of(1983, 12, 10) | true     | 137.88                | 10         | true           || 16900.00  || 0.00       || 2            || LocalDate.of(1985, 4, 9)
        LocalDate.of(2046, 12, 2)  | LocalDate.of(2047, 8, 4)  | LocalDate.of(2046, 6, 9)   | true     | 248.80                | 1          | true           || 1690.00   || 0.00       || 2            || LocalDate.of(2047, 12, 4)
        LocalDate.of(1976, 12, 24) | LocalDate.of(1977, 7, 23) | LocalDate.of(1976, 4, 1)   | true     | 385.82                | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(1977, 11, 23)
        LocalDate.of(1986, 7, 15)  | LocalDate.of(1987, 1, 9)  | LocalDate.of(1985, 11, 14) | true     | 0.00                  | 13         | true           || 21970.00  || 0.00       || 2            || LocalDate.of(1987, 5, 9)
        LocalDate.of(2026, 4, 30)  | LocalDate.of(2026, 12, 4) | LocalDate.of(2025, 6, 17)  | true     | 0.00                  | 3          | true           || 5070.00   || 0.00       || 2            || LocalDate.of(2027, 4, 4)
        LocalDate.of(1986, 8, 23)  | LocalDate.of(1987, 7, 26) | null                       | true     | 0.00                  | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(1987, 9, 26)
        LocalDate.of(1980, 5, 23)  | LocalDate.of(1981, 2, 26) | null                       | true     | 1089.06               | 1          | true           || 1690.00   || 0.00       || 2            || LocalDate.of(1981, 4, 26)
        LocalDate.of(2032, 7, 21)  | LocalDate.of(2032, 8, 2)  | LocalDate.of(2032, 4, 27)  | true     | 0.00                  | 10         | true           || 8450.00   || 0.00       || 0            || LocalDate.of(2032, 8, 9)
        LocalDate.of(1998, 7, 21)  | LocalDate.of(1999, 1, 14) | LocalDate.of(1998, 5, 20)  | true     | 0.00                  | 6          | true           || 10140.00  || 0.00       || 2            || LocalDate.of(1999, 3, 14)
        LocalDate.of(2045, 5, 25)  | LocalDate.of(2046, 3, 15) | null                       | true     | 0.00                  | 10         | true           || 16900.00  || 0.00       || 2            || LocalDate.of(2046, 5, 15)
        LocalDate.of(2045, 8, 19)  | LocalDate.of(2045, 11, 6) | LocalDate.of(2045, 4, 2)   | true     | 128.90                | 3          | true           || 5070.00   || 0.00       || 2            || LocalDate.of(2046, 1, 6)
        LocalDate.of(2045, 8, 8)   | LocalDate.of(2045, 9, 8)  | LocalDate.of(2045, 5, 12)  | true     | 0.00                  | 9          | true           || 15210.00  || 0.00       || 0            || LocalDate.of(2045, 9, 15)
        LocalDate.of(1984, 5, 4)   | LocalDate.of(1984, 5, 8)  | null                       | true     | 1769.08               | 0          | true           || 0.00      || 1265.00    || 0            || LocalDate.of(1984, 5, 15)
        LocalDate.of(1996, 11, 11) | LocalDate.of(1997, 2, 25) | LocalDate.of(1995, 12, 27) | true     | 0.00                  | 6          | true           || 10140.00  || 0.00       || 2            || LocalDate.of(1997, 6, 25)
        LocalDate.of(1975, 1, 12)  | LocalDate.of(1975, 8, 10) | null                       | true     | 1931.00               | 3          | true           || 5070.00   || 1265.00    || 2            || LocalDate.of(1975, 10, 10)
        LocalDate.of(2037, 11, 1)  | LocalDate.of(2038, 2, 18) | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2038, 2, 25)
        LocalDate.of(1999, 4, 19)  | LocalDate.of(2000, 3, 24) | LocalDate.of(1998, 8, 2)   | true     | 0.00                  | 3          | true           || 5070.00   || 0.00       || 2            || LocalDate.of(2000, 7, 24)
        LocalDate.of(2022, 5, 16)  | LocalDate.of(2022, 5, 25) | LocalDate.of(2022, 2, 10)  | true     | 0.00                  | 8          | true           || 6760.00   || 0.00       || 0            || LocalDate.of(2022, 6, 1)
        LocalDate.of(2015, 1, 5)   | LocalDate.of(2015, 3, 10) | LocalDate.of(2014, 6, 24)  | true     | 1154.47               | 2          | true           || 3380.00   || 0.00       || 2            || LocalDate.of(2015, 5, 10)
    }

    def "Tier 4 Student Sabbatical Office - Check 'Continuation courses' dependants only"() {

        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1996, 3, 24)  | LocalDate.of(1996, 12, 14) | LocalDate.of(1996, 3, 19) | false    | 0.00                  | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(1997, 2, 14)
        LocalDate.of(2021, 5, 21)  | LocalDate.of(2022, 4, 4)   | LocalDate.of(2021, 1, 24) | true     | 617.36                | 7          | true           || 11830.00  || 0.00       || 2            || LocalDate.of(2022, 8, 4)
        LocalDate.of(1978, 9, 9)   | LocalDate.of(1979, 1, 30)  | LocalDate.of(1978, 2, 7)  | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1979, 3, 30)
        LocalDate.of(2023, 9, 4)   | LocalDate.of(2024, 7, 26)  | LocalDate.of(2023, 7, 18) | false    | 0.00                  | 2          | true           || 2720.00   || 0.00       || 2            || LocalDate.of(2024, 11, 26)
        LocalDate.of(2047, 12, 7)  | LocalDate.of(2048, 12, 11) | LocalDate.of(2047, 3, 1)  | false    | 591.60                | 14         | true           || 19040.00  || 0.00       || 2            || LocalDate.of(2049, 4, 11)
        LocalDate.of(2020, 11, 4)  | LocalDate.of(2021, 11, 22) | LocalDate.of(2020, 6, 19) | true     | 0.00                  | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(2022, 3, 22)
        LocalDate.of(1973, 8, 16)  | LocalDate.of(1974, 6, 11)  | LocalDate.of(1973, 5, 24) | false    | 0.00                  | 3          | true           || 4080.00   || 0.00       || 2            || LocalDate.of(1974, 10, 11)
        LocalDate.of(2043, 2, 19)  | LocalDate.of(2043, 6, 12)  | LocalDate.of(2042, 9, 4)  | false    | 0.00                  | 11         | true           || 14960.00  || 0.00       || 2            || LocalDate.of(2043, 8, 12)
        LocalDate.of(1982, 12, 9)  | LocalDate.of(1983, 6, 18)  | LocalDate.of(1982, 6, 6)  | true     | 0.00                  | 4          | true           || 6760.00   || 0.00       || 2            || LocalDate.of(1983, 10, 18)
        LocalDate.of(1975, 4, 9)   | LocalDate.of(1976, 5, 13)  | LocalDate.of(1974, 9, 4)  | false    | 707.35                | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(1976, 9, 13)
        LocalDate.of(1973, 10, 10) | LocalDate.of(1973, 12, 26) | LocalDate.of(1973, 6, 27) | true     | 193.74                | 14         | true           || 23660.00  || 0.00       || 2            || LocalDate.of(1974, 2, 26)
        LocalDate.of(2050, 6, 11)  | LocalDate.of(2051, 6, 7)   | LocalDate.of(2049, 6, 25) | false    | 246.65                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2051, 10, 7)
        LocalDate.of(2003, 1, 14)  | LocalDate.of(2003, 8, 12)  | LocalDate.of(2002, 4, 19) | true     | 1062.07               | 14         | true           || 23660.00  || 0.00       || 2            || LocalDate.of(2003, 12, 12)
        LocalDate.of(1978, 4, 7)   | LocalDate.of(1979, 3, 18)  | LocalDate.of(1977, 9, 18) | false    | 0.00                  | 6          | true           || 8160.00   || 0.00       || 2            || LocalDate.of(1979, 7, 18)
        LocalDate.of(2043, 6, 1)   | LocalDate.of(2044, 2, 20)  | LocalDate.of(2042, 7, 28) | true     | 0.00                  | 5          | true           || 8450.00   || 0.00       || 2            || LocalDate.of(2044, 6, 20)
        LocalDate.of(2012, 2, 18)  | LocalDate.of(2012, 12, 14) | LocalDate.of(2011, 9, 28) | true     | 0.00                  | 2          | true           || 3380.00   || 0.00       || 2            || LocalDate.of(2013, 4, 14)
        LocalDate.of(2014, 8, 1)   | LocalDate.of(2015, 5, 12)  | LocalDate.of(2014, 5, 2)  | false    | 0.00                  | 8          | true           || 10880.00  || 0.00       || 2            || LocalDate.of(2015, 9, 12)
        LocalDate.of(2049, 6, 1)   | LocalDate.of(2050, 2, 26)  | LocalDate.of(2049, 3, 3)  | true     | 1066.74               | 8          | true           || 13520.00  || 0.00       || 2            || LocalDate.of(2050, 4, 26)
        LocalDate.of(2053, 3, 10)  | LocalDate.of(2053, 9, 13)  | LocalDate.of(2052, 3, 18) | true     | 0.00                  | 14         | true           || 23660.00  || 0.00       || 2            || LocalDate.of(2054, 1, 13)
        LocalDate.of(2006, 9, 30)  | LocalDate.of(2006, 11, 25) | LocalDate.of(2006, 4, 12) | true     | 966.90                | 4          | true           || 6760.00   || 0.00       || 0            || LocalDate.of(2007, 1, 25)
    }

    // All variants


    def "Tier 4 Student Sabbatical Office - Check 'All variants'"() {
        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
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
            assert jsonContent.cappedvalues == null
        }

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1995, 7, 10)  | LocalDate.of(1995, 10, 25) | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1995, 11, 1)
        LocalDate.of(2020, 2, 4)   | LocalDate.of(2020, 12, 2)  | LocalDate.of(2019, 11, 19) | false    | 0.00                  | 14         | false          || 21070.00  || 0.00       || 2            || LocalDate.of(2021, 4, 2)
        LocalDate.of(1973, 1, 16)  | LocalDate.of(1973, 7, 18)  | LocalDate.of(1972, 1, 9)   | true     | 517.59                | 7          | false          || 13842.41  || 0.00       || 2            || LocalDate.of(1973, 11, 18)
        LocalDate.of(2004, 3, 13)  | LocalDate.of(2004, 4, 18)  | LocalDate.of(2003, 8, 1)   | false    | 0.00                  | 5          | true           || 6800.00   || 0.00       || 0            || LocalDate.of(2004, 6, 18)
        LocalDate.of(2014, 10, 24) | LocalDate.of(2015, 8, 23)  | null                       | true     | 0.00                  | 13         | false          || 24500.00  || 0.00       || 2            || LocalDate.of(2015, 10, 23)
        LocalDate.of(1983, 11, 30) | LocalDate.of(1984, 1, 13)  | LocalDate.of(1982, 11, 8)  | true     | 1477.15               | 8          | true           || 13520.00  || 1265.00    || 0            || LocalDate.of(1984, 5, 13)
        LocalDate.of(1978, 7, 30)  | LocalDate.of(1978, 12, 11) | LocalDate.of(1978, 5, 2)   | false    | 0.00                  | 10         | false          || 15630.00  || 0.00       || 2            || LocalDate.of(1979, 2, 11)
        LocalDate.of(2033, 10, 2)  | LocalDate.of(2034, 4, 24)  | LocalDate.of(2033, 5, 29)  | false    | 485.46                | 13         | true           || 17680.00  || 0.00       || 2            || LocalDate.of(2034, 6, 24)
        LocalDate.of(2013, 7, 11)  | LocalDate.of(2014, 7, 24)  | LocalDate.of(2012, 12, 15) | true     | 1097.65               | 0          | false          || 1432.35   || 0.00       || 2            || LocalDate.of(2014, 11, 24)
        LocalDate.of(1985, 8, 25)  | LocalDate.of(1986, 4, 16)  | LocalDate.of(1984, 10, 13) | false    | 0.00                  | 5          | false          || 8830.00   || 0.00       || 2            || LocalDate.of(1986, 8, 16)
        LocalDate.of(1981, 12, 9)  | LocalDate.of(1982, 7, 17)  | null                       | false    | 401.83                | 9          | true           || 12240.00  || 0.00       || 2            || LocalDate.of(1982, 9, 17)
        LocalDate.of(2054, 8, 25)  | LocalDate.of(2055, 9, 17)  | null                       | false    | 1176.74               | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(2056, 1, 17)
        LocalDate.of(1997, 7, 16)  | LocalDate.of(1998, 4, 21)  | null                       | true     | 0.00                  | 2          | false          || 5910.00   || 0.00       || 2            || LocalDate.of(1998, 6, 21)
        LocalDate.of(2015, 6, 14)  | LocalDate.of(2016, 4, 17)  | LocalDate.of(2015, 4, 28)  | false    | 0.00                  | 8          | true           || 10880.00  || 0.00       || 2            || LocalDate.of(2016, 6, 17)
        LocalDate.of(2009, 10, 15) | LocalDate.of(2010, 5, 5)   | null                       | true     | 191.95                | 4          | false          || 9098.05   || 0.00       || 2            || LocalDate.of(2010, 7, 5)
        LocalDate.of(2033, 3, 5)   | LocalDate.of(2034, 4, 6)   | LocalDate.of(2032, 8, 3)   | true     | 0.00                  | 7          | false          || 14360.00  || 0.00       || 2            || LocalDate.of(2034, 8, 6)
        LocalDate.of(2041, 3, 26)  | LocalDate.of(2042, 3, 8)   | null                       | true     | 1753.72               | 14         | true           || 23660.00  || 1265.00    || 2            || LocalDate.of(2042, 5, 8)
        LocalDate.of(2008, 9, 6)   | LocalDate.of(2009, 9, 2)   | LocalDate.of(2008, 2, 7)   | true     | 520.71                | 8          | false          || 15529.29  || 0.00       || 2            || LocalDate.of(2010, 1, 2)
        LocalDate.of(2025, 7, 17)  | LocalDate.of(2025, 12, 12) | null                       | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 2            || LocalDate.of(2025, 12, 19)
        LocalDate.of(2031, 1, 3)   | LocalDate.of(2031, 4, 7)   | LocalDate.of(2029, 12, 31) | false    | 1110.82               | 13         | true           || 17680.00  || 0.00       || 2            || LocalDate.of(2031, 8, 7)
    }


    def "Tier 4 Student Sabbatical Office - Check invalid accommodation fees parameters"() {
        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        courseStartDate           | courseEndDate            | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 2, 26) | LocalDate.of(2003, 6, 5) | LocalDate.of(2002, 5, 23)  | true     | -1                    | 11         || 20541.70  || 0.00       || 2            || LocalDate.of(2003, 10, 5)
        LocalDate.of(2001, 7, 9)  | LocalDate.of(2001, 9, 5) | LocalDate.of(2000, 12, 24) | false    | -7                    | 13         || 18685.25  || 0.00       || 0            || LocalDate.of(2001, 11, 5)
    }

    def "Tier 4 Student Sabbatical Office - Check invalid characters accommodation fees parameters"() {
        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        courseStartDate           | courseEndDate            | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 2, 26) | LocalDate.of(2003, 6, 5) | LocalDate.of(2002, 5, 23)  | true     | "^&"                  | 11         || 20541.70  || 0.00       || 2            || LocalDate.of(2003, 10, 5)
        LocalDate.of(2001, 7, 9)  | LocalDate.of(2001, 9, 5) | LocalDate.of(2000, 12, 24) | false    | ")()"                 | 13         || 18685.25  || 0.00       || 0            || LocalDate.of(2001, 11, 5)
    }

    def "Tier 4 Student Sabbatical Office - Check rounding accommodation fees parameters"() {
        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isOk())

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate           | courseEndDate              | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2028, 12, 8) | LocalDate.of(2029, 10, 20) | LocalDate.of(2028, 4, 6)  | false    | 1239.5001             | 2          || 3510.50   || 0.00       || 2            || LocalDate.of(2030, 2, 20)
        LocalDate.of(1973, 8, 5)  | LocalDate.of(1974, 3, 22)  | LocalDate.of(1973, 1, 21) | true     | 1027.7834             | 3          || 6572.22   || 0.00       || 2            || LocalDate.of(1974, 7, 22)
        LocalDate.of(2022, 11, 6) | LocalDate.of(2023, 9, 7)   | LocalDate.of(2022, 8, 15) | true     | 1398.631              | 11         || 19855.00  || 1265.00    || 2            || LocalDate.of(2024, 1, 7)
        LocalDate.of(2012, 7, 31) | LocalDate.of(2013, 2, 20)  | LocalDate.of(2012, 4, 1)  | false    | 1057.80002            | 8          || 11852.20  || 0.00       || 2            || LocalDate.of(2013, 4, 20)
        LocalDate.of(2045, 8, 16) | LocalDate.of(2046, 7, 29)  | LocalDate.of(2044, 12, 7) | true     | 1931.60244            | 12         || 21545.00  || 1265.00    || 2            || LocalDate.of(2046, 11, 29)
        LocalDate.of(1980, 8, 17) | LocalDate.of(1981, 8, 28)  | LocalDate.of(1980, 1, 21) | false    | 169.876               | 9          || 14100.12  || 0.00       || 2            || LocalDate.of(1981, 12, 28)
        LocalDate.of(2050, 8, 26) | LocalDate.of(2051, 2, 11)  | LocalDate.of(2050, 7, 8)  | true     | 775.7399              | 5          || 10204.26  || 0.00       || 2            || LocalDate.of(2051, 4, 11)
    }

    def "Tier 4 Student Sabbatical Office - Check invalid dependants parameters"() {
        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2048, 11, 30) | LocalDate.of(2049, 3, 9)  | LocalDate.of(2048, 3, 17) | true     | 428.18                | -3         || 7171.82   || 0.00       || 2            || LocalDate.of(2049, 5, 9)
        LocalDate.of(2050, 7, 26)  | LocalDate.of(2050, 8, 12) | LocalDate.of(2049, 9, 22) | false    | 1543.94               | -20        || 0.00      || 1265.00    || 0            || LocalDate.of(2050, 10, 12)
        LocalDate.of(1993, 5, 21)  | LocalDate.of(1994, 5, 17) | null                      | false    | 1879.34               | -11        || 15725.00  || 1265.00    || 2            || LocalDate.of(1994, 7, 17)
    }

    def "Tier 4 Student Sabbatical Office - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2017, 3, 27)  | LocalDate.of(2017, 6, 27) | null                    | true     | 984.58                | ")£("      || 1545.42   || 0.00       || 2            || LocalDate.of(2017, 7, 4)
        LocalDate.of(2011, 12, 31) | LocalDate.of(2012, 3, 4)  | null                    | true     | 1708.30               | "%£"       || 1265.00   || 1265.00    || 2            || LocalDate.of(2012, 3, 11)
        LocalDate.of(2037, 10, 2)  | LocalDate.of(2038, 7, 20) | null                    | false    | 487.82                | "(&"       || 12422.18  || 0.00       || 2            || LocalDate.of(2038, 9, 20)
    }

    def "Tier 4 Student Sabbatical Office - Check invalid dependantsOnly parameters"() {
        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, 0, dependantsOnly)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependantsOnly")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2048, 11, 30) | LocalDate.of(2049, 3, 9)  | LocalDate.of(2048, 3, 17) | true     | 428.18                | -3             || 7171.82   || 0.00       || 2            || LocalDate.of(2049, 5, 9)
        LocalDate.of(2050, 7, 26)  | LocalDate.of(2050, 8, 12) | LocalDate.of(2049, 9, 22) | false    | 1543.94               | -20            || 0.00      || 1265.00    || 0            || LocalDate.of(2050, 10, 12)
        LocalDate.of(1993, 5, 21)  | LocalDate.of(1994, 5, 17) | null                      | false    | 1879.34               | -11            || 15725.00  || 1265.00    || 2            || LocalDate.of(1994, 7, 17)
    }

    def "Tier 4 Student Sabbatical Office - Check invalid characters dependantsOnly parameters"() {
        expect:
        def response = callApi("suso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, 0, dependantsOnly)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependantsOnly")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate | inLondon | accommodationFeesPaid | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2017, 3, 27)  | LocalDate.of(2017, 6, 27) | null                    | true     | 984.58                | ")£("          || 1545.42   || 0.00       || 2            || LocalDate.of(2017, 7, 4)
        LocalDate.of(2011, 12, 31) | LocalDate.of(2012, 3, 4)  | null                    | true     | 1708.30               | "%£"           || 1265.00   || 1265.00    || 2            || LocalDate.of(2012, 3, 11)
        LocalDate.of(2037, 10, 2)  | LocalDate.of(2038, 7, 20) | null                    | false    | 487.82                | "(&"           || 12422.18  || 0.00       || 2            || LocalDate.of(2038, 9, 20)
    }

}
