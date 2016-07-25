package uk.gov.digital.ho.proving.financialstatus.api.test

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdService
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

import static TestUtils.getMessageSource
import static TestUtils.getStudentTypeChecker
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
class StudentTypeMaintenanceThresholdServiceSpec extends Specification {

    def thresholdService = new ThresholdService(
        new MaintenanceThresholdCalculator(TestUtils.innerLondonMaintenance, TestUtils.nonInnerLondonMaintenance,
            TestUtils.maxMaintenanceAllowance, TestUtils.innerLondonDependant, TestUtils.nonInnerLondonDependant,
            TestUtils.nonDoctorateMinCourseLength, TestUtils.nonDoctorateMaxCourseLength,
            TestUtils.doctorateMinCourseLength, TestUtils.doctorateMaxCourseLength
        ), getMessageSource(), getStudentTypeChecker()
    )

    MockMvc mockMvc = standaloneSetup(thresholdService)
        .setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter())
        .setControllerAdvice(new ApiExceptionHandler(new ServiceConfiguration().objectMapper()))
        .build()


    def url = TestUtils.thresholdUrl

    def callApi(studentType, innerLondon, courseLengthInMonths, accommodationFeesPaid, dependants, tuitionFees, tuitionFeesPaid) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("innerLondon", innerLondon.toString())
                .param("courseLength", courseLengthInMonths.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
                .param("tuitionFees", tuitionFees.toString())
                .param("tuitionFeesPaid", tuitionFeesPaid.toString())

        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Tier 4 Student types"() {

        expect:
        def response = callApi(studentType, true, 1, 0, 0, 0, 0)
        response.andExpect(status().is(httpStatus))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == statusMessage

        where:
        studentType    || httpStatus || statusMessage
        "doctorate"    || 200        || "OK"
        "nondoctorate" || 200        || "OK"
        "pgdd"         || 200        || "OK"
        "rubbish"      || 400        || "Parameter error: Invalid studentType, must be one of [doctorate,nondoctorate,pgdd]"
        ""             || 400        || "Parameter error: Invalid studentType, must be one of [doctorate,nondoctorate,pgdd]"

    }


}
