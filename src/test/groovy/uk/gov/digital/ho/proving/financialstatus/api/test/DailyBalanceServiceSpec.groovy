package uk.gov.digital.ho.proving.financialstatus.api.test

import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.acl.BarclaysBankService
import uk.gov.digital.ho.proving.financialstatus.api.DailyBalanceService
import uk.gov.digital.ho.proving.financialstatus.api.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.domain.AccountDailyBalance

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

        1 * barlcaysBankService.fetchAccountDailyBalances(_, _, _) >> randomDailyBalances(LocalDate.of(2016,6,9), 28, 2700, 3500)

        when:

        def response = mockMvc.perform(
            get("/incomeproving/v1/individual/dailybalancecheck")
                .param("accountNumber", "12345678")
                .param("sortCode", "12-34-56")
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

    def randomDailyBalances(LocalDate date, Integer num, Float lower, Float upper) {

        def randomBalances = []
        (0..num).each {
            def randomValue = (new java.util.Random().nextFloat() * (upper - lower)) + lower
            randomBalances << new AccountDailyBalance(date.minusDays(it), new scala.math.BigDecimal(randomValue))
        }

        println("randomBalances: " + randomBalances)
    }


}
