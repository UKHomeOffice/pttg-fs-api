package uk.gov.digital.ho.proving.financialstatus.api.test

//import org.springframework.test.web.servlet.MockMvc
//import spock.lang.Specification
//import uk.gov.digital.ho.proving.financialstatus.api.PingService
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
//import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup
//
///**
// * @Author Home Office Digital
// */
//
//class PingServiceSpec extends Specification {
//
//    def pingService = new PingService()
//    MockMvc mockMvc = standaloneSetup(pingService).build()
//
//    def setup() {
//    }
//
//    def "pinging the service returns a pong response"() {
//
//        when:
//        def response = mockMvc.perform(
//            get("/incomeproving/v1/individual/financialstatus/ping")
//        )
//
//        then:
//        response.andReturn().response.contentAsString == "Pong"
//        response.andExpect(status().isOk())
//
//    }
//
//}
