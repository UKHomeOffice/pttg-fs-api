package uk.gov.digital.ho.proving.financialstatus.api.test

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.apache.http.conn.HttpHostConnectException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.acl.MockBankService
import uk.gov.digital.ho.proving.financialstatus.api.DailyBalanceService
import uk.gov.digital.ho.proving.financialstatus.api.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.client.HttpUtils
import uk.gov.digital.ho.proving.financialstatus.domain.AccountStatusChecker

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = ServiceConfiguration.class)
class ServiceErrorsSpec extends Specification {

    def mockHttpUtils = Mock(HttpUtils)

    def serviceName = "mongoservice"

    @Autowired
    RestTemplate restTemplate

    MockBankService mockBankService = new MockBankService(new ObjectMapper(), mockHttpUtils, serviceName)


    def dailyBalanceService = new DailyBalanceService(new AccountStatusChecker(mockBankService, 28))
    MockMvc mockMvc = standaloneSetup(dailyBalanceService).setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter()).build()


    def "daily balance threshold check fail (bank service timeout)"() {

        given:
        def url = "/pttg/financialstatusservice/v1/accounts/12-34-56/12345678/dailybalancestatus"
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(26)

        1 * mockHttpUtils.performRequest(_) >> { throw new ResourceAccessException("Read timed out", new SocketTimeoutException()) }

        when:
        def response = mockMvc.perform(
            get(url)
                .param("fromDate", "2016-05-13")
                .param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isInternalServerError())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message == "Connection timeout"

    }

    def "daily balance threshold check fail (bank service not available)"() {

        given:
        def url = "/pttg/financialstatusservice/v1/accounts/12-34-56/12345678/dailybalancestatus"
        def toDate = LocalDate.of(2016, 6, 9)
        def fromDate = toDate.minusDays(26)

        1 * mockHttpUtils.performRequest(_) >> { throw new ResourceAccessException("Connection refused", new HttpHostConnectException(null, null, null)) }

        when:
        def response = mockMvc.perform(
            get(url)
                .param("fromDate", "2016-05-13")
                .param("toDate", "2016-06-09")
                .param("minimum", "2560.23")
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isInternalServerError())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.status.message=="Connection refused"

    }


}
