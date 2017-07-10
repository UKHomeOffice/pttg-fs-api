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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = [ ServiceConfiguration.class, EmbeddedMongoClientConfiguration.class ])
class CourseTypeCheckerServiceSpec extends Specification {

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

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, tuitionFees, tuitionFeesPaid, courseType, dependantsOnly) {


        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
                .param("dependantsOnly", dependantsOnly.toString())
                .param("tuitionFees", tuitionFees.toString())
                .param("tuitionFeesPaid", tuitionFeesPaid.toString())
                .param("courseType", courseType)

        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Tier 4 Course types"() {

        expect:
        def response = callApi("general", true, LocalDate.of(2000, 1, 1), LocalDate.of(2000, 5, 31), LocalDate.of(1999, 9, 3), 0, 0, 0, 0, courseType, false)
        response.andExpect(status().is(httpStatus))
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == statusMessage

        where:
        courseType      || httpStatus || statusMessage
        "main"          || 200        || "OK"
        "MAIN"          || 200        || "OK"
        "MAin"          || 200        || "OK"
        "pre-sessional" || 200        || "OK"
        "PRE-SESSIONAL" || 200        || "OK"
        "prE-sessional" || 200        || "OK"
        "rubbish"       || 400        || "Parameter error: Invalid courseType, must be one of [main,pre-sessional]"
        ""              || 400        || "Parameter error: Invalid courseType, must be one of [main,pre-sessional]"
    }

}
