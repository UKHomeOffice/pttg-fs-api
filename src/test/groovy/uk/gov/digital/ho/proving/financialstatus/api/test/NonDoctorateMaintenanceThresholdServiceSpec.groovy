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

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("courseExtensionEndDate", (courseExtensionEndDate == null) ? "" : courseExtensionEndDate.toString())
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
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate            | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | 7                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                   | 7307.00     | 0.00            | 0.00                  | 2          || 26652.00
        false    | 8                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                   | 5878.00     | 0.00            | 0.00                  | 4          || 38478.00
        false    | 9                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                   | 9180.00     | 0.00            | 0.00                  | 10         || 79515.00

    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate            | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true     | 7                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                   | 12672.00    | 0.00            | 0.00                  | 13         || 120392.00
        true     | 8                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                   | 14618.00    | 0.00            | 0.00                  | 10         || 100788.00
        true     | 9                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                   | 11896.00    | 0.00            | 0.00                  | 3          || 46096.00

    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate            | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | 7                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                   | 9870.00     | 4713.00         | 0.00                  | 12         || 85702.00
        false    | 8                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                   | 11031.00    | 395.00          | 0.00                  | 12         || 92196.00
        false    | 9                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                   | 12972.00    | 4774.00         | 0.00                  | 5          || 47933.00

    }


    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate            | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | 7                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                   | 10337.00    | 0.00            | 620.00                | 7          || 59662.00
        false    | 8                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                   | 6749.00     | 0.00            | 485.00                | 6          || 51104.00
        false    | 9                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                   | 6242.00     | 0.00            | 917.00                | 2          || 26700.00

    }

    def "Tier 4 Non Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
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
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | courseExtensionEndDate   | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold || feesCapped || courseLengthCapped
        false    | 14                   | LocalDate.of(2000, 1, 1) | LocalDate.of(2001, 2, 1)  | null                     | 447         | 1348            | 1396                  | 12         || 81310.00  || 1265       || 9
        true     | 7                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1)  | null                     | 305         | 112             | 1894                  | 1          || 15388.00  || 1265       || 0
        true     | 9                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1)  | null                     | 236         | 71              | 987                   | 13         || 109428.00 || 0          || 0
        true     | 5 + 7                | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 5, 1)  | LocalDate.of(2000, 5, 1) | 283         | 340             | 1524                  | 0          || 5060.00   || 1265       || 0
        false    | 15                   | LocalDate.of(2000, 1, 1) | LocalDate.of(2001, 3, 1)  | null                     | 842         | 709             | 302                   | 5          || 39566.00  || 0          || 9
        false    | 3                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1)  | null                     | 467         | 613             | 1727                  | 0          || 1780.00   || 1265       || 0
        false    | 3                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1)  | null                     | 71          | 213             | 229                   | 0          || 2816.00   || 0          || 0
        true     | 15                   | LocalDate.of(2000, 1, 1) | LocalDate.of(2001, 3, 1)  | null                     | 1446        | 1832            | 99                    | 5          || 49311.00  || 0          || 9
        false    | 15                   | LocalDate.of(2000, 1, 1) | LocalDate.of(2001, 3, 1)  | null                     | 1441        | 2285            | 686                   | 5          || 39049.00  || 0          || 9
        true     | 11                   | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 11, 1) | null                     | 1491        | 301             | 1359                  | 4          || 41730.00  || 1265       || 9
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                     | 781         | 418             | 70                    | 0          || 2823.00   || 0          || 0
        true     | 10                   | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 10, 1) | null                     | 437         | 605             | 204                   | 11         || 94836.00  || 0          || 9
        false    | 11                   | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 11, 1) | null                     | 932         | 734             | 1604                  | 7          || 50908.00  || 1265       || 9
        true     | 7                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1)  | null                     | 1348        | 916             | 1062                  | 7          || 61460.00  || 0          || 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                     | 1207        | 1404            | 610                   | 0          || 1920.00   || 0          || 0

    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFees")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | null                   | -2          | 1855.00         | 0          | 454.00
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                   | -0.05       | 4612.00         | 0          | 336.00
    }

    def "Tier 4 Non Doctorate - Check invalid characters intuition fees parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFees")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate            | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                   | "(*&"       | 4612.00         | 0          | 336.00
        false    | 3                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                   | "hh"        | 2720.00         | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                   | 1855.00     | -2              | 0          | 454.00
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                   | 4612.00     | -0.05           | 0          | 336.00
    }

    def "Tier 4 Non Doctorate - Check invalid characters in tuition fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate            | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                   | 4612.00     | "*^"            | 0          | 336.00
        false    | 3                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                   | 2720.00     | "kk"            | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid accommodation fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                   | 454.00      | 1855.00         | 0          | -2
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                   | 336.00      | 4612.00         | 0          | -0.05
    }

    def "Tier 4 Non Doctorate - Check invalid characters accommodation fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate            | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                   | 336.00      | 4612.00         | 0          | "*(^"
        false    | 3                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                   | 1044.00     | 2720.00         | 0          | "hh"
    }

    def "Tier 4 Non Doctorate - Check invalid dependants parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate            | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                   | 454.00      | 1855.00         | -5         | 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                   | 336.00      | 4612.00         | -99        | 0
    }

    def "Tier 4 Non Doctorate - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, courseExtensionEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        inLondon | courseLengthInMonths | courseStartDate          | courseEndDate             | courseExtensionEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | 1                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                   | 454.00      | 1855.00         | ")(&"      | 0
        true     | 2                    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                   | 336.00      | 4612.00         | "h"        | 0
    }

}
