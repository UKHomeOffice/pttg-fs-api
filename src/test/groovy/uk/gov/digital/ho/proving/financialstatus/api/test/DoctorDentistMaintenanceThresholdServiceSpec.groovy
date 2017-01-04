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
class DoctorDentistMaintenanceThresholdServiceSpec extends Specification {

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
            doctorateFixedCourseLength,
            susoMinCourseLength, susoMaxCourseLength
        ),
        getStudentTypeChecker(), getCourseTypeChecker(), serviceMessages, auditor, authenticator, 12, 2, 4
    )


    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(), serviceMessages))
        .build()

    def url = TestUtils.thresholdUrl

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

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2013, 11, 19) | LocalDate.of(2014, 6, 25)  | LocalDate.of(2013, 4, 14) | false    | 1489.36               | 12         || 17085.00  || 1265.00    || 2            || LocalDate.of(2014, 7, 25)
        LocalDate.of(1990, 8, 6)   | LocalDate.of(1991, 4, 20)  | LocalDate.of(1990, 3, 12) | false    | 435.72                | 3          || 5674.28   || 0.00       || 2            || LocalDate.of(1991, 5, 20)
        LocalDate.of(2024, 1, 16)  | LocalDate.of(2024, 11, 14) | LocalDate.of(2023, 7, 1)  | false    | 1350.59               | 14         || 19805.00  || 1265.00    || 2            || LocalDate.of(2024, 12, 14)
        LocalDate.of(2011, 5, 14)  | LocalDate.of(2011, 10, 23) | LocalDate.of(2010, 7, 9)  | false    | 0.00                  | 9          || 14270.00  || 0.00       || 2            || LocalDate.of(2011, 11, 23)
        LocalDate.of(1982, 6, 15)  | LocalDate.of(1982, 11, 4)  | LocalDate.of(1982, 5, 11) | false    | 1545.57               | 4          || 6205.00   || 1265.00    || 2            || LocalDate.of(1982, 12, 4)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1998, 10, 11) | LocalDate.of(1999, 8, 12)  | LocalDate.of(1998, 8, 21) | true     | 0.00                  | 12         || 22810.00  || 0.00       || 2            || LocalDate.of(1999, 9, 12)
        LocalDate.of(2035, 3, 28)  | LocalDate.of(2036, 1, 18)  | LocalDate.of(2034, 5, 31) | true     | 0.00                  | 5          || 10980.00  || 0.00       || 2            || LocalDate.of(2036, 2, 18)
        LocalDate.of(2020, 1, 20)  | LocalDate.of(2020, 2, 10)  | null                      | true     | 0.00                  | 0          || 1265.00   || 0.00       || 0            || LocalDate.of(2020, 3, 10)
        LocalDate.of(1979, 1, 8)   | LocalDate.of(1979, 10, 20) | null                      | true     | 235.11                | 0          || 2294.89   || 0.00       || 2            || LocalDate.of(1979, 11, 20)
        LocalDate.of(1997, 8, 21)  | LocalDate.of(1998, 4, 3)   | LocalDate.of(1997, 7, 23) | true     | 1394.13               | 14         || 24925.00  || 1265.00    || 2            || LocalDate.of(1998, 5, 3)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate           | courseEndDate             | originalCourseStartDate   | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1981, 9, 12) | LocalDate.of(1982, 3, 25) | LocalDate.of(1981, 6, 14) | false    | 1151.61               | 6          || 9038.39   || 0.00       || 2            || LocalDate.of(1982, 4, 25)
        LocalDate.of(2014, 2, 28) | LocalDate.of(2014, 11, 5) | null                      | true     | 1216.22               | 14         || 24973.78  || 0.00       || 2            || LocalDate.of(2014, 12, 5)
        LocalDate.of(2033, 9, 11) | LocalDate.of(2034, 5, 5)  | null                      | true     | 330.37                | 4          || 8959.63   || 0.00       || 2            || LocalDate.of(2034, 6, 5)
        LocalDate.of(2027, 9, 1)  | LocalDate.of(2028, 8, 30) | null                      | false    | 1556.53               | 13         || 18445.00  || 1265.00    || 2            || LocalDate.of(2028, 9, 30)
        LocalDate.of(1985, 8, 23) | LocalDate.of(1986, 7, 3)  | LocalDate.of(1985, 7, 12) | false    | 1644.30               | 11         || 15725.00  || 1265.00    || 2            || LocalDate.of(1986, 8, 3)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'All variants'"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
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
        LocalDate.of(1992, 10, 7)  | LocalDate.of(1993, 9, 13)  | LocalDate.of(1992, 7, 1)   | false    | 1742.51               | 8          || 11645.00  || 1265.00    || 2            || LocalDate.of(1993, 10, 13)
        LocalDate.of(2039, 7, 21)  | LocalDate.of(2039, 11, 14) | LocalDate.of(2038, 10, 8)  | true     | 331.83                | 10         || 19098.17  || 0.00       || 2            || LocalDate.of(2039, 12, 14)
        LocalDate.of(2021, 6, 28)  | LocalDate.of(2022, 7, 12)  | null                       | true     | 0.00                  | 1          || 4220.00   || 0.00       || 2            || LocalDate.of(2022, 8, 12)
        LocalDate.of(2024, 2, 3)   | LocalDate.of(2024, 5, 4)   | null                       | false    | 0.00                  | 0          || 2030.00   || 0.00       || 2            || LocalDate.of(2024, 6, 4)
        LocalDate.of(2027, 3, 4)   | LocalDate.of(2027, 5, 14)  | LocalDate.of(2026, 12, 26) | false    | 0.00                  | 11         || 16990.00  || 0.00       || 2            || LocalDate.of(2027, 6, 14)
        LocalDate.of(2019, 1, 3)   | LocalDate.of(2019, 6, 17)  | LocalDate.of(2018, 9, 15)  | true     | 184.16                | 12         || 22625.84  || 0.00       || 2            || LocalDate.of(2019, 7, 17)
        LocalDate.of(2012, 12, 7)  | LocalDate.of(2013, 5, 27)  | LocalDate.of(2012, 5, 31)  | false    | 254.25                | 1          || 3135.75   || 0.00       || 2            || LocalDate.of(2013, 6, 27)
        LocalDate.of(2045, 4, 10)  | LocalDate.of(2045, 5, 29)  | LocalDate.of(2045, 4, 6)   | false    | 0.00                  | 0          || 2030.00   || 0.00       || 0            || LocalDate.of(2045, 6, 29)
        LocalDate.of(1982, 7, 29)  | LocalDate.of(1983, 3, 17)  | null                       | false    | 0.00                  | 4          || 7470.00   || 0.00       || 2            || LocalDate.of(1983, 4, 17)
        LocalDate.of(2041, 8, 28)  | LocalDate.of(2042, 6, 7)   | LocalDate.of(2041, 3, 12)  | false    | 0.00                  | 12         || 18350.00  || 0.00       || 2            || LocalDate.of(2042, 7, 7)
        LocalDate.of(1985, 1, 12)  | LocalDate.of(1985, 1, 25)  | LocalDate.of(1984, 12, 11) | false    | 1308.00               | 6          || 7910.00   || 1265.00    || 0            || LocalDate.of(1985, 2, 25)
        LocalDate.of(1992, 11, 16) | LocalDate.of(1993, 2, 22)  | LocalDate.of(1992, 5, 10)  | false    | 0.00                  | 10         || 15630.00  || 0.00       || 2            || LocalDate.of(1993, 3, 22)
        LocalDate.of(1993, 4, 13)  | LocalDate.of(1993, 11, 23) | LocalDate.of(1993, 1, 15)  | true     | 331.62                | 13         || 24168.38  || 0.00       || 2            || LocalDate.of(1993, 12, 23)
        LocalDate.of(1997, 6, 14)  | LocalDate.of(1998, 4, 14)  | LocalDate.of(1997, 1, 13)  | true     | 0.00                  | 1          || 4220.00   || 0.00       || 2            || LocalDate.of(1998, 5, 14)
        LocalDate.of(1994, 6, 16)  | LocalDate.of(1995, 7, 17)  | null                       | false    | 896.02                | 2          || 3853.98   || 0.00       || 2            || LocalDate.of(1995, 8, 17)
        LocalDate.of(1976, 1, 5)   | LocalDate.of(1976, 3, 7)   | LocalDate.of(1975, 8, 6)   | false    | 0.00                  | 9          || 14270.00  || 0.00       || 2            || LocalDate.of(1976, 4, 7)
        LocalDate.of(1976, 2, 27)  | LocalDate.of(1977, 1, 4)   | LocalDate.of(1976, 1, 23)  | false    | 940.81                | 8          || 11969.19  || 0.00       || 2            || LocalDate.of(1977, 2, 4)
        LocalDate.of(2048, 9, 14)  | LocalDate.of(2049, 3, 6)   | null                       | false    | 0.00                  | 0          || 2030.00   || 0.00       || 2            || LocalDate.of(2049, 4, 6)
        LocalDate.of(1992, 6, 13)  | LocalDate.of(1993, 6, 22)  | LocalDate.of(1991, 7, 3)   | false    | 1061.83               | 0          || 968.17    || 0.00       || 2            || LocalDate.of(1993, 7, 22)
        LocalDate.of(2052, 2, 2)   | LocalDate.of(2052, 9, 23)  | LocalDate.of(2051, 9, 4)   | true     | 0.00                  | 6          || 12670.00  || 0.00       || 2            || LocalDate.of(2052, 10, 23)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        courseStartDate           | courseEndDate            | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 2, 26) | LocalDate.of(2003, 6, 5) | LocalDate.of(2002, 5, 23)  | true     | -1                    | 11         || 20541.70  || 0.00       || 2            || LocalDate.of(2003, 10, 5)
        LocalDate.of(2001, 7, 9)  | LocalDate.of(2001, 9, 5) | LocalDate.of(2000, 12, 24) | false    | -7                    | 13         || 18685.25  || 0.00       || 0            || LocalDate.of(2001, 11, 5)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid characters accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        courseStartDate           | courseEndDate            | originalCourseStartDate    | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2003, 2, 26) | LocalDate.of(2003, 6, 5) | LocalDate.of(2002, 5, 23)  | true     | "^&"                  | 11         || 20541.70  || 0.00       || 2            || LocalDate.of(2003, 10, 5)
        LocalDate.of(2001, 7, 9)  | LocalDate.of(2001, 9, 5) | LocalDate.of(2000, 12, 24) | false    | ")()"                 | 13         || 18685.25  || 0.00       || 0            || LocalDate.of(2001, 11, 5)
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check rounding accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

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
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
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
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        courseStartDate            | courseEndDate             | originalCourseStartDate | inLondon | accommodationFeesPaid | dependants || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2017, 3, 27)  | LocalDate.of(2017, 6, 27) | null                    | true     | 984.58                | ")£("      || 1545.42   || 0.00       || 2            || LocalDate.of(2017, 7, 4)
        LocalDate.of(2011, 12, 31) | LocalDate.of(2012, 3, 4)  | null                    | true     | 1708.30               | "%£"       || 1265.00   || 1265.00    || 2            || LocalDate.of(2012, 3, 11)
        LocalDate.of(2037, 10, 2)  | LocalDate.of(2038, 7, 20) | null                    | false    | 487.82                | "(&"       || 12422.18  || 0.00       || 2            || LocalDate.of(2038, 9, 20)
    }

}
