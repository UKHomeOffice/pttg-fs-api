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

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("continuationEndDate", (continuationEndDate == null) ? "" : continuationEndDate.toString())
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
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | 7307.00     | 0.00            | 0.00                  | 2          || 26652.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                | 5878.00     | 0.00            | 0.00                  | 4          || 38478.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                | 9180.00     | 0.00            | 0.00                  | 10         || 79515.00

    }

    def "Tier 4 Non Doctorate - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | 12672.00    | 0.00            | 0.00                  | 13         || 120392.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                | 14618.00    | 0.00            | 0.00                  | 10         || 100788.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                | 11896.00    | 0.00            | 0.00                  | 3          || 46096.00

    }

    def "Tier 4 Non Doctorate - Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | 9870.00     | 4713.00         | 0.00                  | 12         || 85702.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                | 11031.00    | 395.00          | 0.00                  | 12         || 92196.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                | 12972.00    | 4774.00         | 0.00                  | 5          || 47933.00

    }


    def "Tier 4 Non Doctorate - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.threshold == threshold

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | 10337.00    | 0.00            | 620.00                | 7          || 59662.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 8, 1) | null                | 6749.00     | 0.00            | 485.00                | 6          || 51104.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 9, 1) | null                | 6242.00     | 0.00            | 917.00                | 2          || 26700.00

    }

    def "Tier 4 Non Doctorate - Check 'All variants'"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
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

        if (continuationLengthCapped > 0) {
            assert jsonContent.cappedValues && jsonContent.cappedValues.continuationLength != null
            assert jsonContent.cappedValues.continuationLength == continuationLengthCapped
        } else {
            assert jsonContent.cappedValues == null || jsonContent.cappedValues.continuationLengthCapped == null
        }


        if (feesCapped == 0 && courseLengthCapped == 0 && continuationLengthCapped == 0) {
            assert jsonContent.cappedvalues == null
        }

        where:
        courseStartDate           | courseEndDate              | continuationEndDate       | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants || threshold || feesCapped || courseLengthCapped || continuationLengthCapped
        LocalDate.of(2000, 1, 8)  | LocalDate.of(2001, 3, 3)   | LocalDate.of(2002, 5, 27) | true     | 1107        | 361             | 590                  || 4          || 41961.00  || 0          || 0                  || 9
        LocalDate.of(2000, 1, 9)  | LocalDate.of(2000, 10, 5)  | LocalDate.of(2001, 5, 3)  | true     | 855         | 387             | 1493                 || 4          || 38478.00  || 1265       || 0                  || 0
        LocalDate.of(2000, 1, 10) | LocalDate.of(2000, 4, 9)   | LocalDate.of(2000, 6, 8)  | false    | 584         | 1578            | 1929                 || 0          || 765.00    || 1265       || 0                  || 0
        LocalDate.of(2000, 1, 12) | LocalDate.of(2000, 12, 7)  | LocalDate.of(2001, 9, 3)  | true     | 658         | 485             | 335                  || 4          || 41643.00  || 0          || 0                  || 0
        LocalDate.of(2000, 1, 13) | LocalDate.of(2000, 7, 11)  | LocalDate.of(2001, 2, 6)  | true     | 1181        | 2141            | 1071                 || 8          || 68624.00  || 0          || 0                  || 0
        LocalDate.of(2000, 1, 14) | LocalDate.of(2001, 2, 7)   | LocalDate.of(2001, 9, 6)  | false    | 1420        | 170             | 29                   || 4          || 32806.00  || 0          || 0                  || 0
        LocalDate.of(2000, 1, 01) | LocalDate.of(2000, 11, 26) | LocalDate.of(2001, 6, 24) | true     | 23          | 510             | 301                  || 6          || 54184.00  || 0          || 0                  || 0
        LocalDate.of(2000, 1, 16) | LocalDate.of(2000, 10, 12) | null                      | true     | 963         | 1542            | 954                  || 2          || 25641.00  || 0          || 0                  || 0
        LocalDate.of(2000, 1, 17) | LocalDate.of(2000, 12, 12) | null                      | true     | 1455        | 1887            | 993                  || 7          || 63627.00  || 0          || 9                  || 0
        LocalDate.of(2000, 1, 18) | LocalDate.of(2001, 2, 11)  | null                      | true     | 424         | 625             | 671                  || 4          || 41134.00  || 0          || 9                  || 0
        LocalDate.of(2000, 1, 19) | LocalDate.of(2000, 5, 18)  | null                      | false    | 694         | 1685            | 967                  || 0          || 3093.00   || 0          || 0                  || 0
        LocalDate.of(2000, 1, 20) | LocalDate.of(2001, 1, 14)  | null                      | true     | 1008        | 14              | 404                  || 6          || 57605.00  || 0          || 9                  || 0
        LocalDate.of(2016, 1, 1)  | LocalDate.of(2016, 10, 5)  | LocalDate.of(2016, 12, 1) | true     | 2000.50     | 200             | 100                  || 2          || 14370.5   || 0          || 0                  || 0


    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFees")))

        where:
        inLondon | courseStartDate          | courseEndDate             | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | null                | -2          | 1855.00         | 0          | 454.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                | -0.05       | 4612.00         | 0          | 336.00
    }

    def "Tier 4 Non Doctorate - Check invalid characters intuition fees parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFees")))

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                | "(*&"       | 4612.00         | 0          | 336.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                | "hh"        | 2720.00         | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid tuition fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate             | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                | 1855.00     | -2              | 0          | 454.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                | 4612.00     | -0.05           | 0          | 336.00
    }

    def "Tier 4 Non Doctorate - Check invalid characters in tuition fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                | 4612.00     | "*^"            | 0          | 336.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                | 2720.00     | "kk"            | 0          | 1044.00
    }

    def "Tier 4 Non Doctorate - Check invalid accommodation fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate             | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                | 454.00      | 1855.00         | 0          | -2
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                | 336.00      | 4612.00         | 0          | -0.05
    }

    def "Tier 4 Non Doctorate - Check invalid characters accommodation fees paid parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                | 336.00      | 4612.00         | 0          | "*(^"
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                | 1044.00     | 2720.00         | 0          | "hh"
    }

    def "Tier 4 Non Doctorate - Check invalid dependants parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                | 454.00      | 1855.00         | -5         | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                | 336.00      | 4612.00         | -99        | 0
    }

    def "Tier 4 Non Doctorate - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("nondoctorate", inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate             | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                | 454.00      | 1855.00         | ")(&"      | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                | 336.00      | 4612.00         | "h"        | 0
    }

}
