package uk.gov.digital.ho.proving.financialstatus.api.test.tier4

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import spock.lang.Unroll
import uk.gov.digital.ho.proving.financialstatus.api.ThresholdServiceTier4
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventPublisher
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
@ContextConfiguration(classes = ServiceConfiguration.class)
class GeneralMaintenanceThresholdServiceSpec extends Specification {

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

    def callApi(studentType, inLondon, courseStartDate, courseEndDate, originalCourseStartDate,
                tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly) {
        def response = mockMvc.perform(
            get(url)
                .param("studentType", studentType)
                .param("inLondon", inLondon.toString())
                .param("courseStartDate", courseStartDate.toString())
                .param("courseEndDate", courseEndDate.toString())
                .param("originalCourseStartDate", (originalCourseStartDate == null) ? "" : originalCourseStartDate.toString())
                .param("tuitionFees", tuitionFees.toString())
                .param("tuitionFeesPaid", tuitionFeesPaid.toString())
                .param("accommodationFeesPaid", accommodationFeesPaid.toString())
                .param("dependants", dependants.toString())
                .param("dependantsOnly", dependantsOnly.toString())
                .param("courseType", courseType)
        )
        response.andDo(MockMvcResultHandlers.print())
        response
    }

    @Unroll
    def "Tier 4 General - Check 'Non Inner London Borough'"() {

        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate   | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1980, 4, 19)  | LocalDate.of(1980, 5, 13)  | null                      | false    | 9938.19     | 0.00            | 271.64                | 0          | "pre-sessional" | false          || 10681.55  || 0.00       || 0            || LocalDate.of(1980, 6, 13)
        LocalDate.of(2047, 5, 30)  | LocalDate.of(2048, 1, 16)  | null                      | false    | 7453.34     | 16.49           | 1224.23               | 4          | "main"          | false          || 38812.62  || 0.00       || 0            || LocalDate.of(2048, 3, 16)
        LocalDate.of(2014, 12, 22) | LocalDate.of(2015, 6, 3)   | null                      | false    | 0.00        | 0.00            | 201.32                | 0          | "pre-sessional" | false          || 5888.68   || 0.00       || 0            || LocalDate.of(2015, 7, 3)
        LocalDate.of(1993, 8, 13)  | LocalDate.of(1994, 8, 22)  | LocalDate.of(1993, 6, 12) | false    | 9190.11     | 9679.18         | 476.20                | 10         | "main"          | false          || 69858.80  || 0.00       || 9            || LocalDate.of(1994, 12, 22)
        LocalDate.of(2014, 7, 15)  | LocalDate.of(2014, 10, 28) | null                      | false    | 3165.93     | 0.00            | 0.00                  | 0          | "main"          | false          || 7225.93   || 0.00       || 0            || LocalDate.of(2014, 11, 4)
        LocalDate.of(1985, 3, 29)  | LocalDate.of(1985, 5, 4)   | LocalDate.of(1984, 9, 5)  | false    | 6394.10     | 9358.72         | 0.00                  | 3          | "main"          | false          || 10190.00  || 0.00       || 0            || LocalDate.of(1985, 7, 4)
        LocalDate.of(2050, 9, 11)  | LocalDate.of(2051, 7, 2)   | LocalDate.of(2050, 6, 17) | false    | 0.00        | 0.00            | 1224.79               | 1          | "main"          | false          || 14030.21  || 0.00       || 9            || LocalDate.of(2051, 11, 2)
        LocalDate.of(2050, 12, 29) | LocalDate.of(2051, 5, 23)  | LocalDate.of(2050, 11, 9) | false    | 5423.68     | 0.00            | 0.00                  | 7          | "main"          | false          || 43818.68  || 0.00       || 0            || LocalDate.of(2051, 7, 23)
        LocalDate.of(2001, 5, 28)  | LocalDate.of(2002, 2, 6)   | null                      | false    | 7393.73     | 0.00            | 0.00                  | 1          | "main"          | false          || 22648.73  || 0.00       || 0            || LocalDate.of(2002, 4, 6)
        LocalDate.of(2042, 11, 10) | LocalDate.of(2043, 4, 1)   | null                      | false    | 8722.26     | 0.00            | 1901.71               | 0          | "pre-sessional" | false          || 12532.26  || 1265.00    || 0            || LocalDate.of(2043, 5, 1)
        LocalDate.of(1983, 12, 27) | LocalDate.of(1984, 12, 4)  | LocalDate.of(1983, 8, 20) | false    | 6639.64     | 8437.99         | 0.00                  | 9          | "main"          | false          || 64215.00  || 0.00       || 9            || LocalDate.of(1985, 4, 4)
        LocalDate.of(2009, 1, 29)  | LocalDate.of(2009, 8, 20)  | LocalDate.of(2009, 1, 20) | false    | 9792.39     | 0.00            | 1661.29               | 6          | "main"          | false          || 52352.39  || 1265.00    || 0            || LocalDate.of(2009, 10, 20)
        LocalDate.of(1977, 7, 1)   | LocalDate.of(1977, 12, 11) | LocalDate.of(1976, 9, 15) | false    | 2525.75     | 0.00            | 0.00                  | 3          | "main"          | false          || 26975.75  || 0.00       || 0            || LocalDate.of(1978, 4, 11)
        LocalDate.of(1999, 4, 17)  | LocalDate.of(2000, 3, 26)  | LocalDate.of(1998, 4, 30) | false    | 8151.74     | 8520.21         | 0.00                  | 4          | "main"          | false          || 33615.00  || 0.00       || 9            || LocalDate.of(2000, 7, 26)
        LocalDate.of(1990, 10, 13) | LocalDate.of(1991, 7, 1)   | LocalDate.of(1990, 3, 27) | false    | 2747.84     | 3187.56         | 0.00                  | 8          | "main"          | false          || 58095.00  || 0.00       || 0            || LocalDate.of(1991, 11, 1)
        LocalDate.of(2037, 7, 5)   | LocalDate.of(2038, 2, 19)  | LocalDate.of(2037, 6, 6)  | false    | 4650.69     | 800.24          | 429.02                | 8          | "main"          | false          || 60501.43  || 0.00       || 0            || LocalDate.of(2038, 4, 19)
        LocalDate.of(2019, 10, 27) | LocalDate.of(2020, 3, 19)  | null                      | false    | 6612.23     | 0.00            | 0.00                  | 0          | "main"          | false          || 11687.23  || 0.00       || 0            || LocalDate.of(2020, 3, 26)
        LocalDate.of(2028, 8, 13)  | LocalDate.of(2028, 12, 26) | null                      | false    | 8401.55     | 5972.31         | 807.83                | 0          | "main"          | false          || 6696.41   || 0.00       || 0            || LocalDate.of(2029, 1, 2)
        LocalDate.of(2023, 7, 6)   | LocalDate.of(2023, 11, 17) | LocalDate.of(2023, 6, 19) | false    | 4149.12     | 0.00            | 0.00                  | 12         | "main"          | false          || 50024.12  || 0.00       || 0            || LocalDate.of(2023, 11, 24)
        LocalDate.of(2033, 10, 29) | LocalDate.of(2033, 11, 14) | null                      | false    | 7248.51     | 1895.12         | 0.00                  | 0          | "main"          | false          || 6368.39   || 0.00       || 0            || LocalDate.of(2033, 11, 21)
    }

    @Unroll
    def "Tier 4 General - Check 'Inner London Borough'"() {

        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1987, 1, 4)   | LocalDate.of(1987, 6, 28)  | null                       | true     | 523.52      | 2474.68         | 146.95                | 0          | "pre-sessional" | false          || 7443.05   || 0.00       || 0            || LocalDate.of(1987, 7, 28)
        LocalDate.of(2009, 10, 10) | LocalDate.of(2010, 9, 8)   | LocalDate.of(2009, 9, 19)  | true     | 1650.71     | 0.00            | 0.00                  | 7          | "main"          | false          || 66270.71  || 0.00       || 9            || LocalDate.of(2010, 11, 8)
        LocalDate.of(2003, 7, 31)  | LocalDate.of(2003, 10, 3)  | null                       | true     | 3600.64     | 7388.82         | 462.04                | 0          | "main"          | false          || 3332.96   || 0.00       || 0            || LocalDate.of(2003, 10, 10)
        LocalDate.of(2024, 10, 8)  | LocalDate.of(2025, 10, 2)  | null                       | true     | 8781.65     | 8447.22         | 773.88                | 0          | "pre-sessional" | false          || 10945.55  || 0.00       || 9            || LocalDate.of(2025, 12, 2)
        LocalDate.of(2039, 3, 2)   | LocalDate.of(2039, 12, 29) | null                       | true     | 7044.27     | 4137.89         | 1988.88               | 0          | "main"          | false          || 13026.38  || 1265.00    || 9            || LocalDate.of(2040, 2, 29)
        LocalDate.of(1997, 2, 3)   | LocalDate.of(1997, 8, 8)   | LocalDate.of(1996, 3, 21)  | true     | 8463.61     | 9290.33         | 0.00                  | 6          | "main"          | false          || 54485.00  || 0.00       || 0            || LocalDate.of(1997, 12, 8)
        LocalDate.of(2004, 3, 19)  | LocalDate.of(2004, 8, 12)  | null                       | true     | 6709.23     | 0.00            | 0.00                  | 0          | "main"          | false          || 13034.23  || 0.00       || 0            || LocalDate.of(2004, 8, 19)
        LocalDate.of(2032, 4, 12)  | LocalDate.of(2032, 7, 16)  | LocalDate.of(2031, 3, 11)  | true     | 3937.89     | 0.00            | 1583.43               | 14         | "main"          | false          || 102372.89 || 1265.00    || 0            || LocalDate.of(2032, 11, 16)
        LocalDate.of(2045, 2, 28)  | LocalDate.of(2046, 4, 3)   | null                       | true     | 0.00        | 0.00            | 574.64                | 2          | "main"          | false          || 26020.36  || 0.00       || 9            || LocalDate.of(2046, 8, 3)
        LocalDate.of(2011, 4, 11)  | LocalDate.of(2011, 8, 28)  | LocalDate.of(2011, 3, 3)   | true     | 3436.54     | 0.00            | 0.00                  | 6          | "main"          | false          || 35111.54  || 0.00       || 0            || LocalDate.of(2011, 9, 4)
        LocalDate.of(1989, 11, 14) | LocalDate.of(1989, 12, 2)  | LocalDate.of(1989, 9, 2)   | true     | 1407.48     | 7091.06         | 0.00                  | 6          | "main"          | false          || 6335.00   || 0.00       || 0            || LocalDate.of(1989, 12, 9)
        LocalDate.of(2000, 7, 11)  | LocalDate.of(2000, 8, 29)  | LocalDate.of(2000, 5, 13)  | true     | 2083.49     | 2309.01         | 0.00                  | 8          | "main"          | false          || 16050.00  || 0.00       || 0            || LocalDate.of(2000, 9, 5)
        LocalDate.of(2039, 3, 16)  | LocalDate.of(2039, 7, 28)  | null                       | true     | 2232.43     | 0.00            | 136.45                | 0          | "main"          | false          || 8420.98   || 0.00       || 0            || LocalDate.of(2039, 8, 4)
        LocalDate.of(2021, 8, 10)  | LocalDate.of(2022, 7, 31)  | LocalDate.of(2020, 7, 13)  | true     | 9896.38     | 0.00            | 0.00                  | 9          | "main"          | false          || 89726.38  || 0.00       || 9            || LocalDate.of(2022, 11, 30)
        LocalDate.of(2007, 9, 23)  | LocalDate.of(2008, 6, 14)  | null                       | true     | 9701.00     | 9977.58         | 532.83                | 13         | "pre-sessional" | false          || 109717.17 || 0.00       || 0            || LocalDate.of(2008, 8, 14)
        LocalDate.of(2008, 10, 12) | LocalDate.of(2009, 4, 8)   | null                       | true     | 1305.44     | 7467.41         | 0.00                  | 0          | "main"          | false          || 7590.00   || 0.00       || 0            || LocalDate.of(2009, 4, 15)
        LocalDate.of(2048, 11, 28) | LocalDate.of(2049, 8, 28)  | null                       | true     | 8837.75     | 8017.76         | 0.00                  | 9          | "main"          | false          || 80649.99  || 0.00       || 9            || LocalDate.of(2049, 10, 28)
        LocalDate.of(2019, 5, 11)  | LocalDate.of(2020, 3, 12)  | null                       | true     | 1958.33     | 0.00            | 300.98                | 12         | "main"          | false          || 104302.35 || 0.00       || 9            || LocalDate.of(2020, 5, 12)
        LocalDate.of(2035, 11, 15) | LocalDate.of(2036, 4, 7)   | LocalDate.of(2035, 10, 22) | true     | 3775.45     | 3714.79         | 0.00                  | 2          | "main"          | false          || 14835.66  || 0.00       || 0            || LocalDate.of(2036, 4, 14)
        LocalDate.of(2053, 5, 18)  | LocalDate.of(2053, 6, 30)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "main"          | false          || 2530.00   || 0.00       || 0            || LocalDate.of(2053, 7, 7)
    }

    @Unroll
    def "Tier 4 General - Check 'Tuition Fees paid'"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2048, 5, 15)  | LocalDate.of(2049, 1, 2)   | LocalDate.of(2047, 5, 25)  | true     | 3823.21     | 3765.01         | 821.94                | 9          | "main"          | false          || 77801.26  || 0.00       || 0            || LocalDate.of(2049, 5, 2)
        LocalDate.of(1987, 3, 4)   | LocalDate.of(1987, 5, 31)  | null                       | false    | 4791.10     | 9070.57         | 1111.73               | 0          | "main"          | false          || 1933.27   || 0.00       || 0            || LocalDate.of(1987, 6, 7)
        LocalDate.of(2012, 8, 28)  | LocalDate.of(2012, 12, 8)  | null                       | true     | 6766.26     | 195.98          | 1132.46               | 0          | "pre-sessional" | false          || 10497.82  || 0.00       || 0            || LocalDate.of(2013, 1, 8)
        LocalDate.of(2007, 1, 18)  | LocalDate.of(2007, 3, 7)   | null                       | true     | 537.56      | 5530.03         | 0.00                  | 0          | "main"          | false          || 2530.00   || 0.00       || 0            || LocalDate.of(2007, 3, 14)
        LocalDate.of(1988, 4, 29)  | LocalDate.of(1988, 11, 9)  | null                       | true     | 7085.64     | 6364.68         | 650.66                | 13         | "main"          | false          || 107790.30 || 0.00       || 0            || LocalDate.of(1989, 1, 9)
        LocalDate.of(2049, 10, 1)  | LocalDate.of(2050, 4, 29)  | null                       | true     | 8949.46     | 7167.62         | 0.00                  | 8          | "main"          | false          || 71476.84  || 0.00       || 0            || LocalDate.of(2050, 6, 29)
        LocalDate.of(2012, 1, 17)  | LocalDate.of(2012, 7, 19)  | null                       | false    | 9981.24     | 2803.40         | 681.72                | 5          | "main"          | false          || 44201.12  || 0.00       || 0            || LocalDate.of(2012, 9, 19)
        LocalDate.of(2024, 10, 31) | LocalDate.of(2025, 3, 24)  | LocalDate.of(2024, 6, 14)  | false    | 9405.71     | 4456.38         | 267.75                | 12         | "main"          | false          || 66876.58  || 0.00       || 0            || LocalDate.of(2025, 5, 24)
        LocalDate.of(2032, 2, 14)  | LocalDate.of(2033, 3, 7)   | null                       | false    | 8786.88     | 8253.29         | 822.19                | 9          | "pre-sessional" | false          || 63926.40  || 0.00       || 9            || LocalDate.of(2033, 7, 7)
        LocalDate.of(1995, 12, 4)  | LocalDate.of(1996, 1, 23)  | null                       | true     | 5276.94     | 4182.18         | 1202.85               | 0          | "main"          | false          || 2421.91   || 0.00       || 0            || LocalDate.of(1996, 1, 30)
        LocalDate.of(1973, 7, 22)  | LocalDate.of(1974, 8, 14)  | null                       | false    | 8986.28     | 5824.84         | 570.38                | 3          | "main"          | false          || 30086.06  || 0.00       || 9            || LocalDate.of(1974, 12, 14)
        LocalDate.of(1979, 3, 30)  | LocalDate.of(1979, 5, 10)  | LocalDate.of(1978, 7, 31)  | true     | 3538.49     | 1209.49         | 0.00                  | 5          | "main"          | false          || 21759.00  || 0.00       || 0            || LocalDate.of(1979, 7, 10)
        LocalDate.of(2007, 10, 20) | LocalDate.of(2008, 1, 4)   | null                       | false    | 7786.46     | 7257.73         | 1038.02               | 0          | "main"          | false          || 2535.71   || 0.00       || 0            || LocalDate.of(2008, 1, 11)
        LocalDate.of(2026, 3, 10)  | LocalDate.of(2027, 1, 31)  | LocalDate.of(2025, 6, 30)  | true     | 5132.24     | 7849.22         | 990.84                | 3          | "main"          | false          || 33209.16  || 0.00       || 9            || LocalDate.of(2027, 5, 31)
        LocalDate.of(1981, 12, 27) | LocalDate.of(1982, 11, 14) | null                       | true     | 133.22      | 5390.41         | 0.00                  | 13         | "main"          | false          || 110250.00 || 0.00       || 9            || LocalDate.of(1983, 1, 14)
        LocalDate.of(2027, 5, 5)   | LocalDate.of(2028, 4, 5)   | LocalDate.of(2026, 6, 19)  | false    | 2632.54     | 9588.97         | 0.00                  | 10         | "main"          | false          || 70335.00  || 0.00       || 9            || LocalDate.of(2028, 8, 5)
        LocalDate.of(1984, 10, 18) | LocalDate.of(1985, 9, 13)  | LocalDate.of(1984, 9, 21)  | false    | 8870.49     | 9330.71         | 0.00                  | 7          | "main"          | false          || 51975.00  || 0.00       || 9            || LocalDate.of(1985, 11, 13)
        LocalDate.of(2010, 2, 1)   | LocalDate.of(2010, 5, 27)  | LocalDate.of(2009, 8, 28)  | false    | 8763.68     | 1145.64         | 27.71                 | 4          | "main"          | false          || 27970.33  || 0.00       || 0            || LocalDate.of(2010, 7, 27)
        LocalDate.of(1994, 11, 17) | LocalDate.of(1995, 8, 25)  | LocalDate.of(1993, 11, 24) | false    | 9675.73     | 361.04          | 560.56                | 1          | "main"          | false          || 24009.13  || 0.00       || 9            || LocalDate.of(1995, 12, 25)
        LocalDate.of(1978, 7, 8)   | LocalDate.of(1978, 10, 18) | LocalDate.of(1977, 12, 24) | false    | 4473.44     | 870.96          | 1011.23               | 3          | "main"          | false          || 18891.25  || 0.00       || 0            || LocalDate.of(1978, 12, 18)
    }

    @Unroll
    def "Tier 4 General - Check 'Accommodation Fees paid'"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2043, 10, 13) | LocalDate.of(2043, 11, 8)  | null                       | false    | 2862.94     | 0.00            | 450.15                | 0          | "main"          | false          || 3427.79   || 0.00       || 0            || LocalDate.of(2043, 11, 15)
        LocalDate.of(1989, 6, 18)  | LocalDate.of(1989, 9, 30)  | LocalDate.of(1988, 5, 30)  | false    | 724.91      | 5520.12         | 864.66                | 11         | "main"          | false          || 63035.34  || 0.00       || 0            || LocalDate.of(1990, 1, 30)
        LocalDate.of(2022, 10, 28) | LocalDate.of(2022, 12, 30) | null                       | true     | 9056.73     | 0.00            | 1953.22               | 0          | "pre-sessional" | false          || 11586.73  || 1265.00    || 0            || LocalDate.of(2023, 1, 30)
        LocalDate.of(2039, 5, 10)  | LocalDate.of(2039, 5, 25)  | LocalDate.of(2038, 5, 30)  | true     | 4340.34     | 3439.29         | 408.84                | 11         | "main"          | false          || 29642.21  || 0.00       || 0            || LocalDate.of(2039, 7, 25)
        LocalDate.of(2042, 5, 1)   | LocalDate.of(2043, 5, 2)   | LocalDate.of(2041, 12, 1)  | true     | 1320.06     | 1829.30         | 237.24                | 11         | "main"          | false          || 94802.76  || 0.00       || 9            || LocalDate.of(2043, 9, 2)
        LocalDate.of(1992, 9, 27)  | LocalDate.of(1992, 10, 21) | null                       | true     | 5409.69     | 0.00            | 83.87                 | 0          | "main"          | false          || 6590.82   || 0.00       || 0            || LocalDate.of(1992, 10, 28)
        LocalDate.of(2007, 8, 28)  | LocalDate.of(2008, 8, 9)   | LocalDate.of(2007, 3, 13)  | true     | 398.68      | 0.00            | 1124.52               | 5          | "main"          | false          || 48684.16  || 0.00       || 9            || LocalDate.of(2008, 12, 9)
        LocalDate.of(1990, 1, 12)  | LocalDate.of(1990, 5, 1)   | null                       | true     | 1844.61     | 0.00            | 104.62                | 0          | "pre-sessional" | false          || 6799.99   || 0.00       || 0            || LocalDate.of(1990, 6, 1)
        LocalDate.of(2001, 6, 29)  | LocalDate.of(2001, 7, 13)  | null                       | false    | 2675.96     | 0.00            | 169.03                | 0          | "pre-sessional" | false          || 3521.93   || 0.00       || 0            || LocalDate.of(2001, 8, 13)
        LocalDate.of(2025, 12, 24) | LocalDate.of(2026, 8, 27)  | null                       | true     | 2324.52     | 4156.78         | 791.83                | 5          | "pre-sessional" | false          || 48618.17  || 0.00       || 0            || LocalDate.of(2026, 10, 27)
        LocalDate.of(2014, 9, 29)  | LocalDate.of(2015, 9, 17)  | LocalDate.of(2014, 3, 19)  | true     | 4148.97     | 0.00            | 501.61                | 0          | "main"          | false          || 15032.36  || 0.00       || 9            || LocalDate.of(2016, 1, 17)
        LocalDate.of(2047, 10, 10) | LocalDate.of(2048, 7, 12)  | LocalDate.of(2046, 12, 28) | true     | 1738.37     | 0.00            | 1297.74               | 13         | "main"          | false          || 110723.37 || 1265.00    || 9            || LocalDate.of(2048, 11, 12)
        LocalDate.of(2053, 3, 13)  | LocalDate.of(2053, 5, 21)  | LocalDate.of(2052, 4, 30)  | false    | 3571.35     | 0.00            | 1879.72               | 6          | "main"          | false          || 33911.35  || 1265.00    || 0            || LocalDate.of(2053, 9, 21)
        LocalDate.of(2022, 4, 30)  | LocalDate.of(2023, 2, 28)  | LocalDate.of(2022, 2, 11)  | false    | 5569.67     | 0.00            | 936.86                | 8          | "main"          | false          || 62727.81  || 0.00       || 9            || LocalDate.of(2023, 6, 28)
        LocalDate.of(2038, 7, 1)   | LocalDate.of(2039, 5, 17)  | LocalDate.of(2037, 12, 15) | true     | 5866.86     | 1442.53         | 201.12                | 1          | "main"          | false          || 23213.21  || 0.00       || 9            || LocalDate.of(2039, 9, 17)
        LocalDate.of(2051, 7, 15)  | LocalDate.of(2052, 1, 1)   | null                       | true     | 4199.44     | 0.00            | 988.80                | 0          | "pre-sessional" | false          || 10800.64  || 0.00       || 0            || LocalDate.of(2052, 2, 1)
        LocalDate.of(1996, 3, 17)  | LocalDate.of(1996, 8, 31)  | null                       | true     | 6802.68     | 6401.49         | 1996.11               | 0          | "pre-sessional" | false          || 6726.19   || 1265.00    || 0            || LocalDate.of(1996, 9, 30)
        LocalDate.of(2022, 1, 5)   | LocalDate.of(2022, 6, 27)  | LocalDate.of(2021, 8, 14)  | true     | 226.40      | 0.00            | 1721.30               | 7          | "main"          | false          || 53871.40  || 1265.00    || 0            || LocalDate.of(2022, 8, 27)
        LocalDate.of(2041, 11, 1)  | LocalDate.of(2042, 3, 22)  | null                       | false    | 6133.73     | 0.00            | 1468.85               | 0          | "pre-sessional" | false          || 9943.73   || 1265.00    || 0            || LocalDate.of(2042, 4, 22)
        LocalDate.of(2011, 5, 4)   | LocalDate.of(2011, 9, 29)  | null                       | true     | 7972.09     | 6855.59         | 1602.29               | 0          | "pre-sessional" | false          || 6176.50   || 1265.00    || 0            || LocalDate.of(2011, 10, 29)
    }

    @Unroll
    def "Tier 4 General - Check 'Continuation courses'"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1992, 5, 9)   | LocalDate.of(1992, 11, 16) | LocalDate.of(1991, 6, 26)  | false    | 0.00        | 0.00            | 0.00                  | 14         | "main"     | false          || 92785.00  || 0.00       || 0            || LocalDate.of(1993, 3, 16)
        LocalDate.of(2003, 4, 5)   | LocalDate.of(2004, 5, 6)   | LocalDate.of(2002, 6, 8)   | true     | 0.00        | 0.00            | 0.00                  | 6          | "main"     | false          || 57015.00  || 0.00       || 9            || LocalDate.of(2004, 9, 6)
        LocalDate.of(1992, 8, 7)   | LocalDate.of(1993, 4, 1)   | LocalDate.of(1992, 5, 30)  | false    | 0.00        | 0.00            | 0.00                  | 14         | "main"     | false          || 93800.00  || 0.00       || 0            || LocalDate.of(1993, 6, 1)
        LocalDate.of(1983, 10, 25) | LocalDate.of(1984, 10, 30) | LocalDate.of(1983, 1, 25)  | false    | 0.00        | 0.00            | 0.00                  | 6          | "main"     | false          || 45855.00  || 0.00       || 9            || LocalDate.of(1985, 2, 28)
        LocalDate.of(1979, 10, 4)  | LocalDate.of(1980, 8, 1)   | LocalDate.of(1978, 10, 18) | false    | 0.00        | 0.00            | 0.00                  | 12         | "main"     | false          || 82575.00  || 0.00       || 9            || LocalDate.of(1980, 12, 1)
        LocalDate.of(2020, 10, 14) | LocalDate.of(2021, 10, 8)  | LocalDate.of(2020, 7, 5)   | false    | 0.00        | 0.00            | 0.00                  | 4          | "main"     | false          || 33615.00  || 0.00       || 9            || LocalDate.of(2022, 2, 8)
        LocalDate.of(2025, 11, 19) | LocalDate.of(2026, 10, 1)  | LocalDate.of(2024, 12, 17) | true     | 0.00        | 0.00            | 0.00                  | 12         | "main"     | false          || 102645.00 || 0.00       || 9            || LocalDate.of(2027, 2, 1)
        LocalDate.of(2020, 7, 8)   | LocalDate.of(2021, 6, 7)   | LocalDate.of(2020, 2, 29)  | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"     | false          || 49410.00  || 0.00       || 9            || LocalDate.of(2021, 10, 7)
        LocalDate.of(2032, 6, 4)   | LocalDate.of(2033, 5, 8)   | LocalDate.of(2031, 8, 28)  | true     | 0.00        | 0.00            | 0.00                  | 4          | "main"     | false          || 41805.00  || 0.00       || 9            || LocalDate.of(2033, 9, 8)
        LocalDate.of(1996, 2, 22)  | LocalDate.of(1996, 4, 26)  | LocalDate.of(1996, 1, 13)  | true     | 0.00        | 0.00            | 0.00                  | 0          | "main"     | false          || 3795.00   || 0.00       || 0            || LocalDate.of(1996, 5, 3)
        LocalDate.of(2002, 8, 3)   | LocalDate.of(2002, 11, 5)  | LocalDate.of(2002, 5, 2)   | false    | 0.00        | 0.00            | 0.00                  | 4          | "main"     | false          || 20380.00  || 0.00       || 0            || LocalDate.of(2003, 1, 5)
        LocalDate.of(1974, 11, 20) | LocalDate.of(1975, 12, 20) | LocalDate.of(1974, 6, 25)  | false    | 0.00        | 0.00            | 0.00                  | 9          | "main"     | false          || 64215.00  || 0.00       || 9            || LocalDate.of(1976, 4, 20)
        LocalDate.of(1985, 11, 23) | LocalDate.of(1986, 12, 16) | LocalDate.of(1985, 4, 27)  | false    | 0.00        | 0.00            | 0.00                  | 7          | "main"     | false          || 51975.00  || 0.00       || 9            || LocalDate.of(1987, 4, 16)
        LocalDate.of(2025, 4, 2)   | LocalDate.of(2025, 11, 8)  | LocalDate.of(2024, 11, 8)  | false    | 0.00        | 0.00            | 0.00                  | 6          | "main"     | false          || 44840.00  || 0.00       || 0            || LocalDate.of(2026, 3, 8)
        LocalDate.of(2052, 10, 6)  | LocalDate.of(2052, 11, 17) | LocalDate.of(2052, 5, 4)   | true     | 0.00        | 0.00            | 0.00                  | 12         | "main"     | false          || 43090.00  || 0.00       || 0            || LocalDate.of(2053, 1, 17)
        LocalDate.of(2035, 5, 8)   | LocalDate.of(2036, 2, 20)  | LocalDate.of(2034, 10, 8)  | false    | 0.00        | 0.00            | 0.00                  | 3          | "main"     | false          || 27495.00  || 0.00       || 9            || LocalDate.of(2036, 6, 20)
        LocalDate.of(1989, 11, 7)  | LocalDate.of(1990, 1, 7)   | LocalDate.of(1989, 9, 6)   | false    | 0.00        | 0.00            | 0.00                  | 3          | "main"     | false          || 9165.00   || 0.00       || 0            || LocalDate.of(1990, 1, 14)
        LocalDate.of(1979, 12, 24) | LocalDate.of(1980, 5, 11)  | LocalDate.of(1978, 11, 26) | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"     | false          || 44350.00  || 0.00       || 0            || LocalDate.of(1980, 9, 11)
        LocalDate.of(2040, 10, 13) | LocalDate.of(2040, 10, 28) | LocalDate.of(2040, 5, 10)  | true     | 0.00        | 0.00            | 0.00                  | 3          | "main"     | false          || 3800.00   || 0.00       || 0            || LocalDate.of(2040, 11, 4)
        LocalDate.of(2014, 4, 18)  | LocalDate.of(2015, 2, 16)  | LocalDate.of(2013, 12, 15) | true     | 0.00        | 0.00            | 0.00                  | 8          | "main"     | false          || 72225.00  || 0.00       || 9            || LocalDate.of(2015, 6, 16)
    }

    // Dependants only

    @Unroll
    def "Tier 4 General - Check 'Non Inner London Borough' dependants only"() {

        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1974, 2, 5)   | LocalDate.of(1975, 2, 1)   | LocalDate.of(1973, 9, 17)  | false    | 0.00        | 0.00            | 0.00                  | 5          | "main"          | true           || 30600.00  || 0.00       || 9            || LocalDate.of(1975, 6, 1)
        LocalDate.of(2003, 7, 26)  | LocalDate.of(2004, 6, 29)  | LocalDate.of(2002, 9, 27)  | false    | 0.00        | 0.00            | 0.00                  | 1          | "main"          | true           || 6120.00   || 0.00       || 9            || LocalDate.of(2004, 10, 29)
        LocalDate.of(2041, 5, 24)  | LocalDate.of(2041, 8, 22)  | null                       | false    | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2041, 9, 22)
        LocalDate.of(1985, 5, 14)  | LocalDate.of(1985, 8, 27)  | LocalDate.of(1985, 2, 4)   | false    | 0.00        | 0.00            | 0.00                  | 1          | "main"          | true           || 4080.00   || 0.00       || 0            || LocalDate.of(1985, 10, 27)
        LocalDate.of(2051, 5, 30)  | LocalDate.of(2052, 2, 24)  | null                       | false    | 0.00        | 0.00            | 0.00                  | 8          | "main"          | true           || 48960.00  || 0.00       || 0            || LocalDate.of(2052, 4, 24)
        LocalDate.of(2037, 2, 10)  | LocalDate.of(2037, 10, 11) | LocalDate.of(2036, 5, 21)  | false    | 0.00        | 0.00            | 0.00                  | 4          | "main"          | true           || 24480.00  || 0.00       || 0            || LocalDate.of(2038, 2, 11)
        LocalDate.of(2022, 5, 13)  | LocalDate.of(2022, 6, 6)   | null                       | false    | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2022, 7, 6)
        LocalDate.of(2046, 6, 11)  | LocalDate.of(2047, 1, 24)  | LocalDate.of(2045, 10, 20) | false    | 0.00        | 0.00            | 0.00                  | 9          | "main"          | true           || 55080.00  || 0.00       || 0            || LocalDate.of(2047, 5, 24)
        LocalDate.of(1996, 1, 27)  | LocalDate.of(1996, 3, 29)  | null                       | false    | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1996, 4, 29)
        LocalDate.of(2051, 11, 24) | LocalDate.of(2052, 8, 28)  | LocalDate.of(2051, 6, 23)  | false    | 0.00        | 0.00            | 0.00                  | 3          | "main"          | true           || 18360.00  || 0.00       || 9            || LocalDate.of(2052, 12, 28)
        LocalDate.of(2018, 10, 5)  | LocalDate.of(2019, 7, 3)   | null                       | false    | 0.00        | 0.00            | 0.00                  | 7          | "main"          | true           || 42840.00  || 0.00       || 0            || LocalDate.of(2019, 9, 3)
        LocalDate.of(2023, 2, 15)  | LocalDate.of(2024, 3, 5)   | LocalDate.of(2022, 3, 5)   | false    | 0.00        | 0.00            | 0.00                  | 6          | "main"          | true           || 36720.00  || 0.00       || 9            || LocalDate.of(2024, 7, 5)
        LocalDate.of(1974, 4, 20)  | LocalDate.of(1975, 5, 12)  | null                       | false    | 0.00        | 0.00            | 0.00                  | 3          | "pre-sessional" | true           || 18360.00  || 0.00       || 9            || LocalDate.of(1975, 9, 12)
        LocalDate.of(1992, 8, 20)  | LocalDate.of(1993, 9, 21)  | null                       | false    | 0.00        | 0.00            | 0.00                  | 9          | "pre-sessional" | true           || 55080.00  || 0.00       || 9            || LocalDate.of(1994, 1, 21)
        LocalDate.of(2041, 9, 7)   | LocalDate.of(2041, 12, 29) | LocalDate.of(2040, 9, 26)  | false    | 0.00        | 0.00            | 0.00                  | 11         | "main"          | true           || 59840.00  || 0.00       || 0            || LocalDate.of(2042, 4, 29)
        LocalDate.of(2017, 12, 31) | LocalDate.of(2018, 9, 14)  | null                       | false    | 0.00        | 0.00            | 0.00                  | 1          | "main"          | true           || 6120.00   || 0.00       || 0            || LocalDate.of(2018, 11, 14)
        LocalDate.of(2016, 2, 7)   | LocalDate.of(2016, 9, 5)   | LocalDate.of(2015, 9, 27)  | false    | 0.00        | 0.00            | 0.00                  | 13         | "main"          | true           || 79560.00  || 0.00       || 0            || LocalDate.of(2016, 11, 5)
        LocalDate.of(2017, 1, 3)   | LocalDate.of(2017, 8, 29)  | null                       | false    | 0.00        | 0.00            | 0.00                  | 3          | "pre-sessional" | true           || 18360.00  || 0.00       || 0            || LocalDate.of(2017, 10, 29)
        LocalDate.of(1993, 4, 9)   | LocalDate.of(1993, 5, 24)  | LocalDate.of(1992, 8, 20)  | false    | 0.00        | 0.00            | 0.00                  | 11         | "main"          | true           || 29920.00  || 0.00       || 0            || LocalDate.of(1993, 7, 24)
        LocalDate.of(2040, 7, 16)  | LocalDate.of(2040, 8, 21)  | LocalDate.of(2039, 11, 16) | false    | 0.00        | 0.00            | 0.00                  | 10         | "main"          | true           || 27200.00  || 0.00       || 0            || LocalDate.of(2040, 10, 21)
    }

    @Unroll
    def "Tier 4 General - Check 'Inner London Borough' dependants only"() {

        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1988, 3, 27)  | LocalDate.of(1988, 7, 28)  | LocalDate.of(1987, 5, 30)  | true     | 0.00        | 0.00            | 0.00                  | 6          | "main"          | true           || 45630.00  || 0.00       || 0            || LocalDate.of(1988, 11, 28)
        LocalDate.of(2028, 1, 19)  | LocalDate.of(2028, 8, 4)   | null                       | true     | 0.00        | 0.00            | 0.00                  | 5          | "pre-sessional" | true           || 38025.00  || 0.00       || 0            || LocalDate.of(2028, 10, 4)
        LocalDate.of(2033, 8, 24)  | LocalDate.of(2033, 11, 14) | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2033, 11, 21)
        LocalDate.of(2041, 5, 11)  | LocalDate.of(2041, 10, 3)  | LocalDate.of(2040, 8, 28)  | true     | 0.00        | 0.00            | 0.00                  | 10         | "main"          | true           || 76050.00  || 0.00       || 0            || LocalDate.of(2042, 2, 3)
        LocalDate.of(2042, 3, 28)  | LocalDate.of(2042, 6, 13)  | LocalDate.of(2042, 3, 7)   | true     | 0.00        | 0.00            | 0.00                  | 12         | "main"          | true           || 30420.00  || 0.00       || 0            || LocalDate.of(2042, 6, 20)
        LocalDate.of(2053, 2, 16)  | LocalDate.of(2053, 4, 6)   | LocalDate.of(2052, 7, 4)   | true     | 0.00        | 0.00            | 0.00                  | 13         | "main"          | true           || 43940.00  || 0.00       || 0            || LocalDate.of(2053, 6, 6)
        LocalDate.of(1988, 9, 3)   | LocalDate.of(1988, 10, 11) | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(1988, 10, 18)
        LocalDate.of(2014, 12, 30) | LocalDate.of(2015, 10, 22) | LocalDate.of(2014, 5, 14)  | true     | 0.00        | 0.00            | 0.00                  | 6          | "main"          | true           || 45630.00  || 0.00       || 9            || LocalDate.of(2016, 2, 22)
        LocalDate.of(2000, 7, 15)  | LocalDate.of(2001, 6, 20)  | LocalDate.of(1999, 7, 8)   | true     | 0.00        | 0.00            | 0.00                  | 8          | "main"          | true           || 60840.00  || 0.00       || 9            || LocalDate.of(2001, 10, 20)
        LocalDate.of(1976, 6, 7)   | LocalDate.of(1977, 4, 29)  | LocalDate.of(1976, 4, 15)  | true     | 0.00        | 0.00            | 0.00                  | 3          | "main"          | true           || 22815.00  || 0.00       || 9            || LocalDate.of(1977, 8, 29)
        LocalDate.of(2004, 6, 29)  | LocalDate.of(2004, 11, 2)  | LocalDate.of(2003, 7, 6)   | true     | 0.00        | 0.00            | 0.00                  | 1          | "main"          | true           || 7605.00   || 0.00       || 0            || LocalDate.of(2005, 3, 2)
        LocalDate.of(2036, 7, 9)   | LocalDate.of(2036, 10, 18) | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2036, 10, 25)
        LocalDate.of(1991, 8, 13)  | LocalDate.of(1991, 8, 14)  | LocalDate.of(1991, 5, 28)  | true     | 0.00        | 0.00            | 0.00                  | 8          | "main"          | true           || 6760.00   || 0.00       || 0            || LocalDate.of(1991, 8, 21)
        LocalDate.of(2042, 3, 29)  | LocalDate.of(2042, 9, 18)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2042, 10, 18)
        LocalDate.of(1986, 10, 3)  | LocalDate.of(1987, 3, 31)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1987, 4, 30)
        LocalDate.of(1997, 7, 24)  | LocalDate.of(1998, 6, 11)  | LocalDate.of(1996, 11, 27) | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"          | true           || 38025.00  || 0.00       || 9            || LocalDate.of(1998, 10, 11)
        LocalDate.of(2010, 1, 18)  | LocalDate.of(2010, 8, 24)  | LocalDate.of(2009, 10, 5)  | true     | 0.00        | 0.00            | 0.00                  | 12         | "main"          | true           || 91260.00  || 0.00       || 0            || LocalDate.of(2010, 10, 24)
        LocalDate.of(2009, 12, 26) | LocalDate.of(2010, 8, 15)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 12         | "main"          | true           || 91260.00  || 0.00       || 0            || LocalDate.of(2010, 10, 15)
        LocalDate.of(2034, 2, 22)  | LocalDate.of(2034, 11, 1)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 14         | "pre-sessional" | true           || 106470.00 || 0.00       || 0            || LocalDate.of(2035, 1, 1)
        LocalDate.of(2014, 1, 2)   | LocalDate.of(2015, 1, 18)  | LocalDate.of(2013, 5, 8)   | true     | 0.00        | 0.00            | 0.00                  | 2          | "main"          | true           || 15210.00  || 0.00       || 9            || LocalDate.of(2015, 5, 18)
    }


    @Unroll
    def "Tier 4 General - Check 'Continuation courses' dependants only"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly)
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        assert jsonContent.threshold == threshold
        assert jsonContent.leaveEndDate == leaveToRemain.toString()

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(2031, 4, 25)  | LocalDate.of(2032, 5, 18)  | LocalDate.of(2031, 1, 6)   | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"     | true           || 38025.00  || 0.00       || 9            || LocalDate.of(2032, 9, 18)
        LocalDate.of(1994, 12, 13) | LocalDate.of(1995, 7, 7)   | LocalDate.of(1994, 2, 22)  | false    | 0.00        | 0.00            | 0.00                  | 13         | "main"     | true           || 79560.00  || 0.00       || 0            || LocalDate.of(1995, 11, 7)
        LocalDate.of(1988, 2, 14)  | LocalDate.of(1988, 7, 18)  | LocalDate.of(1987, 4, 27)  | false    | 0.00        | 0.00            | 0.00                  | 14         | "main"     | true           || 85680.00  || 0.00       || 0            || LocalDate.of(1988, 11, 18)
        LocalDate.of(2051, 1, 24)  | LocalDate.of(2051, 6, 8)   | LocalDate.of(2049, 12, 25) | true     | 0.00        | 0.00            | 0.00                  | 8          | "main"     | true           || 60840.00  || 0.00       || 0            || LocalDate.of(2051, 10, 8)
        LocalDate.of(2043, 1, 17)  | LocalDate.of(2044, 2, 18)  | LocalDate.of(2042, 7, 4)   | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"     | true           || 38025.00  || 0.00       || 9            || LocalDate.of(2044, 6, 18)
        LocalDate.of(2024, 7, 1)   | LocalDate.of(2025, 7, 25)  | LocalDate.of(2023, 8, 3)   | false    | 0.00        | 0.00            | 0.00                  | 12         | "main"     | true           || 73440.00  || 0.00       || 9            || LocalDate.of(2025, 11, 25)
        LocalDate.of(1993, 7, 29)  | LocalDate.of(1993, 11, 17) | LocalDate.of(1992, 11, 15) | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"     | true           || 0.00      || 0.00       || 0            || LocalDate.of(1994, 3, 17)
        LocalDate.of(1983, 1, 24)  | LocalDate.of(1983, 5, 8)   | LocalDate.of(1982, 7, 21)  | true     | 0.00        | 0.00            | 0.00                  | 10         | "main"     | true           || 50700.00  || 0.00       || 0            || LocalDate.of(1983, 7, 8)
        LocalDate.of(2024, 12, 6)  | LocalDate.of(2025, 12, 9)  | LocalDate.of(2024, 2, 9)   | false    | 0.00        | 0.00            | 0.00                  | 6          | "main"     | true           || 36720.00  || 0.00       || 9            || LocalDate.of(2026, 4, 9)
        LocalDate.of(1994, 12, 6)  | LocalDate.of(1995, 12, 14) | LocalDate.of(1993, 12, 19) | true     | 0.00        | 0.00            | 0.00                  | 1          | "main"     | true           || 7605.00   || 0.00       || 9            || LocalDate.of(1996, 4, 14)
        LocalDate.of(2001, 1, 5)   | LocalDate.of(2001, 3, 19)  | LocalDate.of(2000, 4, 28)  | true     | 0.00        | 0.00            | 0.00                  | 7          | "main"     | true           || 29575.00  || 0.00       || 0            || LocalDate.of(2001, 5, 19)
        LocalDate.of(2017, 1, 15)  | LocalDate.of(2017, 7, 17)  | LocalDate.of(2016, 11, 29) | false    | 0.00        | 0.00            | 0.00                  | 9          | "main"     | true           || 55080.00  || 0.00       || 0            || LocalDate.of(2017, 9, 17)
        LocalDate.of(1997, 12, 26) | LocalDate.of(1998, 2, 23)  | LocalDate.of(1997, 10, 13) | true     | 0.00        | 0.00            | 0.00                  | 10         | "main"     | true           || 25350.00  || 0.00       || 0            || LocalDate.of(1998, 3, 2)
        LocalDate.of(1994, 12, 12) | LocalDate.of(1995, 2, 14)  | LocalDate.of(1994, 5, 16)  | true     | 0.00        | 0.00            | 0.00                  | 8          | "main"     | true           || 33800.00  || 0.00       || 0            || LocalDate.of(1995, 4, 14)
        LocalDate.of(1997, 3, 20)  | LocalDate.of(1997, 10, 22) | LocalDate.of(1996, 11, 21) | false    | 0.00        | 0.00            | 0.00                  | 2          | "main"     | true           || 12240.00  || 0.00       || 0            || LocalDate.of(1997, 12, 22)
        LocalDate.of(2053, 5, 4)   | LocalDate.of(2054, 2, 14)  | LocalDate.of(2053, 1, 18)  | false    | 0.00        | 0.00            | 0.00                  | 4          | "main"     | true           || 24480.00  || 0.00       || 9            || LocalDate.of(2054, 6, 14)
        LocalDate.of(1999, 1, 5)   | LocalDate.of(1999, 5, 23)  | LocalDate.of(1997, 12, 27) | false    | 0.00        | 0.00            | 0.00                  | 7          | "main"     | true           || 42840.00  || 0.00       || 0            || LocalDate.of(1999, 9, 23)
        LocalDate.of(2024, 2, 15)  | LocalDate.of(2024, 11, 30) | LocalDate.of(2023, 2, 28)  | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"     | true           || 0.00      || 0.00       || 9            || LocalDate.of(2025, 3, 30)
        LocalDate.of(1993, 1, 21)  | LocalDate.of(1993, 10, 21) | LocalDate.of(1992, 2, 19)  | true     | 0.00        | 0.00            | 0.00                  | 1          | "main"     | true           || 7605.00   || 0.00       || 9            || LocalDate.of(1994, 2, 21)
        LocalDate.of(2014, 12, 8)  | LocalDate.of(2015, 11, 30) | LocalDate.of(2014, 6, 2)   | true     | 0.00        | 0.00            | 0.00                  | 5          | "main"     | true           || 38025.00  || 0.00       || 9            || LocalDate.of(2016, 3, 30)
    }

    // All variants

    @Unroll
    def "Tier 4 General - Check 'All variants'"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, dependantsOnly)
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
            assert jsonContent.cappedValues == null
        }

        where:
        courseStartDate            | courseEndDate              | originalCourseStartDate    | inLondon | tuitionFees | tuitionFeesPaid | accommodationFeesPaid | dependants | courseType      | dependantsOnly || threshold || feesCapped || courseCapped || leaveToRemain
        LocalDate.of(1991, 2, 1)   | LocalDate.of(1991, 12, 28) | null                       | true     | 1607.48     | 7509.95         | 1876.68               | 10         | "pre-sessional" | false          || 86170.00  || 1265.00    || 9            || LocalDate.of(1992, 2, 28)
        LocalDate.of(2005, 10, 27) | LocalDate.of(2006, 2, 6)   | null                       | true     | 9362.30     | 289.61          | 1750.41               | 0          | "pre-sessional" | false          || 12867.69  || 1265.00    || 0            || LocalDate.of(2006, 3, 6)
        LocalDate.of(2033, 8, 25)  | LocalDate.of(2033, 11, 6)  | LocalDate.of(2033, 4, 22)  | true     | 7347.45     | 2514.74         | 866.06                | 3          | "main"          | true           || 12675.00  || 0.00       || 0            || LocalDate.of(2034, 1, 6)
        LocalDate.of(2044, 12, 3)  | LocalDate.of(2045, 9, 26)  | null                       | false    | 6839.98     | 0.00            | 1679.11               | 11         | "main"          | true           || 67320.00  || 1265.00    || 9            || LocalDate.of(2045, 11, 26)
        LocalDate.of(1985, 5, 22)  | LocalDate.of(1986, 1, 31)  | LocalDate.of(1984, 12, 30) | true     | 289.42      | 16.09           | 789.18                | 14         | "main"          | false          || 117339.15 || 0.00       || 0            || LocalDate.of(1986, 5, 31)
        LocalDate.of(2052, 11, 5)  | LocalDate.of(2053, 10, 24) | LocalDate.of(2052, 8, 10)  | true     | 2256.10     | 1510.24         | 0.00                  | 7          | "main"          | true           || 53235.00  || 0.00       || 9            || LocalDate.of(2054, 2, 24)
        LocalDate.of(2024, 11, 26) | LocalDate.of(2025, 5, 9)   | null                       | true     | 9739.70     | 8689.48         | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2025, 6, 9)
        LocalDate.of(2045, 9, 22)  | LocalDate.of(2046, 8, 2)   | LocalDate.of(2045, 1, 1)   | false    | 8190.10     | 0.00            | 1252.64               | 14         | "main"          | false          || 101752.46 || 0.00       || 9            || LocalDate.of(2046, 12, 2)
        LocalDate.of(1985, 3, 4)   | LocalDate.of(1985, 12, 15) | LocalDate.of(1984, 8, 6)   | true     | 4163.94     | 0.00            | 0.00                  | 10         | "main"          | true           || 76050.00  || 0.00       || 9            || LocalDate.of(1986, 4, 15)
        LocalDate.of(2038, 4, 27)  | LocalDate.of(2039, 2, 26)  | null                       | true     | 4371.29     | 2790.38         | 920.40                | 1          | "pre-sessional" | false          || 19650.51  || 0.00       || 9            || LocalDate.of(2039, 4, 26)
        LocalDate.of(2044, 10, 27) | LocalDate.of(2044, 11, 15) | null                       | true     | 3816.40     | 8097.13         | 1819.39               | 0          | "main"          | true           || 0.00      || 1265.00    || 0            || LocalDate.of(2044, 11, 22)
        LocalDate.of(2007, 2, 20)  | LocalDate.of(2007, 4, 2)   | LocalDate.of(2006, 6, 23)  | false    | 922.70      | 0.00            | 0.00                  | 8          | "main"          | false          || 24712.70  || 0.00       || 0            || LocalDate.of(2007, 6, 2)
        LocalDate.of(1990, 8, 5)   | LocalDate.of(1991, 3, 11)  | null                       | false    | 6987.87     | 5943.18         | 1224.72               | 12         | "main"          | true           || 73440.00  || 0.00       || 0            || LocalDate.of(1991, 5, 11)
        LocalDate.of(1974, 5, 20)  | LocalDate.of(1975, 4, 19)  | LocalDate.of(1974, 4, 19)  | false    | 9931.33     | 7458.93         | 805.18                | 8          | "main"          | false          || 59762.22  || 0.00       || 9            || LocalDate.of(1975, 8, 19)
        LocalDate.of(1979, 4, 15)  | LocalDate.of(1979, 8, 21)  | null                       | true     | 1176.97     | 9326.60         | 490.65                | 0          | "pre-sessional" | false          || 5834.35   || 0.00       || 0            || LocalDate.of(1979, 9, 21)
        LocalDate.of(1979, 7, 10)  | LocalDate.of(1979, 8, 30)  | LocalDate.of(1979, 4, 2)   | true     | 9147.27     | 0.00            | 0.00                  | 3          | "main"          | true           || 5070.00   || 0.00       || 0            || LocalDate.of(1979, 9, 6)
        LocalDate.of(2034, 8, 20)  | LocalDate.of(2035, 3, 14)  | LocalDate.of(2034, 4, 1)   | false    | 1165.36     | 0.00            | 559.33                | 3          | "main"          | true           || 18360.00  || 0.00       || 0            || LocalDate.of(2035, 5, 14)
        LocalDate.of(2043, 6, 9)   | LocalDate.of(2044, 7, 7)   | LocalDate.of(2043, 1, 31)  | false    | 2229.85     | 6305.20         | 164.01                | 14         | "main"          | true           || 85680.00  || 0.00       || 9            || LocalDate.of(2044, 11, 7)
        LocalDate.of(2003, 7, 1)   | LocalDate.of(2004, 4, 30)  | LocalDate.of(2002, 7, 15)  | true     | 9134.10     | 3265.28         | 1673.39               | 2          | "main"          | false          || 31198.82  || 1265.00    || 9            || LocalDate.of(2004, 8, 30)
        LocalDate.of(2005, 8, 19)  | LocalDate.of(2006, 5, 27)  | LocalDate.of(2004, 12, 21) | true     | 4397.70     | 0.00            | 464.69                | 7          | "main"          | true           || 53235.00  || 0.00       || 9            || LocalDate.of(2006, 9, 27)
        LocalDate.of(1973, 6, 10)  | LocalDate.of(1973, 12, 6)  | null                       | false    | 9863.17     | 0.00            | 0.00                  | 0          | "main"          | false          || 15953.17  || 0.00       || 0            || LocalDate.of(1973, 12, 13)
        LocalDate.of(2001, 4, 15)  | LocalDate.of(2001, 10, 22) | LocalDate.of(2001, 1, 9)   | true     | 3894.62     | 0.00            | 18.01                 | 9          | "main"          | false          || 81176.61  || 0.00       || 0            || LocalDate.of(2001, 12, 22)
        LocalDate.of(2052, 3, 23)  | LocalDate.of(2052, 11, 12) | null                       | false    | 9252.68     | 3995.97         | 378.03                | 5          | "pre-sessional" | false          || 43598.68  || 0.00       || 0            || LocalDate.of(2053, 1, 12)
        LocalDate.of(1977, 5, 27)  | LocalDate.of(1977, 11, 28) | null                       | false    | 1435.08     | 9386.61         | 864.97                | 4          | "pre-sessional" | true           || 24480.00  || 0.00       || 0            || LocalDate.of(1978, 1, 28)
        LocalDate.of(1990, 7, 19)  | LocalDate.of(1991, 5, 4)   | LocalDate.of(1989, 8, 31)  | true     | 6522.96     | 2260.81         | 339.86                | 2          | "main"          | true           || 15210.00  || 0.00       || 9            || LocalDate.of(1991, 9, 4)
        LocalDate.of(2022, 12, 28) | LocalDate.of(2023, 1, 12)  | null                       | true     | 7883.28     | 0.00            | 1271.21               | 0          | "pre-sessional" | false          || 7883.28   || 1265.00    || 0            || LocalDate.of(2023, 2, 12)
        LocalDate.of(2045, 8, 31)  | LocalDate.of(2046, 7, 25)  | null                       | true     | 3607.25     | 5494.30         | 0.00                  | 9          | "pre-sessional" | true           || 68445.00  || 0.00       || 9            || LocalDate.of(2046, 9, 25)
        LocalDate.of(1975, 2, 25)  | LocalDate.of(1975, 11, 29) | null                       | false    | 8909.60     | 0.00            | 1540.02               | 3          | "main"          | true           || 18360.00  || 1265.00    || 9            || LocalDate.of(1976, 1, 29)
        LocalDate.of(1999, 11, 9)  | LocalDate.of(1999, 12, 25) | null                       | true     | 7366.29     | 1811.11         | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2000, 1, 25)
        LocalDate.of(2050, 4, 4)   | LocalDate.of(2050, 6, 7)   | null                       | false    | 6829.10     | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2050, 6, 14)
        LocalDate.of(2005, 11, 10) | LocalDate.of(2006, 2, 16)  | LocalDate.of(2005, 10, 26) | false    | 4470.94     | 0.00            | 1972.00               | 10         | "main"          | false          || 34465.94  || 1265.00    || 0            || LocalDate.of(2006, 2, 23)
        LocalDate.of(2028, 1, 1)   | LocalDate.of(2028, 3, 16)  | LocalDate.of(2027, 7, 2)   | true     | 526.55      | 0.00            | 1682.59               | 12         | "main"          | true           || 50700.00  || 1265.00    || 0            || LocalDate.of(2028, 5, 16)
        LocalDate.of(1981, 12, 5)  | LocalDate.of(1982, 2, 11)  | LocalDate.of(1980, 12, 12) | false    | 9224.55     | 1826.38         | 872.28                | 3          | "main"          | true           || 14280.00  || 0.00       || 0            || LocalDate.of(1982, 6, 11)
        LocalDate.of(2025, 11, 28) | LocalDate.of(2026, 10, 26) | LocalDate.of(2025, 9, 28)  | true     | 8126.77     | 0.00            | 1554.91               | 13         | "main"          | true           || 98865.00  || 1265.00    || 9            || LocalDate.of(2027, 2, 26)
        LocalDate.of(2014, 3, 7)   | LocalDate.of(2014, 3, 14)  | null                       | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2014, 3, 21)
        LocalDate.of(2003, 9, 30)  | LocalDate.of(2003, 11, 8)  | LocalDate.of(2003, 6, 14)  | false    | 7748.60     | 6099.48         | 552.05                | 6          | "main"          | true           || 8160.00   || 0.00       || 0            || LocalDate.of(2003, 11, 15)
        LocalDate.of(1997, 1, 5)   | LocalDate.of(1997, 8, 24)  | LocalDate.of(1995, 12, 7)  | false    | 1429.54     | 4132.77         | 1384.61               | 11         | "main"          | true           || 67320.00  || 1265.00    || 0            || LocalDate.of(1997, 12, 24)
        LocalDate.of(2003, 5, 21)  | LocalDate.of(2003, 7, 25)  | LocalDate.of(2002, 9, 22)  | true     | 5064.94     | 0.00            | 0.00                  | 9          | "main"          | true           || 38025.00  || 0.00       || 0            || LocalDate.of(2003, 9, 25)
        LocalDate.of(2010, 3, 7)   | LocalDate.of(2010, 10, 23) | LocalDate.of(2009, 5, 2)   | false    | 7716.80     | 8936.71         | 0.00                  | 10         | "main"          | true           || 61200.00  || 0.00       || 0            || LocalDate.of(2011, 2, 23)
        LocalDate.of(2011, 3, 21)  | LocalDate.of(2011, 7, 24)  | LocalDate.of(2010, 11, 19) | true     | 4949.21     | 0.00            | 0.00                  | 8          | "main"          | true           || 47320.00  || 0.00       || 0            || LocalDate.of(2011, 9, 24)
        LocalDate.of(2052, 8, 7)   | LocalDate.of(2053, 8, 7)   | LocalDate.of(2052, 7, 16)  | false    | 8311.07     | 0.00            | 0.00                  | 9          | "main"          | false          || 72526.07  || 0.00       || 9            || LocalDate.of(2053, 12, 7)
        LocalDate.of(1977, 7, 10)  | LocalDate.of(1977, 12, 29) | null                       | true     | 2921.06     | 0.00            | 0.00                  | 0          | "pre-sessional" | false          || 10511.06  || 0.00       || 0            || LocalDate.of(1978, 1, 29)
        LocalDate.of(2041, 11, 4)  | LocalDate.of(2042, 4, 22)  | null                       | false    | 8227.10     | 7241.84         | 0.00                  | 0          | "main"          | false          || 7075.26   || 0.00       || 0            || LocalDate.of(2042, 4, 29)
        LocalDate.of(2054, 2, 12)  | LocalDate.of(2054, 9, 28)  | null                       | false    | 7402.71     | 6311.40         | 0.00                  | 9          | "pre-sessional" | true           || 55080.00  || 0.00       || 0            || LocalDate.of(2054, 11, 28)
        LocalDate.of(2011, 3, 20)  | LocalDate.of(2011, 11, 28) | null                       | false    | 2215.45     | 5962.99         | 1424.85               | 14         | "main"          | true           || 85680.00  || 1265.00    || 0            || LocalDate.of(2012, 1, 28)
        LocalDate.of(2035, 1, 18)  | LocalDate.of(2035, 7, 15)  | null                       | true     | 7295.27     | 3283.21         | 1032.01               | 0          | "pre-sessional" | false          || 10570.05  || 0.00       || 0            || LocalDate.of(2035, 8, 15)
        LocalDate.of(2016, 10, 12) | LocalDate.of(2016, 12, 1)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2016, 12, 8)
        LocalDate.of(2009, 12, 27) | LocalDate.of(2010, 1, 28)  | LocalDate.of(2008, 12, 2)  | true     | 1181.35     | 4998.61         | 0.00                  | 5          | "main"          | true           || 25350.00  || 0.00       || 0            || LocalDate.of(2010, 5, 28)
        LocalDate.of(2003, 12, 6)  | LocalDate.of(2004, 7, 6)   | LocalDate.of(2002, 12, 16) | false    | 7787.99     | 7344.66         | 73.78                 | 8          | "main"          | false          || 57449.55  || 0.00       || 0            || LocalDate.of(2004, 11, 6)
        LocalDate.of(1983, 9, 28)  | LocalDate.of(1983, 11, 21) | null                       | true     | 7563.65     | 0.00            | 583.52                | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(1983, 12, 21)
        LocalDate.of(1987, 7, 2)   | LocalDate.of(1987, 12, 1)  | LocalDate.of(1987, 5, 13)  | false    | 5399.42     | 4719.79         | 1091.89               | 0          | "main"          | false          || 4662.74   || 0.00       || 0            || LocalDate.of(1988, 2, 1)
        LocalDate.of(2004, 12, 21) | LocalDate.of(2005, 2, 23)  | LocalDate.of(2004, 9, 1)   | false    | 9374.68     | 0.00            | 647.80                | 1          | "main"          | false          || 13811.88  || 0.00       || 0            || LocalDate.of(2005, 3, 2)
        LocalDate.of(2049, 3, 30)  | LocalDate.of(2049, 10, 14) | null                       | false    | 5118.31     | 0.00            | 1840.12               | 14         | "main"          | true           || 85680.00  || 1265.00    || 0            || LocalDate.of(2049, 12, 14)
        LocalDate.of(2037, 11, 22) | LocalDate.of(2038, 1, 23)  | LocalDate.of(2037, 4, 4)   | false    | 4987.00     | 5984.34         | 0.00                  | 13         | "main"          | true           || 44200.00  || 0.00       || 0            || LocalDate.of(2038, 3, 23)
        LocalDate.of(2026, 11, 8)  | LocalDate.of(2027, 9, 8)   | LocalDate.of(2026, 2, 14)  | true     | 8229.34     | 8410.47         | 0.00                  | 6          | "main"          | true           || 45630.00  || 0.00       || 9            || LocalDate.of(2028, 1, 8)
        LocalDate.of(2035, 7, 2)   | LocalDate.of(2035, 12, 18) | LocalDate.of(2034, 12, 9)  | true     | 7137.91     | 0.00            | 0.00                  | 12         | "main"          | false          || 105987.91 || 0.00       || 0            || LocalDate.of(2036, 4, 18)
        LocalDate.of(2014, 3, 1)   | LocalDate.of(2014, 6, 19)  | null                       | true     | 3878.72     | 7657.47         | 88.57                 | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2014, 7, 19)
        LocalDate.of(2001, 12, 28) | LocalDate.of(2002, 10, 9)  | null                       | true     | 5700.13     | 3944.34         | 406.80                | 7          | "main"          | true           || 53235.00  || 0.00       || 9            || LocalDate.of(2002, 12, 9)
        LocalDate.of(1988, 10, 11) | LocalDate.of(1989, 8, 28)  | LocalDate.of(1987, 10, 9)  | false    | 3613.19     | 2122.87         | 1271.03               | 14         | "main"          | true           || 85680.00  || 1265.00    || 9            || LocalDate.of(1989, 12, 28)
        LocalDate.of(1995, 6, 7)   | LocalDate.of(1996, 5, 10)  | LocalDate.of(1994, 5, 28)  | false    | 4979.74     | 844.69          | 0.00                  | 2          | "main"          | false          || 25510.05  || 0.00       || 9            || LocalDate.of(1996, 9, 10)
        LocalDate.of(1985, 7, 27)  | LocalDate.of(1985, 12, 16) | LocalDate.of(1984, 10, 21) | false    | 4646.65     | 976.26          | 0.00                  | 3          | "main"          | true           || 18360.00  || 0.00       || 0            || LocalDate.of(1986, 4, 16)
        LocalDate.of(2018, 1, 31)  | LocalDate.of(2019, 2, 22)  | LocalDate.of(2017, 1, 26)  | false    | 8492.34     | 0.00            | 62.85                 | 3          | "main"          | false          || 35924.49  || 0.00       || 9            || LocalDate.of(2019, 6, 22)
        LocalDate.of(1988, 10, 5)  | LocalDate.of(1989, 7, 7)   | null                       | true     | 3424.63     | 3627.77         | 0.00                  | 9          | "pre-sessional" | false          || 79830.00  || 0.00       || 9            || LocalDate.of(1989, 9, 7)
        LocalDate.of(1999, 3, 16)  | LocalDate.of(1999, 11, 12) | null                       | true     | 5926.90     | 0.00            | 1059.81               | 8          | "pre-sessional" | false          || 75827.09  || 0.00       || 0            || LocalDate.of(2000, 1, 12)
        LocalDate.of(1986, 2, 16)  | LocalDate.of(1987, 1, 19)  | LocalDate.of(1985, 5, 17)  | false    | 3709.15     | 0.00            | 783.02                | 0          | "main"          | false          || 12061.13  || 0.00       || 9            || LocalDate.of(1987, 5, 19)
        LocalDate.of(1999, 1, 19)  | LocalDate.of(1999, 6, 10)  | LocalDate.of(1998, 7, 12)  | false    | 6472.86     | 0.00            | 0.00                  | 7          | "main"          | false          || 44867.86  || 0.00       || 0            || LocalDate.of(1999, 8, 10)
        LocalDate.of(1997, 6, 8)   | LocalDate.of(1998, 6, 7)   | LocalDate.of(1996, 10, 11) | false    | 5102.32     | 2363.63         | 927.65                | 6          | "main"          | true           || 36720.00  || 0.00       || 9            || LocalDate.of(1998, 10, 7)
        LocalDate.of(2032, 4, 6)   | LocalDate.of(2033, 2, 19)  | LocalDate.of(2031, 11, 26) | false    | 3162.36     | 6344.67         | 0.00                  | 8          | "main"          | true           || 48960.00  || 0.00       || 9            || LocalDate.of(2033, 6, 19)
        LocalDate.of(1974, 11, 19) | LocalDate.of(1975, 3, 3)   | null                       | true     | 3863.78     | 192.52          | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(1975, 3, 10)
        LocalDate.of(2020, 9, 8)   | LocalDate.of(2021, 3, 22)  | LocalDate.of(2020, 3, 1)   | false    | 1098.10     | 7743.33         | 868.49                | 12         | "main"          | false          || 79676.51  || 0.00       || 0            || LocalDate.of(2021, 7, 22)
        LocalDate.of(2005, 2, 23)  | LocalDate.of(2005, 8, 27)  | LocalDate.of(2004, 8, 25)  | true     | 2612.12     | 0.00            | 1167.92               | 1          | "main"          | false          || 17904.20  || 0.00       || 0            || LocalDate.of(2005, 12, 27)
        LocalDate.of(2021, 6, 24)  | LocalDate.of(2022, 6, 3)   | LocalDate.of(2020, 5, 21)  | true     | 1920.15     | 3968.31         | 620.85                | 7          | "main"          | true           || 53235.00  || 0.00       || 9            || LocalDate.of(2022, 10, 3)
        LocalDate.of(2023, 8, 19)  | LocalDate.of(2024, 3, 11)  | null                       | false    | 2197.68     | 22.24           | 1063.38               | 13         | "pre-sessional" | false          || 87777.06  || 0.00       || 0            || LocalDate.of(2024, 5, 11)
        LocalDate.of(2053, 3, 22)  | LocalDate.of(2053, 6, 9)   | LocalDate.of(2052, 6, 9)   | true     | 8095.18     | 0.00            | 310.62                | 0          | "main"          | false          || 11579.56  || 0.00       || 0            || LocalDate.of(2053, 10, 9)
        LocalDate.of(2033, 4, 19)  | LocalDate.of(2033, 11, 21) | null                       | false    | 1219.35     | 0.00            | 0.00                  | 9          | "pre-sessional" | false          || 64419.35  || 0.00       || 0            || LocalDate.of(2034, 1, 21)
        LocalDate.of(2046, 1, 31)  | LocalDate.of(2046, 11, 15) | LocalDate.of(2045, 12, 26) | true     | 2689.88     | 9771.00         | 0.00                  | 2          | "main"          | true           || 15210.00  || 0.00       || 9            || LocalDate.of(2047, 1, 15)
        LocalDate.of(2037, 2, 24)  | LocalDate.of(2037, 5, 20)  | null                       | false    | 5493.13     | 0.00            | 547.71                | 0          | "pre-sessional" | false          || 7990.42   || 0.00       || 0            || LocalDate.of(2037, 6, 20)
        LocalDate.of(2034, 2, 9)   | LocalDate.of(2034, 12, 10) | null                       | false    | 5992.25     | 0.00            | 0.00                  | 14         | "pre-sessional" | false          || 100807.25 || 0.00       || 9            || LocalDate.of(2035, 2, 10)
        LocalDate.of(1995, 12, 28) | LocalDate.of(1996, 8, 4)   | null                       | true     | 5511.08     | 7241.09         | 0.00                  | 4          | "pre-sessional" | false          || 40540.00  || 0.00       || 0            || LocalDate.of(1996, 10, 4)
        LocalDate.of(1990, 4, 3)   | LocalDate.of(1991, 4, 27)  | null                       | false    | 8687.57     | 7940.28         | 0.00                  | 1          | "main"          | false          || 16002.29  || 0.00       || 9            || LocalDate.of(1991, 8, 27)
        LocalDate.of(1995, 10, 3)  | LocalDate.of(1996, 10, 15) | null                       | true     | 6842.07     | 0.00            | 0.00                  | 2          | "pre-sessional" | false          || 33437.07  || 0.00       || 9            || LocalDate.of(1997, 2, 15)
        LocalDate.of(2025, 1, 5)   | LocalDate.of(2025, 1, 24)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2025, 2, 24)
        LocalDate.of(2042, 6, 26)  | LocalDate.of(2042, 8, 11)  | null                       | true     | 6826.91     | 8026.48         | 754.94                | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2042, 8, 18)
        LocalDate.of(1988, 9, 14)  | LocalDate.of(1989, 9, 2)   | LocalDate.of(1988, 4, 12)  | false    | 4434.32     | 3080.34         | 0.00                  | 0          | "main"          | false          || 10488.98  || 0.00       || 9            || LocalDate.of(1990, 1, 2)
        LocalDate.of(2025, 6, 19)  | LocalDate.of(2026, 3, 23)  | LocalDate.of(2025, 4, 11)  | true     | 8811.37     | 2979.07         | 756.86                | 0          | "main"          | false          || 16460.44  || 0.00       || 9            || LocalDate.of(2026, 5, 23)
        LocalDate.of(2015, 10, 6)  | LocalDate.of(2016, 10, 5)  | null                       | true     | 9040.49     | 0.00            | 0.00                  | 9          | "pre-sessional" | false          || 88870.49  || 0.00       || 9            || LocalDate.of(2017, 2, 5)
        LocalDate.of(2042, 7, 24)  | LocalDate.of(2043, 4, 11)  | null                       | false    | 784.25      | 2022.07         | 0.00                  | 7          | "main"          | false          || 51975.00  || 0.00       || 0            || LocalDate.of(2043, 6, 11)
        LocalDate.of(1986, 4, 16)  | LocalDate.of(1987, 4, 20)  | LocalDate.of(1986, 3, 16)  | false    | 6374.95     | 0.00            | 1522.32               | 8          | "main"          | false          || 63204.95  || 1265.00    || 9            || LocalDate.of(1987, 8, 20)
        LocalDate.of(2054, 8, 26)  | LocalDate.of(2055, 4, 18)  | null                       | true     | 2820.31     | 0.00            | 0.00                  | 4          | "pre-sessional" | false          || 43360.31  || 0.00       || 0            || LocalDate.of(2055, 6, 18)
        LocalDate.of(2036, 10, 21) | LocalDate.of(2037, 11, 22) | null                       | true     | 126.47      | 5214.61         | 500.55                | 12         | "pre-sessional" | false          || 102144.45 || 0.00       || 9            || LocalDate.of(2038, 3, 22)
        LocalDate.of(1992, 8, 21)  | LocalDate.of(1993, 2, 18)  | LocalDate.of(1992, 5, 26)  | false    | 0.00        | 0.00            | 0.00                  | 11         | "main"          | false          || 65930.00  || 0.00       || 0            || LocalDate.of(1993, 4, 18)
        LocalDate.of(2038, 5, 19)  | LocalDate.of(2038, 11, 27) | LocalDate.of(2038, 1, 12)  | false    | 1162.41     | 7899.28         | 0.00                  | 8          | "main"          | true           || 48960.00  || 0.00       || 0            || LocalDate.of(2039, 1, 27)
        LocalDate.of(2002, 11, 5)  | LocalDate.of(2003, 3, 5)   | LocalDate.of(2001, 11, 23) | false    | 7132.17     | 6475.17         | 0.00                  | 0          | "main"          | false          || 5732.00   || 0.00       || 0            || LocalDate.of(2003, 7, 5)
        LocalDate.of(1977, 1, 11)  | LocalDate.of(1977, 7, 14)  | null                       | true     | 9454.14     | 6848.39         | 1390.89               | 5          | "pre-sessional" | false          || 48220.75  || 1265.00    || 0            || LocalDate.of(1977, 9, 14)
        LocalDate.of(2020, 5, 16)  | LocalDate.of(2020, 11, 28) | LocalDate.of(2019, 7, 31)  | false    | 3463.02     | 3476.75         | 1571.40               | 10         | "main"          | true           || 61200.00  || 1265.00    || 0            || LocalDate.of(2021, 3, 28)
        LocalDate.of(2005, 6, 27)  | LocalDate.of(2006, 2, 3)   | LocalDate.of(2005, 4, 25)  | true     | 5303.03     | 0.00            | 0.00                  | 13         | "main"          | false          || 114288.03 || 0.00       || 0            || LocalDate.of(2006, 4, 3)
        LocalDate.of(2021, 5, 20)  | LocalDate.of(2022, 5, 26)  | LocalDate.of(2020, 10, 26) | false    | 0.00        | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 9            || LocalDate.of(2022, 9, 26)
        LocalDate.of(1976, 7, 3)   | LocalDate.of(1977, 2, 4)   | null                       | true     | 2116.75     | 0.00            | 916.19                | 10         | "main"          | true           || 76050.00  || 0.00       || 0            || LocalDate.of(1977, 4, 4)
        LocalDate.of(2016, 3, 19)  | LocalDate.of(2016, 8, 16)  | null                       | true     | 38.13       | 8474.43         | 949.55                | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2016, 9, 16)
        LocalDate.of(1982, 5, 19)  | LocalDate.of(1982, 10, 6)  | null                       | false    | 0.00        | 0.00            | 1804.97               | 0          | "main"          | false          || 3810.00   || 1265.00    || 0            || LocalDate.of(1982, 10, 13)
        LocalDate.of(2010, 3, 15)  | LocalDate.of(2010, 8, 2)   | null                       | true     | 2269.70     | 4061.21         | 1148.59               | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2010, 8, 9)
        LocalDate.of(2027, 11, 1)  | LocalDate.of(2028, 4, 25)  | null                       | true     | 3407.06     | 0.00            | 1929.44               | 0          | "main"          | true           || 0.00      || 1265.00    || 0            || LocalDate.of(2028, 5, 2)
        LocalDate.of(2008, 1, 16)  | LocalDate.of(2008, 4, 8)   | LocalDate.of(2007, 11, 9)  | false    | 1237.52     | 0.00            | 0.00                  | 0          | "main"          | false          || 4282.52   || 0.00       || 0            || LocalDate.of(2008, 4, 15)
        LocalDate.of(1983, 6, 16)  | LocalDate.of(1983, 7, 9)   | LocalDate.of(1983, 2, 17)  | true     | 5608.46     | 0.00            | 900.02                | 10         | "main"          | true           || 16900.00  || 0.00       || 0            || LocalDate.of(1983, 7, 16)
        LocalDate.of(2050, 2, 18)  | LocalDate.of(2051, 2, 18)  | null                       | false    | 3337.37     | 0.00            | 1037.99               | 12         | "pre-sessional" | true           || 73440.00  || 0.00       || 9            || LocalDate.of(2051, 6, 18)
        LocalDate.of(1995, 6, 16)  | LocalDate.of(1996, 5, 6)   | LocalDate.of(1994, 11, 25) | false    | 4906.99     | 0.00            | 1984.80               | 13         | "main"          | true           || 79560.00  || 1265.00    || 9            || LocalDate.of(1996, 9, 6)
        LocalDate.of(1981, 7, 23)  | LocalDate.of(1982, 2, 25)  | null                       | true     | 9344.68     | 0.00            | 0.00                  | 8          | "pre-sessional" | true           || 60840.00  || 0.00       || 0            || LocalDate.of(1982, 4, 25)
        LocalDate.of(2045, 11, 15) | LocalDate.of(2046, 5, 22)  | null                       | true     | 0.00        | 0.00            | 0.00                  | 8          | "main"          | true           || 60840.00  || 0.00       || 0            || LocalDate.of(2046, 7, 22)
        LocalDate.of(1992, 11, 19) | LocalDate.of(1993, 9, 7)   | LocalDate.of(1992, 7, 24)  | true     | 6611.81     | 9295.77         | 0.00                  | 14         | "main"          | true           || 106470.00 || 0.00       || 9            || LocalDate.of(1994, 1, 7)
        LocalDate.of(1975, 8, 20)  | LocalDate.of(1975, 8, 31)  | LocalDate.of(1974, 10, 30) | true     | 2212.27     | 0.00            | 0.00                  | 13         | "main"          | false          || 36432.27  || 0.00       || 0            || LocalDate.of(1975, 10, 31)
        LocalDate.of(1973, 6, 10)  | LocalDate.of(1973, 8, 5)   | null                       | true     | 4703.15     | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(1973, 8, 12)
        LocalDate.of(2038, 10, 21) | LocalDate.of(2039, 2, 5)   | LocalDate.of(2038, 4, 6)   | true     | 8478.27     | 9407.05         | 1179.38               | 6          | "main"          | false          || 34300.62  || 0.00       || 0            || LocalDate.of(2039, 4, 5)
        LocalDate.of(2020, 3, 27)  | LocalDate.of(2021, 3, 16)  | null                       | true     | 3113.37     | 9208.67         | 0.00                  | 6          | "pre-sessional" | false          || 57015.00  || 0.00       || 9            || LocalDate.of(2021, 5, 16)
        LocalDate.of(2041, 7, 6)   | LocalDate.of(2042, 8, 5)   | null                       | true     | 9331.99     | 0.00            | 0.00                  | 2          | "pre-sessional" | false          || 35926.99  || 0.00       || 9            || LocalDate.of(2042, 12, 5)
        LocalDate.of(2025, 9, 20)  | LocalDate.of(2026, 2, 17)  | null                       | false    | 5815.21     | 0.00            | 1529.07               | 0          | "main"          | false          || 9625.21   || 1265.00    || 0            || LocalDate.of(2026, 2, 24)
        LocalDate.of(1980, 1, 24)  | LocalDate.of(1980, 10, 13) | null                       | true     | 5285.54     | 7661.10         | 1121.04               | 9          | "pre-sessional" | true           || 68445.00  || 0.00       || 0            || LocalDate.of(1980, 12, 13)
        LocalDate.of(1980, 1, 20)  | LocalDate.of(1980, 12, 23) | LocalDate.of(1978, 12, 19) | false    | 0.00        | 0.00            | 0.00                  | 11         | "main"          | true           || 67320.00  || 0.00       || 9            || LocalDate.of(1981, 4, 23)
        LocalDate.of(2015, 9, 9)   | LocalDate.of(2016, 9, 11)  | LocalDate.of(2015, 3, 19)  | false    | 9318.33     | 5099.44         | 1348.31               | 12         | "main"          | true           || 73440.00  || 1265.00    || 9            || LocalDate.of(2017, 1, 11)
        LocalDate.of(2023, 4, 23)  | LocalDate.of(2023, 7, 13)  | LocalDate.of(2022, 6, 6)   | true     | 9818.00     | 3997.65         | 1499.40               | 12         | "main"          | true           || 70980.00  || 1265.00    || 0            || LocalDate.of(2023, 11, 13)
        LocalDate.of(2031, 1, 12)  | LocalDate.of(2031, 8, 13)  | LocalDate.of(2030, 2, 18)  | true     | 4730.15     | 0.00            | 0.00                  | 12         | "main"          | true           || 91260.00  || 0.00       || 0            || LocalDate.of(2031, 12, 13)
        LocalDate.of(2028, 6, 3)   | LocalDate.of(2029, 2, 11)  | null                       | true     | 4742.21     | 0.00            | 0.00                  | 8          | "pre-sessional" | false          || 76967.21  || 0.00       || 0            || LocalDate.of(2029, 4, 11)
        LocalDate.of(2044, 2, 7)   | LocalDate.of(2044, 3, 14)  | LocalDate.of(2043, 10, 29) | true     | 4793.59     | 6854.90         | 1109.43               | 9          | "main"          | true           || 15210.00  || 0.00       || 0            || LocalDate.of(2044, 3, 21)
        LocalDate.of(1975, 11, 23) | LocalDate.of(1976, 2, 1)   | LocalDate.of(1975, 6, 24)  | true     | 1449.21     | 4868.19         | 0.00                  | 13         | "main"          | true           || 54925.00  || 0.00       || 0            || LocalDate.of(1976, 4, 1)
        LocalDate.of(1990, 3, 13)  | LocalDate.of(1990, 4, 20)  | null                       | false    | 3968.88     | 0.00            | 994.62                | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(1990, 4, 27)
        LocalDate.of(2005, 10, 15) | LocalDate.of(2006, 7, 31)  | LocalDate.of(2005, 3, 3)   | false    | 2306.19     | 2256.86         | 0.00                  | 1          | "main"          | false          || 15304.33  || 0.00       || 9            || LocalDate.of(2006, 11, 30)
        LocalDate.of(2020, 4, 13)  | LocalDate.of(2020, 9, 9)   | LocalDate.of(2019, 4, 29)  | true     | 4946.06     | 0.00            | 843.22                | 5          | "main"          | false          || 48452.84  || 0.00       || 0            || LocalDate.of(2021, 1, 9)
        LocalDate.of(1979, 12, 1)  | LocalDate.of(1980, 1, 21)  | LocalDate.of(1979, 3, 12)  | false    | 8403.04     | 7505.85         | 0.00                  | 11         | "main"          | false          || 32847.19  || 0.00       || 0            || LocalDate.of(1980, 3, 21)
        LocalDate.of(1988, 7, 24)  | LocalDate.of(1989, 6, 10)  | null                       | false    | 212.08      | 0.00            | 1831.93               | 14         | "main"          | false          || 93762.08  || 1265.00    || 9            || LocalDate.of(1989, 8, 10)
        LocalDate.of(2018, 9, 10)  | LocalDate.of(2019, 9, 26)  | LocalDate.of(2018, 1, 31)  | true     | 9927.75     | 3389.60         | 692.28                | 2          | "main"          | false          || 32440.87  || 0.00       || 9            || LocalDate.of(2020, 1, 26)
        LocalDate.of(2000, 9, 17)  | LocalDate.of(2001, 7, 25)  | null                       | false    | 1159.32     | 5611.10         | 423.84                | 10         | "main"          | false          || 69911.16  || 0.00       || 9            || LocalDate.of(2001, 9, 25)
        LocalDate.of(2014, 9, 16)  | LocalDate.of(2014, 10, 5)  | LocalDate.of(2014, 1, 18)  | false    | 9625.53     | 0.00            | 1769.58               | 12         | "main"          | true           || 24480.00  || 1265.00    || 0            || LocalDate.of(2014, 12, 5)
        LocalDate.of(2009, 2, 4)   | LocalDate.of(2010, 2, 16)  | LocalDate.of(2008, 5, 22)  | false    | 8593.26     | 185.55          | 1157.83               | 7          | "main"          | true           || 42840.00  || 0.00       || 9            || LocalDate.of(2010, 6, 16)
        LocalDate.of(2038, 4, 22)  | LocalDate.of(2038, 9, 9)   | null                       | true     | 1287.75     | 2170.85         | 1446.49               | 0          | "main"          | false          || 5060.00   || 1265.00    || 0            || LocalDate.of(2038, 9, 16)
        LocalDate.of(1994, 6, 7)   | LocalDate.of(1994, 6, 13)  | null                       | true     | 7511.81     | 1091.57         | 1542.41               | 0          | "pre-sessional" | true           || 0.00      || 1265.00    || 0            || LocalDate.of(1994, 7, 13)
        LocalDate.of(2016, 5, 29)  | LocalDate.of(2016, 11, 6)  | LocalDate.of(2015, 6, 18)  | true     | 3268.65     | 0.00            | 0.00                  | 10         | "main"          | false          || 86908.65  || 0.00       || 0            || LocalDate.of(2017, 3, 6)
        LocalDate.of(2036, 5, 21)  | LocalDate.of(2036, 10, 1)  | LocalDate.of(2036, 1, 25)  | false    | 4363.11     | 6457.66         | 229.28                | 11         | "main"          | true           || 52360.00  || 0.00       || 0            || LocalDate.of(2036, 12, 1)
        LocalDate.of(1985, 7, 16)  | LocalDate.of(1986, 2, 27)  | null                       | true     | 4942.47     | 0.00            | 0.00                  | 14         | "pre-sessional" | true           || 106470.00 || 0.00       || 0            || LocalDate.of(1986, 4, 27)
        LocalDate.of(1980, 12, 25) | LocalDate.of(1981, 11, 13) | LocalDate.of(1980, 3, 3)   | false    | 5627.52     | 6278.31         | 1170.69               | 11         | "main"          | false          || 75284.31  || 0.00       || 9            || LocalDate.of(1982, 3, 13)
        LocalDate.of(2028, 6, 25)  | LocalDate.of(2028, 7, 1)   | null                       | false    | 5740.17     | 920.83          | 0.00                  | 0          | "pre-sessional" | true           || 0.00      || 0.00       || 0            || LocalDate.of(2028, 8, 1)
        LocalDate.of(1973, 10, 8)  | LocalDate.of(1974, 1, 13)  | LocalDate.of(1973, 8, 9)   | true     | 2415.28     | 3896.37         | 1365.80               | 12         | "main"          | false          || 44355.00  || 1265.00    || 0            || LocalDate.of(1974, 1, 20)
        LocalDate.of(2033, 5, 13)  | LocalDate.of(2033, 7, 30)  | null                       | false    | 4743.81     | 0.00            | 1899.58               | 0          | "main"          | false          || 6523.81   || 1265.00    || 0            || LocalDate.of(2033, 8, 6)
        LocalDate.of(2031, 5, 30)  | LocalDate.of(2031, 9, 22)  | null                       | true     | 2666.07     | 3799.47         | 361.25                | 0          | "pre-sessional" | false          || 4698.75   || 0.00       || 0            || LocalDate.of(2031, 10, 22)
        LocalDate.of(2017, 6, 11)  | LocalDate.of(2017, 10, 3)  | LocalDate.of(2016, 9, 2)   | true     | 1317.37     | 0.00            | 0.00                  | 4          | "main"          | true           || 27040.00  || 0.00       || 0            || LocalDate.of(2018, 2, 3)
        LocalDate.of(1974, 8, 12)  | LocalDate.of(1975, 2, 14)  | LocalDate.of(1974, 1, 10)  | false    | 7507.61     | 0.00            | 0.00                  | 9          | "main"          | false          || 69692.61  || 0.00       || 0            || LocalDate.of(1975, 6, 14)
        LocalDate.of(2054, 5, 1)   | LocalDate.of(2054, 6, 7)   | LocalDate.of(2053, 12, 4)  | true     | 1499.17     | 0.00            | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(2054, 8, 7)
        LocalDate.of(2046, 4, 21)  | LocalDate.of(2047, 1, 28)  | LocalDate.of(2045, 6, 25)  | true     | 564.14      | 0.00            | 0.00                  | 7          | "main"          | false          || 65184.14  || 0.00       || 9            || LocalDate.of(2047, 5, 28)
        LocalDate.of(1997, 5, 10)  | LocalDate.of(1997, 12, 7)  | LocalDate.of(1997, 1, 26)  | true     | 0.00        | 0.00            | 1153.35               | 9          | "main"          | true           || 68445.00  || 0.00       || 0            || LocalDate.of(1998, 2, 7)
        LocalDate.of(1992, 7, 25)  | LocalDate.of(1993, 1, 12)  | null                       | true     | 4124.34     | 2493.28         | 0.00                  | 0          | "main"          | true           || 0.00      || 0.00       || 0            || LocalDate.of(1993, 1, 19)
        LocalDate.of(1981, 9, 6)   | LocalDate.of(1982, 6, 16)  | null                       | true     | 3828.98     | 0.00            | 15.43                 | 1          | "main"          | false          || 22803.55  || 0.00       || 9            || LocalDate.of(1982, 8, 16)
        LocalDate.of(2000, 2, 10)  | LocalDate.of(2000, 12, 23) | LocalDate.of(1999, 7, 5)   | false    | 17.63       | 0.00            | 0.00                  | 1          | "main"          | true           || 6120.00   || 0.00       || 9            || LocalDate.of(2001, 4, 23)
    }

    @Unroll
    def "Tier 4 General - Check invalid tuition fees parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN", false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFees")))

        where:
        inLondon | courseStartDate          | courseEndDate             | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 21) | null                    | -2          | 1855.00         | 0          | 454.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                    | -0.05       | 4612.00         | 0          | 336.00
    }

    @Unroll
    def "Tier 4 General - Check invalid characters intuition fees parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN", false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFees")))

        where:
        inLondon | courseStartDate          | courseEndDate            | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                    | "(*&"       | 4612.00         | 0          | 336.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 7, 1) | null                    | "hh"        | 2720.00         | 0          | 1044.00
    }

    @Unroll
    def "Tier 4 General - Check invalid tuition fees paid parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN", false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate             | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                    | 1855.00     | -2              | 0          | 454.00
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                    | 4612.00     | -0.05           | 0          | 336.00
    }

    @Unroll
    def "Tier 4 General - Check invalid characters in tuition fees paid parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN", false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid tuitionFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate            | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                    | 4612.00     | "*^"            | 0          | 336.00
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                    | 2720.00     | "kk"            | 0          | 1044.00
    }

    @Unroll
    def "Tier 4 General - Check invalid accommodation fees paid parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN", false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate             | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                    | 454.00      | 1855.00         | 0          | -2
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                    | 336.00      | 4612.00         | 0          | -0.05
    }

    @Unroll
    def "Tier 4 General - Check invalid characters accommodation fees paid parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN", false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid accommodationFeesPaid")))

        where:
        inLondon | courseStartDate          | courseEndDate            | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                    | 336.00      | 4612.00         | 0          | "*(^"
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                    | 1044.00     | 2720.00         | 0          | "hh"
    }

    @Unroll
    def "Tier 4 General - Check invalid dependants parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN", false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate            | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                    | 454.00      | 1855.00         | -5         | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                    | 336.00      | 4612.00         | -99        | 0
    }

    @Unroll
    def "Tier 4 General - Check invalid characters dependants parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, "MAIN", false)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependants")))

        where:
        inLondon | courseStartDate          | courseEndDate             | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                    | 454.00      | 1855.00         | ")(&"      | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                    | 336.00      | 4612.00         | "h"        | 0
    }

    @Unroll
    def "Tier 4 General - Check invalid dependantsOnly parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, 0, "MAIN", dependantsOnly)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependantsOnly")))

        where:
        inLondon | courseStartDate          | courseEndDate            | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependantsOnly | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1) | null                    | 454.00      | 1855.00         | -5             | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 3, 1) | null                    | 336.00      | 4612.00         | -99            | 0
    }

    @Unroll
    def "Tier 4 General - Check invalid characters dependantsOnly parameters"() {
        expect:
        def response = callApi("general", inLondon, courseStartDate, courseEndDate, originalCourseStartDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, 0, "MAIN", dependantsOnly)
        response.andExpect(status().isBadRequest())

        response.andExpect(content().string(containsString("Parameter conversion error: Invalid dependantsOnly")))

        where:
        inLondon | courseStartDate          | courseEndDate             | originalCourseStartDate | tuitionFees | tuitionFeesPaid | dependantsOnly | accommodationFeesPaid
        false    | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 1, 31) | null                    | 454.00      | 1855.00         | ")(&"          | 0
        true     | LocalDate.of(2000, 1, 1) | LocalDate.of(2000, 2, 1)  | null                    | 336.00      | 4612.00         | "h"            | 0
    }


}
