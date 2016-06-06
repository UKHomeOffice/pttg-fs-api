package uk.gov.digital.ho.proving.financialstatus.api

import java.time.LocalDate

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}


@RestController
class ScalaPingService {

    @Autowired
    var mapper: ObjectMapper = _

    @RequestMapping(value = Array("/incomeproving/v1/individual/transactions"), method = Array(RequestMethod.GET))
    //def ping: ResponseEntity[String] = {
    def ping: ResponseEntity[AccountTransaction] = {
        val LOGGER: Logger = LoggerFactory.getLogger(classOf[ScalaPingService])

        LOGGER.info("Ping received")
        //new ResponseEntity[String](mapper.writeValueAsString(AccountTransaction(LocalDate.now(), "Description", BigDecimal(-100.00), Some(BigDecimal(2323.00)))), HttpStatus.OK)

        new ResponseEntity[AccountTransaction]((AccountTransaction(LocalDate.now(), "Description", BigDecimal(-100.00), Some(BigDecimal(2323.00)))), HttpStatus.OK)
    }
}

