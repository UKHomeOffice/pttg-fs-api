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
class DoctorDentistMaintenanceThresholdServiceSpec extends Specification {

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

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
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
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | accommodationFeesPaid | dependants || threshold
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | 0.00                  | 5          || 4415.00
        false    | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0.00                  | 7          || 11550.00

    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | accommodationFeesPaid | dependants || threshold
        true     | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | 0.00                  | 4          || 4645.00
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0.00                  | 15         || 27880.00

    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | accommodationFeesPaid | dependants || threshold
        true     | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | 1039.00               | 14         || 12056.00
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 692.00                | 11         || 20428.00
        true     | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 11) | 622.00                | 3          || 3178.00
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 154.00                | 9          || 17586.00
        true     | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 11) | 869.00                | 10         || 8846.00
        false    | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 860.00                | 12         || 17490.00
        false    | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 21) | 106.00                | 11         || 16884.00
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 4)  | 1245.00               | 0          || 0.00
        false    | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 2106.00               | 11         || 15725.00
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 29) | 1845.00               | 0          || 0.00

    }

    def "Tier 4 Post Grad Doctor or Dentist - Check 'All variants'"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
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

        if (feesCapped == 0 && courseLengthCapped == 0) {
            assert jsonContent.cappedvalues == null
        }

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | accommodationFeesPaid | dependants || threshold || feesCapped || courseLengthCapped
        false    | 5                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 5, 1)  | 1627.00               | 15         || 21165.00  || 1265.00    || 2
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | 270.00                | 10         || 7545.00   || 0          || 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 22.00                 | 1          || 4198.00   || 0          || 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 636.00                | 9          || 17104.00  || 0          || 0
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 11) | 1018.00               | 3          || 2037.00   || 0          || 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 446.00                | 6          || 12224.00  || 0          || 0
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | 372.00                | 6          || 4723.00   || 0          || 0
        true     | 7                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1)  | 657.00                | 13         || 23843.00  || 0          || 2
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 953.00                | 6          || 11717.00  || 0          || 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 229.00                | 12         || 22581.00  || 0          || 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 23.00                 | 12         || 22787.00  || 0          || 0
        false    | 3                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1)  | 182.00                | 14         || 20888.00  || 0          || 2
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | 738.00                | 12         || 8437.00   || 0          || 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 73.00                 | 9          || 17667.00  || 0          || 0
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 4)  | 970.00                | 6          || 4125.00   || 0          || 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 1934.00               | 5          || 9715.00   || 1265.00    || 0
        true     | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 3)  | 223.00                | 4          || 4422.00   || 0          || 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 1078.00               | 14         || 25112.00  || 0          || 0
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | dependants | accommodationFeesPaid
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | 14         | -1
        true     | 2                    | LocalDate.of(2000, 2, 1) | LocalDate.of(2000, 2, 22) | 11         | -7
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid characters accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseLengthInMonths | courseStartDate           | courseEndDate             | dependants | accommodationFeesPaid
        false    | 1                    | LocalDate.of(2000, 1, 21) | LocalDate.of(2000, 1, 21) | 14         | "(&"
        true     | 2                    | LocalDate.of(2000, 2, 1)  | LocalDate.of(2000, 2, 1)  | 11         | "ddd"
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check rounding accommodation fees parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())

        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | dependants | accommodationFeesPaid || threshold
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | 0          | 0.0000                || 1015.00
        false    | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | 0.010                 || 2029.99
        false    | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | 0.0010                || 2030.00
        false    | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | 0.005                 || 2029.99
        false    | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | 0.004                 || 2030.00
        false    | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | 0          | -0.004                || 2030.00
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid dependants parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | dependants | accommodationFeesPaid
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | -5         | 0
        true     | 2                    | LocalDate.of(2000, 2, 1) | LocalDate.of(2000, 2, 21) | -986       | 0
    }

    def "Tier 4 Post Grad Doctor or Dentist - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("pgdd", inLondon, courseStartDate, courseEndDate, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | dependants | accommodationFeesPaid
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | "(*&66"    | 0
        true     | 2                    | LocalDate.of(2000, 2, 1) | LocalDate.of(2000, 2, 1)  | "h"        | 0
    }

}
