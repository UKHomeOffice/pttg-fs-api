package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal, MathContext}

import org.slf4j.LoggerFactory
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{ControllerAdvice, RequestMapping, RequestMethod, RestController}

@RestController
@ControllerAdvice class ThresholdService {
  @RequestMapping(value = Array("/incomeproving/v1/individual/threshold"), method = Array(RequestMethod.GET))
  def calculateThreshold: ResponseEntity[ThresholdResponse] = {
    val LOGGER = LoggerFactory.getLogger(classOf[ThresholdService])
    LOGGER.info("Calculating threshold")
    val thresholdResponse: ThresholdResponse = new ThresholdResponse(new BigDecimal(4123.45, MathContext.DECIMAL64), StatusResponse("200", "OK"))
    new ResponseEntity[ThresholdResponse](thresholdResponse, HttpStatus.OK)
  }
}
