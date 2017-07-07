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
import uk.gov.digital.ho.proving.financialstatus.domain.ApplicantTypeChecker
import uk.gov.digital.ho.proving.financialstatus.domain.TierChecker
import uk.gov.digital.ho.proving.financialstatus.domain.VariantTypeChecker

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
class DoctorDentistMaintenanceThresholdServiceSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(TestUtilsTier4.getMessageSource())

    AuditEventPublisher mockAuditor = Mock()
    Authentication mockAuthenticator = Mock()
    TierChecker mockTierChecker = Mock()
    ApplicantTypeChecker mockApplicantTypeChecker = Mock()
    VariantTypeChecker mockVariantTypeChecker = Mock()

    def thresholdService = new ThresholdServiceTier4(
        TestUtilsTier4.maintenanceThresholdServiceBuilder(), TestUtilsTier4.getStudentTypeChecker(),
        TestUtilsTier4.getCourseTypeChecker(), serviceMessages, mockAuditor, mockAuthenticator,
        new DeploymentDetails("localhost", "local")
    )


    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(),
                                                        mockTierChecker,
                                                        mockApplicantTypeChecker,
                                                        mockVariantTypeChecker,
                                                        serviceMessages))
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

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2032, 12, 12) | LocalDate.of(2033, 11, 16) | null                       | false    | 0.00                  | 13         | false          || 19710.00  || 0.00       || 2            || LocalDate.of(2033, 12, 16)
        LocalDate.of(2013, 7, 4)   | LocalDate.of(2013, 11, 23) | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2013, 12, 23)
        LocalDate.of(2049, 11, 27) | LocalDate.of(2050, 4, 10)  | null                       | false    | 1096.79               | 0          | false          || 933.21    || 0.00       || 2            || LocalDate.of(2050, 5, 10)
        LocalDate.of(2027, 6, 30)  | LocalDate.of(2028, 3, 29)  | LocalDate.of(2026, 9, 13)  | false    | 0.00                  | 2          | false          || 4750.00   || 0.00       || 2            || LocalDate.of(2028, 4, 29)
        LocalDate.of(2000, 4, 5)   | LocalDate.of(2000, 10, 17) | LocalDate.of(2000, 3, 25)  | false    | 0.00                  | 6          | false          || 10190.00  || 0.00       || 2            || LocalDate.of(2000, 11, 17)
        LocalDate.of(1993, 6, 16)  | LocalDate.of(1994, 5, 19)  | null                       | false    | 976.78                | 13         | false          || 18733.22  || 0.00       || 2            || LocalDate.of(1994, 6, 19)
        LocalDate.of(2003, 12, 25) | LocalDate.of(2004, 4, 23)  | LocalDate.of(2003, 11, 5)  | false    | 0.00                  | 12         | false          || 18350.00  || 0.00       || 2            || LocalDate.of(2004, 5, 23)
        LocalDate.of(2024, 1, 9)   | LocalDate.of(2024, 9, 25)  | LocalDate.of(2024, 1, 1)   | false    | 0.00                  | 2          | false          || 4750.00   || 0.00       || 2            || LocalDate.of(2024, 10, 25)
        LocalDate.of(2006, 5, 10)  | LocalDate.of(2006, 9, 10)  | LocalDate.of(2005, 5, 20)  | false    | 291.80                | 12         | false          || 18058.20  || 0.00       || 2            || LocalDate.of(2006, 10, 10)
        LocalDate.of(2042, 4, 18)  | LocalDate.of(2042, 4, 24)  | null                       | false    | 0.00                  | 0          | false          || 1015.00   || 0.00       || 0            || LocalDate.of(2042, 5, 24)
        LocalDate.of(1998, 7, 7)   | LocalDate.of(1998, 12, 18) | LocalDate.of(1997, 10, 27) | false    | 0.00                  | 3          | false          || 6110.00   || 0.00       || 2            || LocalDate.of(1999, 1, 18)
        LocalDate.of(1992, 8, 16)  | LocalDate.of(1993, 3, 18)  | LocalDate.of(1992, 5, 14)  | false    | 569.49                | 10         | false          || 15060.51  || 0.00       || 2            || LocalDate.of(1993, 4, 18)
        LocalDate.of(2044, 6, 4)   | LocalDate.of(2045, 2, 7)   | LocalDate.of(2043, 12, 20) | false    | 1985.07               | 12         | false          || 17085.00  || 1265.00    || 2            || LocalDate.of(2045, 3, 7)
        LocalDate.of(2022, 12, 10) | LocalDate.of(2023, 11, 13) | LocalDate.of(2022, 9, 28)  | false    | 1448.79               | 6          | false          || 8925.00   || 1265.00    || 2            || LocalDate.of(2023, 12, 13)
        LocalDate.of(2003, 8, 15)  | LocalDate.of(2004, 1, 17)  | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2004, 2, 17)
        LocalDate.of(2031, 2, 20)  | LocalDate.of(2031, 7, 2)   | null                       | false    | 172.41                | 0          | false          || 1857.59   || 0.00       || 2            || LocalDate.of(2031, 8, 2)
        LocalDate.of(1975, 2, 24)  | LocalDate.of(1975, 9, 10)  | LocalDate.of(1974, 9, 25)  | false    | 1698.49               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(1975, 10, 10)
        LocalDate.of(2000, 9, 16)  | LocalDate.of(2001, 3, 1)   | LocalDate.of(1999, 8, 19)  | false    | 1060.00               | 4          | false          || 6410.00   || 0.00       || 2            || LocalDate.of(2001, 4, 1)
        LocalDate.of(1988, 3, 12)  | LocalDate.of(1988, 12, 13) | LocalDate.of(1987, 9, 22)  | false    | 0.00                  | 11         | false          || 16990.00  || 0.00       || 2            || LocalDate.of(1989, 1, 13)
        LocalDate.of(2048, 6, 10)  | LocalDate.of(2049, 1, 14)  | null                       | false    | 399.61                | 5          | false          || 8430.39   || 0.00       || 2            || LocalDate.of(2049, 2, 14)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2009, 11, 27) | LocalDate.of(2010, 2, 26)  | null                       | true     | 1554.02               | 0          | false          || 1265.00   || 1265.00    || 2            || LocalDate.of(2010, 3, 26)
        LocalDate.of(1986, 12, 16) | LocalDate.of(1987, 1, 26)  | null                       | true     | 1333.92               | 0          | false          || 1265.00   || 1265.00    || 0            || LocalDate.of(1987, 2, 26)
        LocalDate.of(2041, 9, 27)  | LocalDate.of(2041, 11, 13) | LocalDate.of(2041, 6, 26)  | true     | 33.49                 | 0          | false          || 2496.51   || 0.00       || 0            || LocalDate.of(2041, 12, 13)
        LocalDate.of(2038, 1, 15)  | LocalDate.of(2038, 8, 11)  | null                       | true     | 707.92                | 8          | false          || 15342.08  || 0.00       || 2            || LocalDate.of(2038, 9, 11)
        LocalDate.of(2003, 1, 23)  | LocalDate.of(2003, 3, 25)  | null                       | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 2            || LocalDate.of(2003, 4, 25)
        LocalDate.of(1994, 11, 20) | LocalDate.of(1994, 12, 27) | LocalDate.of(1994, 5, 30)  | true     | 288.03                | 1          | false          || 3931.97   || 0.00       || 0            || LocalDate.of(1995, 1, 27)
        LocalDate.of(2039, 7, 4)   | LocalDate.of(2040, 1, 5)   | LocalDate.of(2038, 8, 3)   | true     | 0.00                  | 5          | false          || 10980.00  || 0.00       || 2            || LocalDate.of(2040, 2, 5)
        LocalDate.of(2034, 8, 9)   | LocalDate.of(2035, 6, 13)  | null                       | true     | 0.00                  | 7          | false          || 14360.00  || 0.00       || 2            || LocalDate.of(2035, 7, 13)
        LocalDate.of(2008, 2, 20)  | LocalDate.of(2009, 2, 9)   | LocalDate.of(2007, 11, 19) | true     | 1378.96               | 3          | false          || 6335.00   || 1265.00    || 2            || LocalDate.of(2009, 3, 9)
        LocalDate.of(1979, 9, 30)  | LocalDate.of(1980, 10, 23) | null                       | true     | 0.00                  | 1          | false          || 4220.00   || 0.00       || 2            || LocalDate.of(1980, 11, 23)
        LocalDate.of(2016, 9, 21)  | LocalDate.of(2017, 4, 15)  | LocalDate.of(2016, 7, 3)   | true     | 1417.61               | 4          | false          || 8025.00   || 1265.00    || 2            || LocalDate.of(2017, 5, 15)
        LocalDate.of(2028, 4, 1)   | LocalDate.of(2028, 8, 18)  | LocalDate.of(2027, 4, 5)   | true     | 0.00                  | 1          | false          || 4220.00   || 0.00       || 2            || LocalDate.of(2028, 9, 18)
        LocalDate.of(1984, 2, 28)  | LocalDate.of(1985, 2, 14)  | LocalDate.of(1984, 2, 12)  | true     | 455.49                | 0          | false          || 2074.51   || 0.00       || 2            || LocalDate.of(1985, 3, 14)
        LocalDate.of(2020, 10, 6)  | LocalDate.of(2020, 12, 6)  | LocalDate.of(2019, 10, 18) | true     | 1709.54               | 8          | false          || 14785.00  || 1265.00    || 2            || LocalDate.of(2021, 1, 6)
        LocalDate.of(2006, 5, 4)   | LocalDate.of(2006, 7, 26)  | null                       | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 2            || LocalDate.of(2006, 8, 26)
        LocalDate.of(2035, 8, 15)  | LocalDate.of(2036, 4, 21)  | LocalDate.of(2034, 11, 16) | true     | 1090.80               | 4          | false          || 8199.20   || 0.00       || 2            || LocalDate.of(2036, 5, 21)
        LocalDate.of(1996, 8, 18)  | LocalDate.of(1997, 8, 5)   | LocalDate.of(1996, 7, 27)  | true     | 0.00                  | 4          | false          || 9290.00   || 0.00       || 2            || LocalDate.of(1997, 9, 5)
        LocalDate.of(2035, 12, 21) | LocalDate.of(2036, 1, 17)  | null                       | true     | 0.00                  | 0          | false          || 1265.00   || 0.00       || 0            || LocalDate.of(2036, 2, 17)
        LocalDate.of(2003, 10, 13) | LocalDate.of(2003, 10, 17) | LocalDate.of(2003, 8, 5)   | true     | 0.00                  | 14         | false          || 24925.00  || 0.00       || 0            || LocalDate.of(2003, 11, 17)
        LocalDate.of(2040, 6, 30)  | LocalDate.of(2040, 9, 27)  | null                       | true     | 736.84                | 0          | false          || 1793.16   || 0.00       || 2            || LocalDate.of(2040, 10, 27)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Continuation course'"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2020, 4, 12)  | LocalDate.of(2020, 5, 11)  | LocalDate.of(2019, 9, 22) | false    | 747.72                | 5          | false          || 7067.28   || 0.00       || 0            || LocalDate.of(2020, 6, 11)
        LocalDate.of(1993, 8, 11)  | LocalDate.of(1994, 6, 28)  | LocalDate.of(1992, 12, 8) | false    | 875.53                | 11         | false          || 16114.47  || 0.00       || 2            || LocalDate.of(1994, 7, 28)
        LocalDate.of(1998, 11, 16) | LocalDate.of(1999, 10, 3)  | LocalDate.of(1997, 11, 7) | false    | 0.00                  | 4          | false          || 7470.00   || 0.00       || 2            || LocalDate.of(1999, 11, 3)
        LocalDate.of(2044, 7, 10)  | LocalDate.of(2045, 6, 14)  | LocalDate.of(2044, 6, 24) | true     | 193.64                | 11         | false          || 20926.36  || 0.00       || 2            || LocalDate.of(2045, 7, 14)
        LocalDate.of(2034, 4, 11)  | LocalDate.of(2034, 9, 7)   | LocalDate.of(2033, 6, 15) | true     | 0.00                  | 5          | false          || 10980.00  || 0.00       || 2            || LocalDate.of(2034, 10, 7)
        LocalDate.of(2005, 6, 16)  | LocalDate.of(2005, 9, 2)   | LocalDate.of(2005, 3, 5)  | true     | 0.00                  | 8          | false          || 16050.00  || 0.00       || 2            || LocalDate.of(2005, 10, 2)
        LocalDate.of(2004, 3, 27)  | LocalDate.of(2004, 12, 16) | LocalDate.of(2003, 9, 30) | false    | 1361.87               | 9          | false          || 13005.00  || 1265.00    || 2            || LocalDate.of(2005, 1, 16)
        LocalDate.of(1979, 6, 17)  | LocalDate.of(1979, 8, 31)  | LocalDate.of(1978, 7, 11) | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1979, 9, 30)
        LocalDate.of(1983, 1, 21)  | LocalDate.of(1983, 4, 11)  | LocalDate.of(1982, 1, 1)  | true     | 1563.99               | 4          | false          || 8025.00   || 1265.00    || 2            || LocalDate.of(1983, 5, 11)
        LocalDate.of(2026, 7, 2)   | LocalDate.of(2027, 5, 10)  | LocalDate.of(2026, 1, 6)  | true     | 0.00                  | 7          | false          || 14360.00  || 0.00       || 2            || LocalDate.of(2027, 6, 10)
        LocalDate.of(2008, 9, 30)  | LocalDate.of(2008, 11, 26) | LocalDate.of(2007, 9, 16) | true     | 0.00                  | 5          | false          || 10980.00  || 0.00       || 0            || LocalDate.of(2008, 12, 26)
        LocalDate.of(1982, 5, 20)  | LocalDate.of(1982, 7, 15)  | LocalDate.of(1981, 11, 6) | true     | 0.00                  | 10         | false          || 19430.00  || 0.00       || 0            || LocalDate.of(1982, 8, 15)
        LocalDate.of(1989, 2, 17)  | LocalDate.of(1989, 10, 27) | LocalDate.of(1989, 1, 2)  | true     | 0.00                  | 10         | false          || 19430.00  || 0.00       || 2            || LocalDate.of(1989, 11, 27)
        LocalDate.of(1995, 9, 17)  | LocalDate.of(1996, 9, 6)   | LocalDate.of(1995, 3, 21) | true     | 0.00                  | 9          | false          || 17740.00  || 0.00       || 2            || LocalDate.of(1996, 10, 6)
        LocalDate.of(2021, 9, 24)  | LocalDate.of(2022, 4, 20)  | LocalDate.of(2020, 9, 6)  | false    | 0.00                  | 6          | false          || 10190.00  || 0.00       || 2            || LocalDate.of(2022, 5, 20)
        LocalDate.of(1992, 3, 11)  | LocalDate.of(1992, 3, 20)  | LocalDate.of(1991, 7, 17) | true     | 0.00                  | 3          | false          || 6335.00   || 0.00       || 0            || LocalDate.of(1992, 4, 20)
        LocalDate.of(1998, 11, 30) | LocalDate.of(1999, 11, 19) | LocalDate.of(1998, 6, 1)  | true     | 109.37                | 4          | false          || 9180.63   || 0.00       || 2            || LocalDate.of(1999, 12, 19)
        LocalDate.of(2025, 8, 14)  | LocalDate.of(2026, 2, 9)   | LocalDate.of(2025, 2, 11) | false    | 0.00                  | 13         | false          || 19710.00  || 0.00       || 2            || LocalDate.of(2026, 3, 9)
        LocalDate.of(2016, 3, 24)  | LocalDate.of(2016, 7, 4)   | LocalDate.of(2016, 2, 22) | false    | 0.00                  | 9          | false          || 14270.00  || 0.00       || 2            || LocalDate.of(2016, 8, 4)
        LocalDate.of(1982, 8, 9)   | LocalDate.of(1983, 7, 31)  | LocalDate.of(1981, 10, 7) | false    | 0.00                  | 10         | false          || 15630.00  || 0.00       || 2            || LocalDate.of(1983, 8, 31)

    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2017, 5, 18)  | LocalDate.of(2017, 12, 5)  | LocalDate.of(2016, 6, 28)  | false    | 1722.21               | 14         | false          || 19805.00  || 1265.00    || 2            || LocalDate.of(2018, 1, 5)
        LocalDate.of(2010, 9, 24)  | LocalDate.of(2011, 7, 29)  | null                       | false    | 421.39                | 5          | false          || 8408.61   || 0.00       || 2            || LocalDate.of(2011, 8, 29)
        LocalDate.of(2004, 4, 17)  | LocalDate.of(2004, 5, 27)  | null                       | true     | 1244.83               | 0          | false          || 1285.17   || 0.00       || 0            || LocalDate.of(2004, 6, 27)
        LocalDate.of(2007, 5, 6)   | LocalDate.of(2007, 12, 10) | null                       | false    | 221.75                | 9          | false          || 14048.25  || 0.00       || 2            || LocalDate.of(2008, 1, 10)
        LocalDate.of(2013, 6, 7)   | LocalDate.of(2014, 5, 25)  | LocalDate.of(2013, 3, 15)  | false    | 1413.58               | 2          | false          || 3485.00   || 1265.00    || 2            || LocalDate.of(2014, 6, 25)
        LocalDate.of(1999, 3, 3)   | LocalDate.of(2000, 2, 24)  | LocalDate.of(1998, 7, 25)  | false    | 834.92                | 7          | false          || 10715.08  || 0.00       || 2            || LocalDate.of(2000, 3, 24)
        LocalDate.of(2035, 11, 3)  | LocalDate.of(2036, 6, 26)  | LocalDate.of(2035, 3, 11)  | true     | 1890.54               | 14         | false          || 24925.00  || 1265.00    || 2            || LocalDate.of(2036, 7, 26)
        LocalDate.of(2026, 10, 9)  | LocalDate.of(2027, 8, 18)  | null                       | true     | 950.32                | 1          | false          || 3269.68   || 0.00       || 2            || LocalDate.of(2027, 9, 18)
        LocalDate.of(2021, 3, 14)  | LocalDate.of(2021, 10, 1)  | null                       | false    | 312.78                | 3          | false          || 5797.22   || 0.00       || 2            || LocalDate.of(2021, 11, 1)
        LocalDate.of(2024, 12, 26) | LocalDate.of(2025, 5, 12)  | null                       | false    | 636.90                | 0          | false          || 1393.10   || 0.00       || 2            || LocalDate.of(2025, 6, 12)
        LocalDate.of(1974, 2, 25)  | LocalDate.of(1974, 5, 16)  | null                       | true     | 1597.40               | 0          | false          || 1265.00   || 1265.00    || 2            || LocalDate.of(1974, 6, 16)
        LocalDate.of(2029, 3, 24)  | LocalDate.of(2029, 10, 6)  | null                       | true     | 1497.25               | 11         | false          || 19855.00  || 1265.00    || 2            || LocalDate.of(2029, 11, 6)
        LocalDate.of(1991, 9, 13)  | LocalDate.of(1992, 8, 22)  | LocalDate.of(1991, 3, 5)   | false    | 737.19                | 11         | false          || 16252.81  || 0.00       || 2            || LocalDate.of(1992, 9, 22)
        LocalDate.of(2009, 10, 22) | LocalDate.of(2010, 10, 9)  | null                       | true     | 1081.01               | 8          | false          || 14968.99  || 0.00       || 2            || LocalDate.of(2010, 11, 9)
        LocalDate.of(2046, 5, 31)  | LocalDate.of(2046, 8, 27)  | LocalDate.of(2045, 6, 30)  | false    | 1682.58               | 7          | false          || 10285.00  || 1265.00    || 2            || LocalDate.of(2046, 9, 27)
        LocalDate.of(2034, 1, 28)  | LocalDate.of(2034, 5, 24)  | LocalDate.of(2033, 7, 2)   | true     | 1650.83               | 2          | false          || 4645.00   || 1265.00    || 2            || LocalDate.of(2034, 6, 24)
        LocalDate.of(2015, 6, 29)  | LocalDate.of(2015, 12, 15) | LocalDate.of(2014, 11, 24) | false    | 998.49                | 6          | false          || 9191.51   || 0.00       || 2            || LocalDate.of(2016, 1, 15)
        LocalDate.of(2010, 3, 9)   | LocalDate.of(2010, 9, 12)  | LocalDate.of(2009, 4, 23)  | true     | 846.33                | 14         | false          || 25343.67  || 0.00       || 2            || LocalDate.of(2010, 10, 12)
        LocalDate.of(1985, 12, 21) | LocalDate.of(1986, 12, 22) | LocalDate.of(1985, 10, 20) | false    | 1247.27               | 7          | false          || 10302.73  || 0.00       || 2            || LocalDate.of(1987, 1, 22)
        LocalDate.of(1987, 4, 28)  | LocalDate.of(1987, 8, 9)   | null                       | false    | 1330.89               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(1987, 9, 9)
    }

    // Dependants only

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Non Inner London Borough'  (dependants only)"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2042, 8, 31)  | LocalDate.of(2042, 12, 4)  | LocalDate.of(2042, 8, 23)  | false    | 1111.05               | 6          | true           || 8160.00   || 0.00       || 2            || LocalDate.of(2043, 1, 4)
        LocalDate.of(2051, 4, 1)   | LocalDate.of(2051, 11, 10) | LocalDate.of(2050, 12, 16) | false    | 0.00                  | 4          | true           || 5440.00   || 0.00       || 2            || LocalDate.of(2051, 12, 10)
        LocalDate.of(2051, 7, 18)  | LocalDate.of(2051, 10, 28) | LocalDate.of(2050, 8, 6)   | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2051, 11, 28)
        LocalDate.of(1981, 11, 22) | LocalDate.of(1982, 11, 21) | LocalDate.of(1981, 8, 12)  | false    | 0.00                  | 3          | true           || 4080.00   || 0.00       || 2            || LocalDate.of(1982, 12, 21)
        LocalDate.of(1994, 9, 23)  | LocalDate.of(1995, 8, 6)   | null                       | false    | 201.11                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1995, 9, 6)
        LocalDate.of(1977, 6, 22)  | LocalDate.of(1977, 9, 3)   | null                       | false    | 393.89                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1977, 10, 3)
        LocalDate.of(2019, 4, 25)  | LocalDate.of(2019, 12, 26) | LocalDate.of(2019, 2, 18)  | false    | 615.04                | 13         | true           || 17680.00  || 0.00       || 2            || LocalDate.of(2020, 1, 26)
        LocalDate.of(1986, 7, 8)   | LocalDate.of(1986, 11, 5)  | null                       | false    | 1701.65               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(1986, 12, 5)
        LocalDate.of(2013, 10, 13) | LocalDate.of(2014, 3, 23)  | LocalDate.of(2013, 4, 7)   | false    | 0.00                  | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(2014, 4, 23)
        LocalDate.of(2010, 11, 16) | LocalDate.of(2011, 6, 2)   | LocalDate.of(2010, 5, 16)  | false    | 0.00                  | 7          | true           || 9520.00   || 0.00       || 2            || LocalDate.of(2011, 7, 2)
        LocalDate.of(2046, 7, 21)  | LocalDate.of(2047, 3, 11)  | LocalDate.of(2046, 2, 3)   | false    | 0.00                  | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(2047, 4, 11)
        LocalDate.of(2012, 5, 11)  | LocalDate.of(2012, 6, 6)   | null                       | false    | 1361.33               | 0          | true           || 0.00      || 1265.00    || 0            || LocalDate.of(2012, 7, 6)
        LocalDate.of(2039, 5, 23)  | LocalDate.of(2039, 9, 23)  | LocalDate.of(2039, 4, 27)  | false    | 0.00                  | 6          | true           || 8160.00   || 0.00       || 2            || LocalDate.of(2039, 10, 23)
        LocalDate.of(2005, 12, 11) | LocalDate.of(2006, 11, 8)  | null                       | false    | 1113.65               | 9          | true           || 12240.00  || 0.00       || 2            || LocalDate.of(2006, 12, 8)
        LocalDate.of(2040, 11, 24) | LocalDate.of(2041, 6, 11)  | LocalDate.of(2040, 1, 12)  | false    | 1601.17               | 6          | true           || 8160.00   || 1265.00    || 2            || LocalDate.of(2041, 7, 11)
        LocalDate.of(2034, 1, 1)   | LocalDate.of(2034, 11, 23) | null                       | false    | 0.00                  | 7          | true           || 9520.00   || 0.00       || 2            || LocalDate.of(2034, 12, 23)
        LocalDate.of(2004, 8, 18)  | LocalDate.of(2005, 7, 19)  | LocalDate.of(2003, 9, 25)  | false    | 1035.75               | 6          | true           || 8160.00   || 0.00       || 2            || LocalDate.of(2005, 8, 19)
        LocalDate.of(2030, 6, 9)   | LocalDate.of(2031, 3, 8)   | LocalDate.of(2029, 7, 23)  | false    | 0.00                  | 2          | true           || 2720.00   || 0.00       || 2            || LocalDate.of(2031, 4, 8)
        LocalDate.of(2022, 9, 12)  | LocalDate.of(2023, 2, 1)   | LocalDate.of(2022, 3, 10)  | false    | 0.00                  | 14         | true           || 19040.00  || 0.00       || 2            || LocalDate.of(2023, 3, 1)
        LocalDate.of(2048, 7, 17)  | LocalDate.of(2049, 6, 27)  | LocalDate.of(2047, 11, 27) | false    | 1487.01               | 2          | true           || 2720.00   || 1265.00    || 2            || LocalDate.of(2049, 7, 27)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Inner London Borough'  (dependants only)"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2044, 12, 9)  | LocalDate.of(2045, 1, 6)   | LocalDate.of(2044, 7, 7)   | true     | 0.00                  | 8          | true           || 13520.00  || 0.00       || 0            || LocalDate.of(2045, 2, 6)
        LocalDate.of(2014, 10, 25) | LocalDate.of(2015, 8, 22)  | LocalDate.of(2014, 9, 23)  | true     | 1796.01               | 11         | true           || 18590.00  || 1265.00    || 2            || LocalDate.of(2015, 9, 22)
        LocalDate.of(2011, 5, 18)  | LocalDate.of(2011, 8, 3)   | null                       | true     | 689.91                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2011, 9, 3)
        LocalDate.of(2050, 8, 14)  | LocalDate.of(2051, 8, 24)  | LocalDate.of(2049, 12, 24) | true     | 1439.89               | 7          | true           || 11830.00  || 1265.00    || 2            || LocalDate.of(2051, 9, 24)
        LocalDate.of(2046, 12, 19) | LocalDate.of(2047, 11, 4)  | LocalDate.of(2046, 6, 20)  | true     | 667.06                | 5          | true           || 8450.00   || 0.00       || 2            || LocalDate.of(2047, 12, 4)
        LocalDate.of(1993, 9, 5)   | LocalDate.of(1993, 11, 25) | LocalDate.of(1992, 10, 31) | true     | 1656.81               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(1993, 12, 25)
        LocalDate.of(2015, 4, 29)  | LocalDate.of(2015, 10, 4)  | LocalDate.of(2015, 3, 7)   | true     | 0.00                  | 8          | true           || 13520.00  || 0.00       || 2            || LocalDate.of(2015, 11, 4)
        LocalDate.of(1978, 10, 28) | LocalDate.of(1979, 2, 12)  | LocalDate.of(1977, 10, 9)  | true     | 0.00                  | 11         | true           || 18590.00  || 0.00       || 2            || LocalDate.of(1979, 3, 12)
        LocalDate.of(1998, 7, 13)  | LocalDate.of(1999, 3, 9)   | LocalDate.of(1998, 4, 17)  | true     | 1057.32               | 5          | true           || 8450.00   || 0.00       || 2            || LocalDate.of(1999, 4, 9)
        LocalDate.of(1979, 11, 19) | LocalDate.of(1980, 11, 15) | LocalDate.of(1978, 10, 25) | true     | 0.00                  | 6          | true           || 10140.00  || 0.00       || 2            || LocalDate.of(1980, 12, 15)
        LocalDate.of(2022, 9, 22)  | LocalDate.of(2023, 4, 13)  | null                       | true     | 545.66                | 10         | true           || 16900.00  || 0.00       || 2            || LocalDate.of(2023, 5, 13)
        LocalDate.of(1987, 7, 26)  | LocalDate.of(1988, 4, 20)  | LocalDate.of(1986, 8, 16)  | true     | 1392.22               | 2          | true           || 3380.00   || 1265.00    || 2            || LocalDate.of(1988, 5, 20)
        LocalDate.of(1975, 9, 7)   | LocalDate.of(1975, 12, 19) | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1976, 1, 19)
        LocalDate.of(2003, 10, 25) | LocalDate.of(2004, 4, 6)   | LocalDate.of(2003, 7, 8)   | true     | 1693.00               | 10         | true           || 16900.00  || 1265.00    || 2            || LocalDate.of(2004, 5, 6)
        LocalDate.of(2038, 5, 7)   | LocalDate.of(2039, 3, 14)  | null                       | true     | 1036.91               | 7          | true           || 11830.00  || 0.00       || 2            || LocalDate.of(2039, 4, 14)
        LocalDate.of(2003, 10, 12) | LocalDate.of(2004, 7, 29)  | LocalDate.of(2002, 12, 31) | true     | 1355.21               | 13         | true           || 21970.00  || 1265.00    || 2            || LocalDate.of(2004, 8, 29)
        LocalDate.of(1975, 10, 22) | LocalDate.of(1976, 5, 26)  | null                       | true     | 507.10                | 5          | true           || 8450.00   || 0.00       || 2            || LocalDate.of(1976, 6, 26)
        LocalDate.of(1976, 12, 13) | LocalDate.of(1977, 3, 31)  | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1977, 4, 30)
        LocalDate.of(2000, 5, 31)  | LocalDate.of(2001, 2, 25)  | LocalDate.of(1999, 9, 7)   | true     | 0.00                  | 3          | true           || 5070.00   || 0.00       || 2            || LocalDate.of(2001, 3, 25)
        LocalDate.of(1977, 10, 10) | LocalDate.of(1978, 5, 22)  | null                       | true     | 757.57                | 11         | true           || 18590.00  || 0.00       || 2            || LocalDate.of(1978, 6, 22)
    }


    def "Tier 4 Post Grad Doctor or Dentist - Check 'Continuation course'  (dependants only)"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1996, 7, 28)  | LocalDate.of(1997, 8, 28)  | LocalDate.of(1995, 11, 7)  | false    | 0.00                  | 4          | true           || 5440.00   || 0.00       || 2            || LocalDate.of(1997, 9, 28)
        LocalDate.of(2001, 7, 29)  | LocalDate.of(2002, 2, 1)   | LocalDate.of(2000, 8, 25)  | false    | 862.43                | 4          | true           || 5440.00   || 0.00       || 2            || LocalDate.of(2002, 3, 1)
        LocalDate.of(1980, 2, 20)  | LocalDate.of(1981, 1, 23)  | LocalDate.of(1979, 4, 15)  | false    | 0.00                  | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(1981, 2, 23)
        LocalDate.of(2044, 5, 19)  | LocalDate.of(2045, 4, 14)  | LocalDate.of(2043, 5, 14)  | true     | 1382.00               | 10         | true           || 16900.00  || 1265.00    || 2            || LocalDate.of(2045, 5, 14)
        LocalDate.of(2037, 3, 6)   | LocalDate.of(2037, 5, 11)  | LocalDate.of(2036, 4, 27)  | false    | 654.66                | 8          | true           || 10880.00  || 0.00       || 2            || LocalDate.of(2037, 6, 11)
        LocalDate.of(1983, 7, 9)   | LocalDate.of(1984, 5, 4)   | LocalDate.of(1982, 12, 13) | false    | 0.00                  | 7          | true           || 9520.00   || 0.00       || 2            || LocalDate.of(1984, 6, 4)
        LocalDate.of(1985, 12, 9)  | LocalDate.of(1986, 6, 27)  | LocalDate.of(1985, 10, 9)  | false    | 48.33                 | 3          | true           || 4080.00   || 0.00       || 2            || LocalDate.of(1986, 7, 27)
        LocalDate.of(2050, 7, 30)  | LocalDate.of(2051, 7, 17)  | LocalDate.of(2049, 9, 21)  | true     | 0.00                  | 6          | true           || 10140.00  || 0.00       || 2            || LocalDate.of(2051, 8, 17)
        LocalDate.of(1988, 9, 30)  | LocalDate.of(1988, 12, 13) | LocalDate.of(1988, 7, 14)  | false    | 921.15                | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(1989, 1, 13)
        LocalDate.of(2049, 3, 13)  | LocalDate.of(2049, 8, 21)  | LocalDate.of(2048, 11, 10) | true     | 0.00                  | 8          | true           || 13520.00  || 0.00       || 2            || LocalDate.of(2049, 9, 21)
        LocalDate.of(2007, 7, 13)  | LocalDate.of(2007, 8, 25)  | LocalDate.of(2007, 1, 5)   | false    | 74.15                 | 1          | true           || 1360.00   || 0.00       || 0            || LocalDate.of(2007, 9, 25)
        LocalDate.of(2003, 5, 17)  | LocalDate.of(2004, 2, 6)   | LocalDate.of(2003, 1, 9)   | false    | 671.05                | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(2004, 3, 6)
        LocalDate.of(1984, 7, 14)  | LocalDate.of(1984, 8, 2)   | LocalDate.of(1984, 6, 11)  | true     | 1338.69               | 7          | true           || 11830.00  || 1265.00    || 0            || LocalDate.of(1984, 9, 2)
        LocalDate.of(2051, 4, 12)  | LocalDate.of(2052, 4, 22)  | LocalDate.of(2050, 3, 23)  | false    | 871.98                | 3          | true           || 4080.00   || 0.00       || 2            || LocalDate.of(2052, 5, 22)
        LocalDate.of(2012, 4, 23)  | LocalDate.of(2012, 7, 4)   | LocalDate.of(2011, 8, 13)  | false    | 0.00                  | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(2012, 8, 4)
        LocalDate.of(2024, 5, 11)  | LocalDate.of(2025, 3, 26)  | LocalDate.of(2024, 4, 16)  | false    | 0.00                  | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(2025, 4, 26)
        LocalDate.of(2023, 3, 14)  | LocalDate.of(2024, 1, 24)  | LocalDate.of(2022, 7, 8)   | false    | 1867.49               | 11         | true           || 14960.00  || 1265.00    || 2            || LocalDate.of(2024, 2, 24)
        LocalDate.of(1982, 12, 17) | LocalDate.of(1983, 1, 7)   | LocalDate.of(1982, 9, 2)   | true     | 291.51                | 7          | true           || 11830.00  || 0.00       || 0            || LocalDate.of(1983, 2, 7)
        LocalDate.of(2040, 1, 20)  | LocalDate.of(2040, 9, 6)   | LocalDate.of(2039, 1, 11)  | false    | 67.82                 | 5          | true           || 6800.00   || 0.00       || 2            || LocalDate.of(2040, 10, 6)
        LocalDate.of(1980, 8, 12)  | LocalDate.of(1981, 8, 28)  | LocalDate.of(1980, 6, 13)  | true     | 1278.42               | 3          | true           || 5070.00   || 1265.00    || 2            || LocalDate.of(1981, 9, 28)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Accommodation Fees paid (dependants only)'"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2017, 5, 18)  | LocalDate.of(2017, 12, 5)  | LocalDate.of(2016, 6, 28)  | false    | 1722.21               | 14         | false          || 19805.00  || 1265.00    || 2            || LocalDate.of(2018, 1, 5)
        LocalDate.of(2010, 9, 24)  | LocalDate.of(2011, 7, 29)  | null                       | false    | 421.39                | 5          | false          || 8408.61   || 0.00       || 2            || LocalDate.of(2011, 8, 29)
        LocalDate.of(2004, 4, 17)  | LocalDate.of(2004, 5, 27)  | null                       | true     | 1244.83               | 0          | false          || 1285.17   || 0.00       || 0            || LocalDate.of(2004, 6, 27)
        LocalDate.of(2007, 5, 6)   | LocalDate.of(2007, 12, 10) | null                       | false    | 221.75                | 9          | false          || 14048.25  || 0.00       || 2            || LocalDate.of(2008, 1, 10)
        LocalDate.of(2013, 6, 7)   | LocalDate.of(2014, 5, 25)  | LocalDate.of(2013, 3, 15)  | false    | 1413.58               | 2          | false          || 3485.00   || 1265.00    || 2            || LocalDate.of(2014, 6, 25)
        LocalDate.of(1999, 3, 3)   | LocalDate.of(2000, 2, 24)  | LocalDate.of(1998, 7, 25)  | false    | 834.92                | 7          | false          || 10715.08  || 0.00       || 2            || LocalDate.of(2000, 3, 24)
        LocalDate.of(2035, 11, 3)  | LocalDate.of(2036, 6, 26)  | LocalDate.of(2035, 3, 11)  | true     | 1890.54               | 14         | false          || 24925.00  || 1265.00    || 2            || LocalDate.of(2036, 7, 26)
        LocalDate.of(2026, 10, 9)  | LocalDate.of(2027, 8, 18)  | null                       | true     | 950.32                | 1          | false          || 3269.68   || 0.00       || 2            || LocalDate.of(2027, 9, 18)
        LocalDate.of(2021, 3, 14)  | LocalDate.of(2021, 10, 1)  | null                       | false    | 312.78                | 3          | false          || 5797.22   || 0.00       || 2            || LocalDate.of(2021, 11, 1)
        LocalDate.of(2024, 12, 26) | LocalDate.of(2025, 5, 12)  | null                       | false    | 636.90                | 0          | false          || 1393.10   || 0.00       || 2            || LocalDate.of(2025, 6, 12)
        LocalDate.of(1974, 2, 25)  | LocalDate.of(1974, 5, 16)  | null                       | true     | 1597.40               | 0          | false          || 1265.00   || 1265.00    || 2            || LocalDate.of(1974, 6, 16)
        LocalDate.of(2029, 3, 24)  | LocalDate.of(2029, 10, 6)  | null                       | true     | 1497.25               | 11         | false          || 19855.00  || 1265.00    || 2            || LocalDate.of(2029, 11, 6)
        LocalDate.of(1991, 9, 13)  | LocalDate.of(1992, 8, 22)  | LocalDate.of(1991, 3, 5)   | false    | 737.19                | 11         | false          || 16252.81  || 0.00       || 2            || LocalDate.of(1992, 9, 22)
        LocalDate.of(2009, 10, 22) | LocalDate.of(2010, 10, 9)  | null                       | true     | 1081.01               | 8          | false          || 14968.99  || 0.00       || 2            || LocalDate.of(2010, 11, 9)
        LocalDate.of(2046, 5, 31)  | LocalDate.of(2046, 8, 27)  | LocalDate.of(2045, 6, 30)  | false    | 1682.58               | 7          | false          || 10285.00  || 1265.00    || 2            || LocalDate.of(2046, 9, 27)
        LocalDate.of(2034, 1, 28)  | LocalDate.of(2034, 5, 24)  | LocalDate.of(2033, 7, 2)   | true     | 1650.83               | 2          | false          || 4645.00   || 1265.00    || 2            || LocalDate.of(2034, 6, 24)
        LocalDate.of(2015, 6, 29)  | LocalDate.of(2015, 12, 15) | LocalDate.of(2014, 11, 24) | false    | 998.49                | 6          | false          || 9191.51   || 0.00       || 2            || LocalDate.of(2016, 1, 15)
        LocalDate.of(2010, 3, 9)   | LocalDate.of(2010, 9, 12)  | LocalDate.of(2009, 4, 23)  | true     | 846.33                | 14         | false          || 25343.67  || 0.00       || 2            || LocalDate.of(2010, 10, 12)
        LocalDate.of(1985, 12, 21) | LocalDate.of(1986, 12, 22) | LocalDate.of(1985, 10, 20) | false    | 1247.27               | 7          | false          || 10302.73  || 0.00       || 2            || LocalDate.of(1987, 1, 22)
        LocalDate.of(1987, 4, 28)  | LocalDate.of(1987, 8, 9)   | null                       | false    | 1330.89               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(1987, 9, 9)
    }

    // All variants

    def "Tier 4 Post Grad Doctor or Dentist - Check 'All variants'"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, dependantsOnly)
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
        LocalDate.of(2024, 11, 29) | LocalDate.of(2025, 6, 22)  | LocalDate.of(2024, 5, 29)  | false    | 0.00                  | 5          | true           || 6800.00   || 0.00       || 2            || LocalDate.of(2025, 7, 22)
        LocalDate.of(2021, 2, 1)   | LocalDate.of(2021, 6, 16)  | null                       | false    | 254.82                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2021, 7, 16)
        LocalDate.of(2025, 11, 27) | LocalDate.of(2026, 5, 27)  | null                       | true     | 1418.40               | 3          | true           || 5070.00   || 1265.00    || 2            || LocalDate.of(2026, 6, 27)
        LocalDate.of(2051, 4, 6)   | LocalDate.of(2051, 5, 9)   | null                       | false    | 253.49                | 0          | false          || 1776.51   || 0.00       || 0            || LocalDate.of(2051, 6, 9)
        LocalDate.of(2000, 10, 16) | LocalDate.of(2001, 11, 8)  | LocalDate.of(2000, 2, 29)  | true     | 625.07                | 13         | true           || 21970.00  || 0.00       || 2            || LocalDate.of(2001, 12, 8)
        LocalDate.of(2033, 8, 11)  | LocalDate.of(2034, 3, 23)  | null                       | false    | 1409.79               | 13         | true           || 17680.00  || 1265.00    || 2            || LocalDate.of(2034, 4, 23)
        LocalDate.of(2009, 6, 8)   | LocalDate.of(2010, 7, 10)  | LocalDate.of(2008, 9, 27)  | false    | 0.00                  | 2          | true           || 2720.00   || 0.00       || 2            || LocalDate.of(2010, 8, 10)
        LocalDate.of(2041, 7, 18)  | LocalDate.of(2041, 12, 8)  | null                       | true     | 980.38                | 0          | false          || 1549.62   || 0.00       || 2            || LocalDate.of(2042, 1, 8)
        LocalDate.of(2049, 8, 25)  | LocalDate.of(2049, 10, 24) | LocalDate.of(2049, 8, 20)  | false    | 34.40                 | 12         | false          || 18315.60  || 0.00       || 0            || LocalDate.of(2049, 11, 24)
        LocalDate.of(2008, 6, 27)  | LocalDate.of(2008, 8, 6)   | LocalDate.of(2007, 8, 28)  | true     | 0.00                  | 11         | true           || 18590.00  || 0.00       || 0            || LocalDate.of(2008, 9, 6)
        LocalDate.of(2004, 10, 9)  | LocalDate.of(2004, 11, 14) | null                       | true     | 916.43                | 0          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2004, 12, 14)
        LocalDate.of(1982, 11, 24) | LocalDate.of(1983, 6, 26)  | null                       | false    | 1678.10               | 6          | true           || 8160.00   || 1265.00    || 2            || LocalDate.of(1983, 7, 26)
        LocalDate.of(2000, 7, 18)  | LocalDate.of(2001, 5, 16)  | null                       | false    | 1295.05               | 8          | false          || 11645.00  || 1265.00    || 2            || LocalDate.of(2001, 6, 16)
        LocalDate.of(2048, 5, 9)   | LocalDate.of(2049, 2, 25)  | LocalDate.of(2047, 8, 22)  | true     | 1716.02               | 11         | false          || 19855.00  || 1265.00    || 2            || LocalDate.of(2049, 3, 25)
        LocalDate.of(2023, 12, 13) | LocalDate.of(2024, 10, 3)  | LocalDate.of(2023, 1, 27)  | false    | 0.00                  | 11         | true           || 14960.00  || 0.00       || 2            || LocalDate.of(2024, 11, 3)
        LocalDate.of(1982, 3, 5)   | LocalDate.of(1982, 6, 13)  | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1982, 7, 13)
        LocalDate.of(2038, 11, 27) | LocalDate.of(2038, 12, 19) | null                       | true     | 0.00                  | 0          | false          || 1265.00   || 0.00       || 0            || LocalDate.of(2039, 1, 19)
        LocalDate.of(1981, 1, 15)  | LocalDate.of(1982, 2, 15)  | LocalDate.of(1980, 5, 31)  | false    | 1287.42               | 10         | true           || 13600.00  || 1265.00    || 2            || LocalDate.of(1982, 3, 15)
        LocalDate.of(2023, 4, 22)  | LocalDate.of(2023, 12, 13) | null                       | true     | 1669.28               | 13         | false          || 23235.00  || 1265.00    || 2            || LocalDate.of(2024, 1, 13)
        LocalDate.of(2000, 6, 4)   | LocalDate.of(2000, 8, 22)  | LocalDate.of(2000, 4, 21)  | false    | 0.00                  | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(2000, 9, 22)
        LocalDate.of(2037, 10, 20) | LocalDate.of(2038, 5, 18)  | null                       | false    | 1706.76               | 4          | false          || 6205.00   || 1265.00    || 2            || LocalDate.of(2038, 6, 18)
        LocalDate.of(2023, 11, 13) | LocalDate.of(2024, 5, 23)  | null                       | true     | 0.00                  | 11         | true           || 18590.00  || 0.00       || 2            || LocalDate.of(2024, 6, 23)
        LocalDate.of(2040, 10, 10) | LocalDate.of(2041, 7, 4)   | LocalDate.of(2040, 2, 18)  | true     | 0.00                  | 1          | false          || 4220.00   || 0.00       || 2            || LocalDate.of(2041, 8, 4)
        LocalDate.of(1988, 8, 7)   | LocalDate.of(1988, 8, 31)  | LocalDate.of(1987, 7, 5)   | true     | 0.00                  | 8          | false          || 14785.00  || 0.00       || 0            || LocalDate.of(1988, 9, 30)
        LocalDate.of(2031, 9, 2)   | LocalDate.of(2032, 7, 14)  | LocalDate.of(2030, 9, 15)  | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2032, 8, 14)
        LocalDate.of(2043, 2, 17)  | LocalDate.of(2043, 4, 5)   | LocalDate.of(2042, 12, 3)  | false    | 971.07                | 13         | true           || 17680.00  || 0.00       || 0            || LocalDate.of(2043, 5, 5)
        LocalDate.of(2004, 9, 1)   | LocalDate.of(2005, 6, 30)  | null                       | false    | 0.00                  | 7          | false          || 11550.00  || 0.00       || 2            || LocalDate.of(2005, 7, 30)
        LocalDate.of(2003, 6, 7)   | LocalDate.of(2004, 5, 30)  | null                       | false    | 923.78                | 1          | false          || 2466.22   || 0.00       || 2            || LocalDate.of(2004, 6, 30)
        LocalDate.of(1982, 1, 23)  | LocalDate.of(1982, 1, 26)  | LocalDate.of(1981, 8, 6)   | false    | 0.00                  | 4          | false          || 6455.00   || 0.00       || 0            || LocalDate.of(1982, 2, 26)
        LocalDate.of(1973, 9, 16)  | LocalDate.of(1973, 12, 1)  | null                       | true     | 754.29                | 0          | false          || 1775.71   || 0.00       || 2            || LocalDate.of(1974, 1, 1)
        LocalDate.of(2008, 7, 10)  | LocalDate.of(2009, 2, 3)   | LocalDate.of(2007, 12, 5)  | false    | 1393.46               | 1          | false          || 2125.00   || 1265.00    || 2            || LocalDate.of(2009, 3, 3)
        LocalDate.of(1981, 2, 1)   | LocalDate.of(1981, 11, 11) | LocalDate.of(1980, 4, 12)  | true     | 964.86                | 8          | true           || 13520.00  || 0.00       || 2            || LocalDate.of(1981, 12, 11)
        LocalDate.of(2038, 11, 2)  | LocalDate.of(2039, 7, 21)  | null                       | false    | 1837.88               | 10         | true           || 13600.00  || 1265.00    || 2            || LocalDate.of(2039, 8, 21)
        LocalDate.of(2000, 4, 29)  | LocalDate.of(2001, 3, 3)   | LocalDate.of(1999, 11, 18) | true     | 848.37                | 1          | true           || 1690.00   || 0.00       || 2            || LocalDate.of(2001, 4, 3)
        LocalDate.of(2035, 5, 22)  | LocalDate.of(2035, 12, 13) | null                       | true     | 372.44                | 14         | true           || 23660.00  || 0.00       || 2            || LocalDate.of(2036, 1, 13)
        LocalDate.of(1976, 3, 5)   | LocalDate.of(1976, 8, 7)   | null                       | false    | 1451.30               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(1976, 9, 7)
        LocalDate.of(2012, 5, 19)  | LocalDate.of(2013, 3, 10)  | null                       | false    | 0.00                  | 14         | false          || 21070.00  || 0.00       || 2            || LocalDate.of(2013, 4, 10)
        LocalDate.of(1982, 12, 26) | LocalDate.of(1983, 4, 8)   | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1983, 5, 8)
        LocalDate.of(2019, 9, 1)   | LocalDate.of(2020, 4, 30)  | null                       | true     | 887.53                | 5          | false          || 10092.47  || 0.00       || 2            || LocalDate.of(2020, 5, 30)
        LocalDate.of(2004, 12, 7)  | LocalDate.of(2005, 2, 27)  | null                       | false    | 1162.88               | 0          | false          || 867.12    || 0.00       || 2            || LocalDate.of(2005, 3, 27)
        LocalDate.of(1997, 2, 11)  | LocalDate.of(1997, 6, 11)  | null                       | false    | 1982.89               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(1997, 7, 11)
        LocalDate.of(2022, 2, 4)   | LocalDate.of(2022, 4, 13)  | LocalDate.of(2021, 10, 22) | true     | 0.00                  | 11         | true           || 18590.00  || 0.00       || 2            || LocalDate.of(2022, 5, 13)
        LocalDate.of(2051, 10, 31) | LocalDate.of(2051, 12, 3)  | LocalDate.of(2051, 9, 27)  | true     | 0.00                  | 5          | true           || 8450.00   || 0.00       || 0            || LocalDate.of(2052, 1, 3)
        LocalDate.of(1975, 9, 12)  | LocalDate.of(1975, 11, 17) | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1975, 12, 17)
        LocalDate.of(1991, 12, 20) | LocalDate.of(1992, 2, 23)  | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(1992, 3, 23)
        LocalDate.of(1974, 2, 3)   | LocalDate.of(1974, 7, 29)  | LocalDate.of(1973, 2, 2)   | true     | 1735.06               | 4          | true           || 6760.00   || 1265.00    || 2            || LocalDate.of(1974, 8, 29)
        LocalDate.of(2052, 1, 9)   | LocalDate.of(2052, 2, 3)   | LocalDate.of(2051, 3, 12)  | true     | 1579.36               | 6          | false          || 10140.00  || 1265.00    || 0            || LocalDate.of(2052, 3, 3)
        LocalDate.of(2038, 3, 20)  | LocalDate.of(2038, 10, 21) | null                       | true     | 0.00                  | 1          | true           || 1690.00   || 0.00       || 2            || LocalDate.of(2038, 11, 21)
        LocalDate.of(2006, 5, 12)  | LocalDate.of(2006, 6, 15)  | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2006, 7, 15)
        LocalDate.of(2002, 4, 6)   | LocalDate.of(2003, 2, 21)  | LocalDate.of(2001, 5, 12)  | false    | 201.59                | 12         | false          || 18148.41  || 0.00       || 2            || LocalDate.of(2003, 3, 21)
        LocalDate.of(2002, 3, 4)   | LocalDate.of(2002, 11, 8)  | LocalDate.of(2001, 6, 28)  | false    | 0.00                  | 7          | true           || 9520.00   || 0.00       || 2            || LocalDate.of(2002, 12, 8)
        LocalDate.of(1998, 11, 15) | LocalDate.of(1998, 11, 16) | LocalDate.of(1998, 5, 12)  | true     | 620.05                | 7          | false          || 12474.95  || 0.00       || 0            || LocalDate.of(1998, 12, 16)
        LocalDate.of(2037, 7, 11)  | LocalDate.of(2037, 12, 23) | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2038, 1, 23)
        LocalDate.of(1995, 7, 6)   | LocalDate.of(1995, 12, 16) | LocalDate.of(1994, 7, 11)  | false    | 504.77                | 6          | true           || 8160.00   || 0.00       || 2            || LocalDate.of(1996, 1, 16)
        LocalDate.of(2039, 6, 21)  | LocalDate.of(2040, 3, 27)  | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2040, 4, 27)
        LocalDate.of(1986, 7, 14)  | LocalDate.of(1987, 8, 2)   | LocalDate.of(1985, 7, 23)  | true     | 0.00                  | 1          | false          || 4220.00   || 0.00       || 2            || LocalDate.of(1987, 9, 2)
        LocalDate.of(2014, 5, 20)  | LocalDate.of(2015, 2, 23)  | LocalDate.of(2014, 1, 13)  | false    | 864.53                | 14         | false          || 20205.47  || 0.00       || 2            || LocalDate.of(2015, 3, 23)
        LocalDate.of(1990, 7, 18)  | LocalDate.of(1990, 8, 23)  | null                       | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 0            || LocalDate.of(1990, 9, 23)
        LocalDate.of(2009, 12, 2)  | LocalDate.of(2010, 7, 26)  | LocalDate.of(2009, 5, 13)  | false    | 1788.34               | 6          | false          || 8925.00   || 1265.00    || 2            || LocalDate.of(2010, 8, 26)
        LocalDate.of(1991, 8, 30)  | LocalDate.of(1992, 5, 28)  | LocalDate.of(1991, 5, 1)   | false    | 0.00                  | 13         | true           || 17680.00  || 0.00       || 2            || LocalDate.of(1992, 6, 28)
        LocalDate.of(1987, 10, 8)  | LocalDate.of(1987, 11, 12) | null                       | false    | 1493.82               | 0          | false          || 765.00    || 1265.00    || 0            || LocalDate.of(1987, 12, 12)
        LocalDate.of(2051, 2, 18)  | LocalDate.of(2051, 12, 5)  | LocalDate.of(2050, 10, 28) | true     | 422.00                | 10         | true           || 16900.00  || 0.00       || 2            || LocalDate.of(2052, 1, 5)
        LocalDate.of(1978, 2, 2)   | LocalDate.of(1978, 10, 31) | null                       | false    | 417.45                | 4          | false          || 7052.55   || 0.00       || 2            || LocalDate.of(1978, 11, 30)
        LocalDate.of(2028, 9, 1)   | LocalDate.of(2028, 10, 12) | LocalDate.of(2028, 2, 25)  | true     | 0.00                  | 11         | true           || 18590.00  || 0.00       || 0            || LocalDate.of(2028, 11, 12)
        LocalDate.of(2043, 3, 18)  | LocalDate.of(2044, 3, 3)   | LocalDate.of(2042, 11, 13) | true     | 0.00                  | 7          | true           || 11830.00  || 0.00       || 2            || LocalDate.of(2044, 4, 3)
        LocalDate.of(2024, 1, 18)  | LocalDate.of(2024, 7, 8)   | LocalDate.of(2022, 12, 21) | true     | 0.00                  | 10         | true           || 16900.00  || 0.00       || 2            || LocalDate.of(2024, 8, 8)
        LocalDate.of(2024, 10, 5)  | LocalDate.of(2024, 10, 13) | LocalDate.of(2024, 9, 28)  | false    | 62.51                 | 7          | true           || 9520.00   || 0.00       || 0            || LocalDate.of(2024, 11, 13)
        LocalDate.of(2003, 4, 17)  | LocalDate.of(2003, 9, 10)  | LocalDate.of(2002, 12, 12) | false    | 0.00                  | 6          | true           || 8160.00   || 0.00       || 2            || LocalDate.of(2003, 10, 10)
        LocalDate.of(1996, 7, 9)   | LocalDate.of(1997, 3, 1)   | LocalDate.of(1995, 8, 17)  | false    | 0.00                  | 6          | true           || 8160.00   || 0.00       || 2            || LocalDate.of(1997, 4, 1)
        LocalDate.of(1986, 9, 6)   | LocalDate.of(1987, 1, 22)  | null                       | true     | 438.50                | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1987, 2, 22)
        LocalDate.of(2030, 3, 2)   | LocalDate.of(2030, 11, 28) | null                       | true     | 0.00                  | 14         | false          || 26190.00  || 0.00       || 2            || LocalDate.of(2030, 12, 28)
        LocalDate.of(2029, 5, 22)  | LocalDate.of(2029, 10, 14) | LocalDate.of(2028, 7, 19)  | false    | 789.04                | 0          | false          || 1240.96   || 0.00       || 2            || LocalDate.of(2029, 11, 14)
        LocalDate.of(1972, 11, 30) | LocalDate.of(1972, 12, 11) | LocalDate.of(1972, 3, 4)   | true     | 1946.78               | 12         | false          || 20280.00  || 1265.00    || 0            || LocalDate.of(1973, 1, 11)
        LocalDate.of(1996, 2, 13)  | LocalDate.of(1996, 7, 14)  | LocalDate.of(1995, 5, 18)  | false    | 516.97                | 0          | false          || 1513.03   || 0.00       || 2            || LocalDate.of(1996, 8, 14)
        LocalDate.of(1973, 12, 4)  | LocalDate.of(1974, 2, 12)  | LocalDate.of(1973, 11, 15) | false    | 935.92                | 7          | true           || 9520.00   || 0.00       || 2            || LocalDate.of(1974, 3, 12)
        LocalDate.of(2007, 3, 21)  | LocalDate.of(2007, 3, 27)  | LocalDate.of(2006, 10, 17) | true     | 0.00                  | 4          | false          || 8025.00   || 0.00       || 0            || LocalDate.of(2007, 4, 27)
        LocalDate.of(1977, 7, 20)  | LocalDate.of(1977, 10, 23) | LocalDate.of(1977, 5, 4)   | false    | 0.00                  | 7          | false          || 11550.00  || 0.00       || 2            || LocalDate.of(1977, 11, 23)
        LocalDate.of(1992, 8, 28)  | LocalDate.of(1993, 3, 13)  | LocalDate.of(1992, 7, 30)  | true     | 509.27                | 8          | true           || 13520.00  || 0.00       || 2            || LocalDate.of(1993, 4, 13)
        LocalDate.of(1994, 9, 13)  | LocalDate.of(1994, 9, 25)  | LocalDate.of(1994, 2, 9)   | false    | 1682.69               | 10         | true           || 13600.00  || 1265.00    || 0            || LocalDate.of(1994, 10, 25)
        LocalDate.of(1984, 1, 4)   | LocalDate.of(1984, 6, 24)  | LocalDate.of(1983, 10, 2)  | false    | 0.00                  | 7          | false          || 11550.00  || 0.00       || 2            || LocalDate.of(1984, 7, 24)
        LocalDate.of(1980, 6, 19)  | LocalDate.of(1981, 1, 30)  | LocalDate.of(1980, 5, 16)  | true     | 1920.38               | 10         | false          || 18165.00  || 1265.00    || 2            || LocalDate.of(1981, 2, 28)
        LocalDate.of(2004, 8, 13)  | LocalDate.of(2005, 5, 19)  | null                       | true     | 343.32                | 14         | true           || 23660.00  || 0.00       || 2            || LocalDate.of(2005, 6, 19)
        LocalDate.of(1992, 3, 27)  | LocalDate.of(1992, 8, 22)  | LocalDate.of(1991, 3, 17)  | false    | 0.00                  | 5          | false          || 8830.00   || 0.00       || 2            || LocalDate.of(1992, 9, 22)
        LocalDate.of(2016, 10, 3)  | LocalDate.of(2016, 12, 16) | LocalDate.of(2015, 9, 27)  | false    | 0.00                  | 2          | false          || 4750.00   || 0.00       || 2            || LocalDate.of(2017, 1, 16)
        LocalDate.of(2018, 10, 8)  | LocalDate.of(2019, 5, 13)  | null                       | true     | 1778.22               | 10         | false          || 18165.00  || 1265.00    || 2            || LocalDate.of(2019, 6, 13)
        LocalDate.of(2019, 8, 23)  | LocalDate.of(2020, 5, 12)  | LocalDate.of(2018, 11, 22) | true     | 0.00                  | 1          | true           || 1690.00   || 0.00       || 2            || LocalDate.of(2020, 6, 12)
        LocalDate.of(1999, 5, 11)  | LocalDate.of(2000, 2, 11)  | LocalDate.of(1999, 4, 5)   | true     | 0.00                  | 13         | false          || 24500.00  || 0.00       || 2            || LocalDate.of(2000, 3, 11)
        LocalDate.of(1988, 10, 28) | LocalDate.of(1989, 2, 25)  | null                       | false    | 1697.53               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(1989, 3, 25)
        LocalDate.of(2041, 2, 5)   | LocalDate.of(2041, 2, 15)  | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2041, 3, 15)
        LocalDate.of(2053, 10, 2)  | LocalDate.of(2053, 12, 21) | null                       | true     | 1834.74               | 0          | false          || 1265.00   || 1265.00    || 2            || LocalDate.of(2054, 1, 21)
        LocalDate.of(2005, 9, 4)   | LocalDate.of(2006, 7, 12)  | LocalDate.of(2005, 1, 14)  | false    | 980.89                | 12         | true           || 16320.00  || 0.00       || 2            || LocalDate.of(2006, 8, 12)
        LocalDate.of(2011, 6, 8)   | LocalDate.of(2011, 8, 20)  | LocalDate.of(2011, 4, 27)  | true     | 0.00                  | 7          | true           || 11830.00  || 0.00       || 2            || LocalDate.of(2011, 9, 20)
        LocalDate.of(2026, 7, 19)  | LocalDate.of(2027, 3, 7)   | null                       | true     | 0.00                  | 7          | false          || 14360.00  || 0.00       || 2            || LocalDate.of(2027, 4, 7)
        LocalDate.of(2009, 4, 2)   | LocalDate.of(2010, 4, 12)  | LocalDate.of(2008, 11, 5)  | true     | 0.00                  | 14         | true           || 23660.00  || 0.00       || 2            || LocalDate.of(2010, 5, 12)
        LocalDate.of(1997, 11, 19) | LocalDate.of(1998, 9, 11)  | LocalDate.of(1997, 3, 10)  | false    | 0.00                  | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(1998, 10, 11)
        LocalDate.of(2002, 2, 20)  | LocalDate.of(2002, 9, 18)  | LocalDate.of(2001, 10, 31) | true     | 539.83                | 1          | true           || 1690.00   || 0.00       || 2            || LocalDate.of(2002, 10, 18)
        LocalDate.of(2039, 10, 26) | LocalDate.of(2040, 7, 25)  | null                       | false    | 50.64                 | 6          | false          || 10139.36  || 0.00       || 2            || LocalDate.of(2040, 8, 25)
        LocalDate.of(1994, 11, 9)  | LocalDate.of(1995, 10, 20) | LocalDate.of(1994, 10, 15) | true     | 1830.59               | 7          | false          || 13095.00  || 1265.00    || 2            || LocalDate.of(1995, 11, 20)
        LocalDate.of(2036, 4, 4)   | LocalDate.of(2036, 10, 1)  | null                       | false    | 1878.50               | 0          | false          || 765.00    || 1265.00    || 2            || LocalDate.of(2036, 11, 1)
        LocalDate.of(2002, 11, 6)  | LocalDate.of(2003, 1, 5)   | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 0            || LocalDate.of(2003, 2, 5)
        LocalDate.of(2030, 8, 3)   | LocalDate.of(2031, 5, 8)   | null                       | false    | 647.07                | 14         | true           || 19040.00  || 0.00       || 2            || LocalDate.of(2031, 6, 8)
        LocalDate.of(2013, 5, 2)   | LocalDate.of(2013, 11, 30) | LocalDate.of(2012, 12, 12) | true     | 1560.71               | 3          | false          || 6335.00   || 1265.00    || 2            || LocalDate.of(2013, 12, 30)
        LocalDate.of(1973, 8, 12)  | LocalDate.of(1973, 8, 13)  | LocalDate.of(1972, 9, 29)  | false    | 0.00                  | 9          | false          || 13255.00  || 0.00       || 0            || LocalDate.of(1973, 9, 13)
        LocalDate.of(2017, 10, 20) | LocalDate.of(2018, 4, 9)   | null                       | true     | 115.06                | 0          | false          || 2414.94   || 0.00       || 2            || LocalDate.of(2018, 5, 9)
        LocalDate.of(1996, 9, 25)  | LocalDate.of(1996, 10, 7)  | null                       | false    | 199.22                | 0          | false          || 815.78    || 0.00       || 0            || LocalDate.of(1996, 11, 7)
        LocalDate.of(2010, 8, 8)   | LocalDate.of(2011, 7, 25)  | null                       | true     | 654.32                | 11         | true           || 18590.00  || 0.00       || 2            || LocalDate.of(2011, 8, 25)
        LocalDate.of(2026, 1, 20)  | LocalDate.of(2026, 9, 25)  | LocalDate.of(2025, 6, 11)  | true     | 0.00                  | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(2026, 10, 25)
        LocalDate.of(2030, 3, 18)  | LocalDate.of(2031, 2, 27)  | LocalDate.of(2029, 7, 15)  | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2031, 3, 27)
        LocalDate.of(2036, 3, 16)  | LocalDate.of(2037, 4, 8)   | LocalDate.of(2035, 8, 6)   | false    | 0.00                  | 5          | true           || 6800.00   || 0.00       || 2            || LocalDate.of(2037, 5, 8)
        LocalDate.of(2054, 6, 1)   | LocalDate.of(2054, 8, 11)  | null                       | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2054, 9, 11)
        LocalDate.of(2021, 7, 3)   | LocalDate.of(2022, 3, 10)  | LocalDate.of(2021, 4, 27)  | false    | 1819.70               | 5          | true           || 6800.00   || 1265.00    || 2            || LocalDate.of(2022, 4, 10)
        LocalDate.of(2014, 2, 23)  | LocalDate.of(2014, 5, 24)  | LocalDate.of(2013, 2, 13)  | true     | 1741.69               | 14         | true           || 23660.00  || 1265.00    || 2            || LocalDate.of(2014, 6, 24)
        LocalDate.of(2003, 10, 1)  | LocalDate.of(2004, 7, 4)   | LocalDate.of(2003, 1, 9)   | false    | 1301.36               | 4          | true           || 5440.00   || 1265.00    || 2            || LocalDate.of(2004, 8, 4)
        LocalDate.of(2035, 9, 7)   | LocalDate.of(2036, 8, 16)  | LocalDate.of(2035, 9, 4)   | true     | 330.70                | 3          | true           || 5070.00   || 0.00       || 2            || LocalDate.of(2036, 9, 16)
        LocalDate.of(1993, 10, 13) | LocalDate.of(1994, 5, 11)  | LocalDate.of(1993, 8, 26)  | true     | 911.75                | 2          | false          || 4998.25   || 0.00       || 2            || LocalDate.of(1994, 6, 11)
        LocalDate.of(1990, 7, 15)  | LocalDate.of(1991, 7, 12)  | null                       | true     | 0.00                  | 6          | true           || 10140.00  || 0.00       || 2            || LocalDate.of(1991, 8, 12)
        LocalDate.of(1998, 9, 9)   | LocalDate.of(1999, 6, 10)  | LocalDate.of(1998, 5, 3)   | true     | 433.97                | 7          | true           || 11830.00  || 0.00       || 2            || LocalDate.of(1999, 7, 10)
        LocalDate.of(2039, 1, 2)   | LocalDate.of(2039, 9, 19)  | LocalDate.of(2038, 6, 20)  | true     | 0.00                  | 3          | true           || 5070.00   || 0.00       || 2            || LocalDate.of(2039, 10, 19)
        LocalDate.of(1994, 5, 19)  | LocalDate.of(1995, 2, 21)  | LocalDate.of(1993, 10, 16) | true     | 461.03                | 6          | false          || 12208.97  || 0.00       || 2            || LocalDate.of(1995, 3, 21)
        LocalDate.of(2051, 10, 9)  | LocalDate.of(2051, 10, 21) | LocalDate.of(2050, 12, 24) | true     | 0.00                  | 4          | false          || 8025.00   || 0.00       || 0            || LocalDate.of(2051, 11, 21)
        LocalDate.of(2048, 6, 30)  | LocalDate.of(2048, 7, 13)  | null                       | false    | 0.00                  | 0          | false          || 1015.00   || 0.00       || 0            || LocalDate.of(2048, 8, 13)
        LocalDate.of(1975, 4, 20)  | LocalDate.of(1975, 5, 7)   | LocalDate.of(1974, 4, 16)  | true     | 1950.60               | 13         | true           || 21970.00  || 1265.00    || 0            || LocalDate.of(1975, 6, 7)
        LocalDate.of(1988, 8, 1)   | LocalDate.of(1989, 1, 21)  | LocalDate.of(1988, 5, 2)   | true     | 1778.86               | 4          | false          || 8025.00   || 1265.00    || 2            || LocalDate.of(1989, 2, 21)
        LocalDate.of(1989, 7, 23)  | LocalDate.of(1990, 4, 10)  | null                       | false    | 1588.83               | 0          | true           || 0.00      || 1265.00    || 2            || LocalDate.of(1990, 5, 10)
        LocalDate.of(2036, 7, 31)  | LocalDate.of(2037, 8, 10)  | null                       | false    | 0.00                  | 12         | false          || 18350.00  || 0.00       || 2            || LocalDate.of(2037, 9, 10)
        LocalDate.of(2006, 3, 14)  | LocalDate.of(2006, 12, 16) | LocalDate.of(2005, 5, 26)  | false    | 453.60                | 10         | true           || 13600.00  || 0.00       || 2            || LocalDate.of(2007, 1, 16)
        LocalDate.of(1982, 3, 27)  | LocalDate.of(1982, 6, 25)  | LocalDate.of(1982, 3, 23)  | false    | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(1982, 7, 25)
        LocalDate.of(1989, 5, 21)  | LocalDate.of(1989, 5, 27)  | LocalDate.of(1988, 9, 14)  | true     | 1237.12               | 10         | true           || 16900.00  || 0.00       || 0            || LocalDate.of(1989, 6, 27)
        LocalDate.of(1986, 8, 11)  | LocalDate.of(1987, 4, 29)  | LocalDate.of(1985, 10, 1)  | false    | 1320.29               | 14         | false          || 19805.00  || 1265.00    || 2            || LocalDate.of(1987, 5, 29)
        LocalDate.of(1986, 1, 31)  | LocalDate.of(1986, 6, 3)   | LocalDate.of(1985, 4, 29)  | true     | 0.00                  | 4          | true           || 6760.00   || 0.00       || 2            || LocalDate.of(1986, 7, 3)
        LocalDate.of(2031, 9, 16)  | LocalDate.of(2032, 9, 30)  | LocalDate.of(2031, 7, 24)  | false    | 0.00                  | 14         | true           || 19040.00  || 0.00       || 2            || LocalDate.of(2032, 10, 30)
        LocalDate.of(1975, 9, 25)  | LocalDate.of(1976, 2, 24)  | LocalDate.of(1974, 10, 10) | true     | 0.00                  | 4          | false          || 9290.00   || 0.00       || 2            || LocalDate.of(1976, 3, 24)
        LocalDate.of(2009, 3, 27)  | LocalDate.of(2009, 6, 5)   | LocalDate.of(2008, 5, 8)   | true     | 0.00                  | 11         | false          || 21120.00  || 0.00       || 2            || LocalDate.of(2009, 7, 5)
        LocalDate.of(2001, 2, 13)  | LocalDate.of(2001, 5, 21)  | null                       | true     | 0.00                  | 0          | true           || 0.00      || 0.00       || 2            || LocalDate.of(2001, 6, 21)
        LocalDate.of(2021, 5, 24)  | LocalDate.of(2021, 11, 17) | LocalDate.of(2020, 11, 19) | false    | 0.00                  | 1          | true           || 1360.00   || 0.00       || 2            || LocalDate.of(2021, 12, 17)
        LocalDate.of(1997, 9, 30)  | LocalDate.of(1998, 2, 24)  | LocalDate.of(1997, 4, 27)  | true     | 0.00                  | 13         | true           || 21970.00  || 0.00       || 2            || LocalDate.of(1998, 3, 24)
        LocalDate.of(1997, 11, 8)  | LocalDate.of(1998, 11, 21) | LocalDate.of(1996, 12, 28) | false    | 0.00                  | 1          | false          || 3390.00   || 0.00       || 2            || LocalDate.of(1998, 12, 21)
        LocalDate.of(2014, 9, 24)  | LocalDate.of(2014, 10, 25) | LocalDate.of(2014, 2, 12)  | false    | 207.21                | 3          | false          || 5902.79   || 0.00       || 0            || LocalDate.of(2014, 11, 25)
        LocalDate.of(1985, 7, 13)  | LocalDate.of(1985, 7, 28)  | LocalDate.of(1985, 4, 15)  | false    | 0.00                  | 7          | true           || 9520.00   || 0.00       || 0            || LocalDate.of(1985, 8, 28)
        LocalDate.of(2040, 1, 15)  | LocalDate.of(2040, 8, 2)   | LocalDate.of(2039, 12, 4)  | true     | 0.00                  | 0          | false          || 2530.00   || 0.00       || 2            || LocalDate.of(2040, 9, 2)
        LocalDate.of(2049, 2, 19)  | LocalDate.of(2049, 10, 24) | null                       | false    | 0.00                  | 6          | false          || 10190.00  || 0.00       || 2            || LocalDate.of(2049, 11, 24)
        LocalDate.of(1998, 6, 19)  | LocalDate.of(1998, 9, 3)   | LocalDate.of(1997, 9, 1)   | false    | 0.00                  | 11         | false          || 16990.00  || 0.00       || 2            || LocalDate.of(1998, 10, 3)
        LocalDate.of(2048, 12, 12) | LocalDate.of(2049, 7, 23)  | LocalDate.of(2048, 1, 6)   | false    | 1167.36               | 12         | false          || 17182.64  || 0.00       || 2            || LocalDate.of(2049, 8, 23)
        LocalDate.of(2034, 1, 9)   | LocalDate.of(2034, 6, 27)  | null                       | false    | 0.00                  | 0          | false          || 2030.00   || 0.00       || 2            || LocalDate.of(2034, 7, 27)
        LocalDate.of(2017, 4, 12)  | LocalDate.of(2018, 1, 17)  | LocalDate.of(2016, 6, 4)   | false    | 1726.71               | 2          | true           || 2720.00   || 1265.00    || 2            || LocalDate.of(2018, 2, 17)
        LocalDate.of(2006, 10, 27) | LocalDate.of(2007, 8, 29)  | LocalDate.of(2006, 8, 17)  | true     | 0.00                  | 14         | true           || 23660.00  || 0.00       || 2            || LocalDate.of(2007, 9, 29)
        LocalDate.of(1985, 9, 15)  | LocalDate.of(1986, 9, 1)   | LocalDate.of(1985, 3, 14)  | true     | 0.00                  | 14         | false          || 26190.00  || 0.00       || 2            || LocalDate.of(1986, 10, 1)
        LocalDate.of(2004, 10, 19) | LocalDate.of(2005, 4, 25)  | null                       | true     | 0.00                  | 3          | false          || 7600.00   || 0.00       || 2            || LocalDate.of(2005, 5, 25)
        LocalDate.of(2007, 3, 21)  | LocalDate.of(2007, 10, 1)  | LocalDate.of(2006, 7, 16)  | true     | 564.04                | 9          | true           || 15210.00  || 0.00       || 2            || LocalDate.of(2007, 11, 1)
        LocalDate.of(1995, 12, 1)  | LocalDate.of(1996, 7, 3)   | null                       | false    | 0.00                  | 7          | false          || 11550.00  || 0.00       || 2            || LocalDate.of(1996, 8, 3)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        courseStartDate           | courseEndDate            | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 2, 26) | LocalDate.of(2003, 6, 5) | LocalDate.of(2002, 5, 23)  | true     | -1                    | 11         || 20541.70  || 0.00       || 2            || LocalDate.of(2003, 10, 5)
        LocalDate.of(2001, 7, 9)  | LocalDate.of(2001, 9, 5) | LocalDate.of(2000, 12, 24) | false    | -7                    | 13         || 18685.25  || 0.00       || 0            || LocalDate.of(2001, 11, 5)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid characters accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        courseStartDate           | courseEndDate            | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 2, 26) | LocalDate.of(2003, 6, 5) | LocalDate.of(2002, 5, 23)  | true     | "^&"                  | 11         || 20541.70  || 0.00       || 2            || LocalDate.of(2003, 10, 5)
        LocalDate.of(2001, 7, 9)  | LocalDate.of(2001, 9, 5) | LocalDate.of(2000, 12, 24) | false    | ")()"                 | 13         || 18685.25  || 0.00       || 0            || LocalDate.of(2001, 11, 5)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check rounding accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isOk())

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 10, 17) | LocalDate.of(2004, 1, 11)  | LocalDate.of(2003, 5, 18) | true     | 456.8412              | 9          || 17283.16  || 0.00       || 2            || LocalDate.of(2004, 2, 11)
        LocalDate.of(2038, 8, 7)   | LocalDate.of(2039, 8, 26)  | LocalDate.of(2038, 5, 10) | true     | 1703.9934             | 2          || 4645.00   || 1265.00    || 2            || LocalDate.of(2039, 9, 26)
        LocalDate.of(2024, 1, 21)  | LocalDate.of(2024, 12, 27) | LocalDate.of(2023, 4, 1)  | false    | 1258.597              | 12         || 17091.40  || 0.00       || 2            || LocalDate.of(2025, 1, 27)
        LocalDate.of(2050, 1, 1)   | LocalDate.of(2050, 8, 22)  | LocalDate.of(2049, 6, 9)  | true     | 19.4199               | 1          || 4200.58   || 0.00       || 2            || LocalDate.of(2050, 9, 22)
        LocalDate.of(1988, 1, 28)  | LocalDate.of(1988, 9, 5)   | null                      | false    | 579.903               | 12         || 17770.10  || 0.00       || 2            || LocalDate.of(1988, 10, 5)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid dependants parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2048, 11, 30) | LocalDate.of(2049, 3, 9)  | LocalDate.of(2048, 3, 17) | true     | 428.18                | -3         || 7171.82   || 0.00       || 2            || LocalDate.of(2049, 5, 9)
        LocalDate.of(2050, 7, 26)  | LocalDate.of(2050, 8, 12) | LocalDate.of(2049, 9, 22) | false    | 1543.94               | -20        || 0.00      || 1265.00    || 0            || LocalDate.of(2050, 10, 12)
        LocalDate.of(1993, 5, 21)  | LocalDate.of(1994, 5, 17) | null                      | false    | 1879.34               | -11        || 15725.00  || 1265.00    || 2            || LocalDate.of(1994, 7, 17)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2017, 3, 27)  | LocalDate.of(2017, 6, 27) | null                    | true     | 984.58                | ")£("      || 1545.42   || 0.00       || 2            || LocalDate.of(2017, 7, 4)
        LocalDate.of(2011, 12, 31) | LocalDate.of(2012, 3, 4)  | null                    | true     | 1708.30               | "%£"       || 1265.00   || 1265.00    || 2            || LocalDate.of(2012, 3, 11)
        LocalDate.of(2037, 10, 2)  | LocalDate.of(2038, 7, 20) | null                    | false    | 487.82                | "(&"       || 12422.18  || 0.00       || 2            || LocalDate.of(2038, 9, 20)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid dependantsOnly parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, 0, dependantsOnly)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependantsOnly")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2048, 11, 30) | LocalDate.of(2049, 3, 9)  | LocalDate.of(2048, 3, 17) | true     | 428.18                | -3             || 7171.82   || 0.00       || 2            || LocalDate.of(2049, 5, 9)
        LocalDate.of(2050, 7, 26)  | LocalDate.of(2050, 8, 12) | LocalDate.of(2049, 9, 22) | false    | 1543.94               | -20            || 0.00      || 1265.00    || 0            || LocalDate.of(2050, 10, 12)
        LocalDate.of(1993, 5, 21)  | LocalDate.of(1994, 5, 17) | null                      | false    | 1879.34               | -11            || 15725.00  || 1265.00    || 2            || LocalDate.of(1994, 7, 17)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid characters dependantsOnly parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, 0, dependantsOnly)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependantsOnly")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate | inLondon | accommodationFeesPaid | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2017, 3, 27)  | LocalDate.of(2017, 6, 27) | null                    | true     | 984.58                | ")£("          || 1545.42   || 0.00       || 2            || LocalDate.of(2017, 7, 4)
        LocalDate.of(2011, 12, 31) | LocalDate.of(2012, 3, 4)  | null                    | true     | 1708.30               | "%£"           || 1265.00   || 1265.00    || 2            || LocalDate.of(2012, 3, 11)
        LocalDate.of(2037, 10, 2)  | LocalDate.of(2038, 7, 20) | null                    | false    | 487.82                | "(&"           || 12422.18  || 0.00       || 2            || LocalDate.of(2038, 9, 20)
    }

}
