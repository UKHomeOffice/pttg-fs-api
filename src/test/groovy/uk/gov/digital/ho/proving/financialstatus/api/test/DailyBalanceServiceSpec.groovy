package uk.gov.digital.ho.proving.financialstatus.api.test

import groovy.json.JsonSlurper
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.acl.BarclaysBankService
import uk.gov.digital.ho.proving.financialstatus.api.DailyBalanceService
import uk.gov.digital.ho.proving.financialstatus.api.ServiceConfiguration

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

/**
 * @Author Home Office Digital
 */
@WebAppConfiguration
@ContextConfiguration(classes = ServiceConfiguration.class)
class DailyBalanceServiceSpec extends Specification {

    def barlcaysBankService = Mock(BarclaysBankService)

    def dailyBalanceService = new DailyBalanceService(barlcaysBankService)
    MockMvc mockMvc = standaloneSetup(dailyBalanceService).setMessageConverters(new ServiceConfiguration().mappingJackson2HttpMessageConverter()).build()


    def setup() {

    }

    def "daily balance threshold check pass"() {

        1 * barlcaysBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.randomBankResponseOK(LocalDate.of(2016,6,9), 28, 2560.23, 3500, true, false)

        when:
        def response = mockMvc.perform(
            get("/incomeproving/v1/individual/dailybalancecheck/12-34-56/12345678")
                .param("applicationRaisedDate", "2016-06-09")
                .param("threshold", "2560.23")
                .param("days", "28")
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.dailyBalanceCheck.minimumAboveThreshold == true

    }


    def "daily balance threshold check fail"() {

        1 * barlcaysBankService.fetchAccountDailyBalances(_, _, _) >> DataUtils.randomBankResponseOK(LocalDate.of(2016,6,9), 28, 2560.22, 3500, true, false)

        when:
        def response = mockMvc.perform(
            get("/incomeproving/v1/individual/dailybalancecheck/12-34-56/12345678")
                .param("applicationRaisedDate", "2016-06-09")
                .param("threshold", "2560.23")
                .param("days", "28")
        )

        then:
        response.andDo(MockMvcResultHandlers.print())
        response.andExpect(status().isOk())
        def jsonContent = new JsonSlurper().parseText(response.andReturn().response.getContentAsString())
        jsonContent.dailyBalanceCheck.minimumAboveThreshold == false

    }


}
