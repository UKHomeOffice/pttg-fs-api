package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

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
class ServiceDateValidationSpec extends Specification {

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

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, originalCourseStartDate, accommodationFeesPaid, dependants, tuitionFees, tuitionFeesPaid) {


        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("originalCourseStartDate", originalCourseStartDate == null ? "" : originalCourseStartDate.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
                .param("tuitionFees", tuitionFees.toString())
                .param("tuitionFeesPaid", tuitionFeesPaid.toString())
                .param("courseType", "main")
                .param("dependantsOnly", "false")

        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    def "Validate date fields"() {

        expect:
        def response = callApi("general", true, courseStartDate, courseEndDate, originalCourseStartDate, 0, 0, 0, 0)
        response.andExpect(status().is(httpStatus))
        response.andExpect(content().string(containsString(statusMessage)))

        where:
        courseStartDate | courseEndDate | originalCourseStartDate || httpStatus || statusMessage
        "2000-01-01"    | "2000-01-02"  | "1999-01-03"            || 200        || "OK"
        "2000-0A-01"    | "2000-01-01"  | "2000-01-01"            || 400        || "Parameter conversion error: Invalid courseStartDate"
        "2000-01-01"    | "2A00-01-01"  | "2000-01-01"            || 400        || "Parameter conversion error: Invalid courseEndDate"
        "2000-01-01"    | "2000-01-01"  | "2000-01-0A"            || 400        || "Parameter conversion error: Invalid originalCourseStartDate"
        "2000-13-01"    | "2000-01-01"  | "2000-01-01"            || 400        || "Parameter conversion error: Invalid courseStartDate"
        "2000-01-01"    | "2000-01-32"  | "2000-01-01"            || 400        || "Parameter conversion error: Invalid courseEndDate"
        "2001-01-01"    | "2000-01-01"  | "2000-01-01"            || 400        || "Parameter error: Course start date must be before course end date"
        "2000-01-01"    | "2000-01-02"  | null                    || 200        || "OK"
        "2000-0A-01"    | "2000-01-01"  | null                    || 400        || "Parameter conversion error: Invalid courseStartDate"
        "2000-01-01"    | "2A00-01-01"  | null                    || 400        || "Parameter conversion error: Invalid courseEndDate"
        "2000-13-01"    | "2000-01-01"  | null                    || 400        || "Parameter conversion error: Invalid courseStartDate"
        "2000-01-01"    | "2000-01-32"  | null                    || 400        || "Parameter conversion error: Invalid courseEndDate"
        "2000-01-01"    | ""            | null                    || 400        || "Parameter conversion error: Invalid courseEndDate"
        ""              | "2000-01-31"  | null                    || 400        || "Parameter conversion error: Invalid courseStartDate"
        "2000-01-01"    | "2000-01-31"  | "2000-04-0A"            || 400        || "Parameter conversion error: Invalid originalCourseStartDate"

    }

}
