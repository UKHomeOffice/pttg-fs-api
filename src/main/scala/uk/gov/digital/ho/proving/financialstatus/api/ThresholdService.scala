package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.PropertySource
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatusservice/v1/maintenance"))
class ThresholdService /* @Autowired() */ {

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET))
  def calculateThreshold(innerLondon: Boolean, courseLength: Int, tuitionFees: JBigDecimal): ResponseEntity[ThresholdResponse] = {
    val LOGGER = LoggerFactory.getLogger(classOf[ThresholdService])
    LOGGER.info("Calculating threshold")

    val thresholdResponse: ThresholdResponse = new ThresholdResponse(
      MaintenanceThresholdCalculator.calculate(innerLondon, courseLength, BigDecimal(tuitionFees).setScale(2)),
      StatusResponse("200", "OK")
    )

    new ResponseEntity[ThresholdResponse](thresholdResponse, HttpStatus.OK)
  }

}
