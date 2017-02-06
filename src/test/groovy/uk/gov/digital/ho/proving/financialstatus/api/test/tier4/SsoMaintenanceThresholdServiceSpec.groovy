package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import groovy.json.JsonSlurper
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
import uk.gov.digital.ho.proving.financialstatus.audit.EmbeddedMongoClientConfiguration
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.DeploymentDetails
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
@ContextConfiguration(classes = [ ServiceConfiguration.class, EmbeddedMongoClientConfiguration.class ])
class SsoMaintenanceThresholdServiceSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    AuditEventPublisher auditor = Mock()
    Authentication authenticator = Mock()

    def thresholdService = new ThresholdServiceTier4(
        TestUtilsTier4.maintenanceThresholdServiceBuilder(), TestUtilsTier4.getStudentTypeChecker(),
        TestUtilsTier4.getCourseTypeChecker(), serviceMessages, auditor, authenticator,
        new DeploymentDetails("localhost", "local")
    )

    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(), serviceMessages))
        .build()

    def url = TestUtilsTier4.thresholdUrl

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("originalCourseStartDate", (originalCourseStartDate == null) ? "" : originalCourseStartDate.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Tier 4 Student Sabbatical Office - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("sso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate           | courseEndDate             | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1997, 2, 11) | LocalDate.of(1998, 2, 3)  | LocalDate.of(1996, 12, 29) | false    | 0.00                  | 5          || 8830.00   || 0.00       || 2            || LocalDate.of(1998, 6, 3)
        LocalDate.of(2049, 8, 8)  | LocalDate.of(2050, 1, 25) | null                       | false    | 0.00                  | 0          || 2030.00   || 0.00       || 2            || LocalDate.of(2050, 2, 1)

    }

    def "Tier 4 Student Sabbatical Office - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("sso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate           | courseEndDate             | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2038, 6, 23) | LocalDate.of(2039, 1, 4)  | LocalDate.of(2037, 9, 26) | true     | 1397.01               | 0          || 1265.00   || 1265.00    || 2            || LocalDate.of(2039, 5, 4)
        LocalDate.of(2017, 2, 28) | LocalDate.of(2017, 11, 9) | null                      | true     | 0.00                  | 14         || 26190.00  || 0.00       || 2            || LocalDate.of(2018, 1, 9)

    }

    def "Tier 4 Student Sabbatical Office - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("sso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 2, 26)  | LocalDate.of(2003, 6, 5)  | LocalDate.of(2002, 5, 23)  | true     | 578.30                | 11         || 20541.70  || 0.00       || 2            || LocalDate.of(2003, 10, 5)
        LocalDate.of(2001, 7, 9)   | LocalDate.of(2001, 9, 5)  | LocalDate.of(2000, 12, 24) | false    | 1024.75               | 13         || 18685.25  || 0.00       || 0            || LocalDate.of(2001, 11, 5)
        LocalDate.of(1990, 8, 14)  | LocalDate.of(1991, 9, 13) | LocalDate.of(1989, 11, 20) | true     | 1281.30               | 5          || 9715.00   || 1265.00    || 2            || LocalDate.of(1992, 1, 13)
        LocalDate.of(1982, 7, 16)  | LocalDate.of(1983, 8, 18) | LocalDate.of(1981, 8, 12)  | false    | 1630.14               | 9          || 13005.00  || 1265.00    || 2            || LocalDate.of(1983, 12, 18)
        LocalDate.of(1998, 5, 20)  | LocalDate.of(1998, 6, 11) | LocalDate.of(1997, 12, 3)  | false    | 1716.73               | 4          || 5190.00   || 1265.00    || 0            || LocalDate.of(1998, 8, 11)
        LocalDate.of(2041, 12, 26) | LocalDate.of(2042, 1, 29) | null                       | true     | 1817.48               | 0          || 1265.00   || 1265.00    || 0            || LocalDate.of(2042, 2, 5)
        LocalDate.of(2041, 8, 22)  | LocalDate.of(2042, 9, 14) | LocalDate.of(2041, 2, 20)  | true     | 635.95                | 3          || 6964.05   || 0.00       || 2            || LocalDate.of(2043, 1, 14)
        LocalDate.of(2012, 9, 27)  | LocalDate.of(2012, 12, 8) | LocalDate.of(2011, 11, 14) | true     | 648.77                | 11         || 20471.23  || 0.00       || 2            || LocalDate.of(2013, 4, 8)
        LocalDate.of(2028, 11, 3)  | LocalDate.of(2028, 12, 6) | LocalDate.of(2027, 11, 15) | false    | 504.12                | 3          || 5605.88   || 0.00       || 0            || LocalDate.of(2029, 4, 6)
        LocalDate.of(2020, 4, 2)   | LocalDate.of(2020, 6, 29) | null                       | true     | 1190.92               | 0          || 1339.08   || 0.00       || 2            || LocalDate.of(2020, 7, 6)
    }

    def "Tier 4 Student Sabbatical Office - Check 'All variants'"() {
        expect:
        def response = callApi("sso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
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
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2045, 5, 13)  | LocalDate.of(2046, 1, 13)  | null                       | true     | 993.10                | 5          || 9986.90   || 0.00       || 2            || LocalDate.of(2046, 3, 13)
        LocalDate.of(2003, 3, 25)  | LocalDate.of(2003, 12, 6)  | LocalDate.of(2002, 3, 20)  | false    | 0.00                  | 10         || 15630.00  || 0.00       || 2            || LocalDate.of(2004, 4, 6)
        LocalDate.of(1999, 2, 7)   | LocalDate.of(1999, 12, 9)  | null                       | true     | 0.00                  | 14         || 26190.00  || 0.00       || 2            || LocalDate.of(2000, 2, 9)
        LocalDate.of(2025, 3, 1)   | LocalDate.of(2025, 7, 14)  | LocalDate.of(2025, 2, 18)  | true     | 166.22                | 12         || 22643.78  || 0.00       || 2            || LocalDate.of(2025, 7, 21)
        LocalDate.of(2008, 12, 11) | LocalDate.of(2009, 11, 14) | LocalDate.of(2008, 4, 27)  | false    | 804.28                | 7          || 10745.72  || 0.00       || 2            || LocalDate.of(2010, 3, 14)
        LocalDate.of(2009, 3, 24)  | LocalDate.of(2009, 9, 19)  | LocalDate.of(2009, 1, 25)  | false    | 532.03                | 3          || 5577.97   || 0.00       || 2            || LocalDate.of(2009, 11, 19)
        LocalDate.of(1993, 1, 19)  | LocalDate.of(1993, 6, 14)  | null                       | false    | 264.47                | 0          || 1765.53   || 0.00       || 2            || LocalDate.of(1993, 6, 21)
        LocalDate.of(2024, 8, 3)   | LocalDate.of(2025, 3, 26)  | LocalDate.of(2024, 6, 24)  | true     | 0.00                  | 4          || 9290.00   || 0.00       || 2            || LocalDate.of(2025, 5, 26)
        LocalDate.of(1987, 12, 30) | LocalDate.of(1988, 3, 8)   | LocalDate.of(1987, 12, 9)  | false    | 0.00                  | 12         || 18350.00  || 0.00       || 2            || LocalDate.of(1988, 3, 15)
        LocalDate.of(2046, 1, 18)  | LocalDate.of(2046, 11, 6)  | null                       | true     | 223.77                | 13         || 24276.23  || 0.00       || 2            || LocalDate.of(2047, 1, 6)
        LocalDate.of(2014, 8, 1)   | LocalDate.of(2014, 10, 29) | LocalDate.of(2014, 2, 9)   | false    | 1635.70               | 3          || 4845.00   || 1265.00    || 2            || LocalDate.of(2014, 12, 29)
        LocalDate.of(2026, 9, 14)  | LocalDate.of(2026, 11, 10) | LocalDate.of(2026, 2, 27)  | false    | 0.00                  | 13         || 19710.00  || 0.00       || 0            || LocalDate.of(2027, 1, 10)
        LocalDate.of(1974, 11, 17) | LocalDate.of(1975, 6, 23)  | LocalDate.of(1973, 11, 12) | true     | 0.00                  | 2          || 5910.00   || 0.00       || 2            || LocalDate.of(1975, 10, 23)
        LocalDate.of(2053, 8, 15)  | LocalDate.of(2053, 10, 5)  | LocalDate.of(2052, 7, 23)  | true     | 0.00                  | 5          || 10980.00  || 0.00       || 0            || LocalDate.of(2054, 2, 5)
        LocalDate.of(2039, 6, 2)   | LocalDate.of(2039, 8, 15)  | LocalDate.of(2038, 7, 16)  | true     | 457.94                | 14         || 25732.06  || 0.00       || 2            || LocalDate.of(2039, 12, 15)
        LocalDate.of(2008, 12, 6)  | LocalDate.of(2009, 9, 29)  | LocalDate.of(2008, 7, 1)   | false    | 331.14                | 11         || 16658.86  || 0.00       || 2            || LocalDate.of(2010, 1, 29)
        LocalDate.of(2002, 12, 31) | LocalDate.of(2003, 11, 3)  | LocalDate.of(2002, 3, 30)  | false    | 993.94                | 6          || 9196.06   || 0.00       || 2            || LocalDate.of(2004, 3, 3)
        LocalDate.of(2034, 5, 18)  | LocalDate.of(2035, 1, 26)  | LocalDate.of(2033, 7, 22)  | true     | 0.00                  | 9          || 17740.00  || 0.00       || 2            || LocalDate.of(2035, 5, 26)
        LocalDate.of(2026, 6, 26)  | LocalDate.of(2027, 1, 6)   | LocalDate.of(2026, 1, 28)  | false    | 0.00                  | 6          || 10190.00  || 0.00       || 2            || LocalDate.of(2027, 3, 6)
        LocalDate.of(1987, 3, 12)  | LocalDate.of(1987, 12, 19) | LocalDate.of(1987, 1, 20)  | false    | 889.37                | 8          || 12020.63  || 0.00       || 2            || LocalDate.of(1988, 2, 19)
    }


    def "Tier 4 Student Sabbatical Office - Check invalid accommodation fees parameters"() {
        expect:
        def response = callApi("sso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        courseStartDate           | courseEndDate            | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 2, 26) | LocalDate.of(2003, 6, 5) | LocalDate.of(2002, 5, 23)  | true     | -1                    | 11         || 20541.70  || 0.00       || 2            || LocalDate.of(2003, 10, 5)
        LocalDate.of(2001, 7, 9)  | LocalDate.of(2001, 9, 5) | LocalDate.of(2000, 12, 24) | false    | -7                    | 13         || 18685.25  || 0.00       || 0            || LocalDate.of(2001, 11, 5)
    }

    def "Tier 4 Student Sabbatical Office - Check invalid characters accommodation fees parameters"() {
        expect:
        def response = callApi("sso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        courseStartDate           | courseEndDate            | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 2, 26) | LocalDate.of(2003, 6, 5) | LocalDate.of(2002, 5, 23)  | true     | "^&"                  | 11         || 20541.70  || 0.00       || 2            || LocalDate.of(2003, 10, 5)
        LocalDate.of(2001, 7, 9)  | LocalDate.of(2001, 9, 5) | LocalDate.of(2000, 12, 24) | false    | ")()"                 | 13         || 18685.25  || 0.00       || 0            || LocalDate.of(2001, 11, 5)
    }

    def "Tier 4 Student Sabbatical Office - Check rounding accommodation fees parameters"() {
        expect:
        def response = callApi("sso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
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
        def response = callApi("sso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
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
        def response = callApi("sso", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2017, 3, 27)  | LocalDate.of(2017, 6, 27) | null                    | true     | 984.58                | ")£("      || 1545.42   || 0.00       || 2            || LocalDate.of(2017, 7, 4)
        LocalDate.of(2011, 12, 31) | LocalDate.of(2012, 3, 4)  | null                    | true     | 1708.30               | "%£"       || 1265.00   || 1265.00    || 2            || LocalDate.of(2012, 3, 11)
        LocalDate.of(2037, 10, 2)  | LocalDate.of(2038, 7, 20) | null                    | false    | 487.82                | "(&"       || 12422.18  || 0.00       || 2            || LocalDate.of(2038, 9, 20)
    }

}
