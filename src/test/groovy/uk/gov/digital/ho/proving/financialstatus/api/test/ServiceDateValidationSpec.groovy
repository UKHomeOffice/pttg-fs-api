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
class ServiceDateValidationSpec extends Specification {

    ServiceMessages serviceMessages = new ServiceMessages(getMessageSource())

    ApplicationEventPublisher auditor = Mock()
    Authentication authenticator = Mock()
    def thresholdService = new ThresholdService(
        new MaintenanceThresholdCalculator(inLondonMaintenance, notInLondonMaintenance,
            maxMaintenanceAllowance, inLondonDependant, notInLondonDependant,
            nonDoctorateMinCourseLength, nonDoctorateMaxCourseLength, nonDoctorateMinCourseLengthWithDependants,
            pgddSsoMinCourseLength, pgddSsoMaxCourseLength, doctorateFixedCourseLength
        ), getStudentTypeChecker(), serviceMessages, auditor, authenticator, 12, 2, 4
    )

    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper(), serviceMessages))
        .build()


    def url = TestUtils.thresholdUrl

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, continuationEndDate, accommodationFeesPaid, dependants, tuitionFees, tuitionFeesPaid) {


        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("continuationEndDate", continuationEndDate == null ? "" : continuationEndDate.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
                .param("tuitionFees", tuitionFees.toString())
                .param("tuitionFeesPaid", tuitionFeesPaid.toString())

        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Validate date fields"() {

        expect:
        def response = callApi("nondoctorate", true, courseStartDate, courseEndDate, continuationEndDate, 0, 0, 0, 0)
        response.andExpect(status().is(httpStatus))
        response.andExpect(content().string(containsString(statusMessage)))

        where:
        courseStartDate | courseEndDate | continuationEndDate || httpStatus || statusMessage
//        "2000-01-01"    | "2000-01-02"  | "2000-01-03"        || 200        || "OK"
//        "2000-0A-01"    | "2000-01-01"  | "2000-01-01"        || 400        || "Parameter conversion error: Invalid courseStartDate"
//        "2000-01-01"    | "2A00-01-01"  | "2000-01-01"        || 400        || "Parameter conversion error: Invalid courseEndDate"
//        "2000-01-01"    | "2000-01-01"  | "2000-01-0A"        || 400        || "Parameter conversion error: Invalid continuationEndDate"
//        "2000-13-01"    | "2000-01-01"  | "2000-01-01"        || 400        || "Parameter conversion error: Invalid courseStartDate"
//        "2000-01-01"    | "2000-01-32"  | "2000-01-01"        || 400        || "Parameter conversion error: Invalid courseEndDate"
//        "2001-01-01"    | "2000-01-01"  | "2000-01-01"        || 400        || "Course end date must be after course start date"
//        "2000-01-01"    | "2001-01-01"  | "2000-01-01"        || 400        || "Continuation end date must be after course end date"
//        "2000-01-01"    | "2000-01-02"  | null                || 200        || "OK"
//        "2000-0A-01"    | "2000-01-01"  | null                || 400        || "Parameter conversion error: Invalid courseStartDate"
//        "2000-01-01"    | "2A00-01-01"  | null                || 400        || "Parameter conversion error: Invalid courseEndDate"
//        "2000-13-01"    | "2000-01-01"  | null                || 400        || "Parameter conversion error: Invalid courseStartDate"
//        "2000-01-01"    | "2000-01-32"  | null                || 400        || "Parameter conversion error: Invalid courseEndDate"
        "2000-01-01"    | ""            | null                || 400        || "Parameter conversion error: Invalid courseEndDate"
        ""              | "2000-01-31"  | null                || 400        || "Parameter conversion error: Invalid courseStartDate"
        "2000-01-01"    | "2000-01-31"  | "2000-04-0A"        || 400        || "Parameter conversion error: Invalid continuationEndDate"

    }

}
