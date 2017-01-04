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

//    def thresholdService = new ThresholdService(
//        new MaintenanceThresholdCalculator(inLondonMaintenance, notInLondonMaintenance,
//            maxMaintenanceAllowance, inLondonDependant, notInLondonDependant,
//            nonDoctorateMinCourseLength, nonDoctorateMaxCourseLength, nonDoctorateMinCourseLengthWithDependants,
//            pgddSsoMinCourseLength, pgddSsoMaxCourseLength, doctorateFixedCourseLength
//        ), getStudentTypeChecker(), serviceMessages, auditor, authenticator, 12, 2, 4
//    )

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
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate             | accommodationFeesPaid | dependants || threshold
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | 0.00                  | 5          || 4415.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0.00                  | 7          || 11550.00

    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate             | accommodationFeesPaid | dependants || threshold
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | 0.00                  | 4          || 4645.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0.00                  | 15         || 27880.00

    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate             | accommodationFeesPaid | dependants || threshold
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | 1039.00               | 14         || 12056.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 692.00                | 11         || 20428.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 11) | 622.00                | 3          || 3178.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 154.00                | 9          || 17586.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 11) | 869.00                | 10         || 8846.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 860.00                | 12         || 17490.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 21) | 106.00                | 11         || 16884.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 4)  | 1245.00               | 0          || 0.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 2106.00               | 11         || 15725.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 29) | 1845.00               | 0          || 0.00

    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'All variants'"() {
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
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate             | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | 14         | -1
        true     | LocalDate.of(2000, 2, 1) | LocalDate.of(2000, 2, 22) | 11         | -7
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid characters accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate           | courseEndDate             | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 21) | LocalDate.of(2000, 1, 21) | 14         | "(&"
        true     | LocalDate.of(2000, 2, 1)  | LocalDate.of(2000, 2, 1)  | 11         | "ddd"
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check rounding accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate             | dependants | accommodationFeesPaid || threshold
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | 0          | 0.0000                || 1015.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | 0.010                 || 2029.99
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | 0.0010                || 2030.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | 0.005                 || 2029.99
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | 0.004                 || 2030.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | -0.004                || 2030.00
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid dependants parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate             | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | -5         | 0
        true     | LocalDate.of(2000, 2, 1) | LocalDate.of(2000, 2, 21) | -986       | 0
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate             | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | "(*&66"    | 0
        true     | LocalDate.of(2000, 2, 1) | LocalDate.of(2000, 2, 1)  | "h"        | 0
    }

}
