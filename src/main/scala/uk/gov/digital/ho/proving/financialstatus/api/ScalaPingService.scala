package uk.gov.digital.ho.proving.financialstatus.api

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}

@RestController
class ScalaPingService {
    @RequestMapping(value = Array("/incomeproving/v1/individual/financialstatus/ping/s"), method = Array(RequestMethod.GET))
    def ping: ResponseEntity[String] = {

        val LOGGER: Logger = LoggerFactory.getLogger(classOf[ScalaPingService])

        LOGGER.info("Ping received")
        new ResponseEntity[String]("Pong", HttpStatus.OK)
    }
}

