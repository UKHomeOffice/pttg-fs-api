package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}
import javax.validation.constraints.NotNull

import org.hibernate.validator.constraints.NotEmpty
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.PropertySource
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatusservice/v1/maintenance"))
@ControllerAdvice
class ThresholdService {

  val courseLengthPattern = """^[0-9]$""".r

  val headers = new HttpHeaders()
  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  val TEMP_ERROR_CODE: String = "0000"

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def calculateThreshold(@RequestParam(required = true) innerLondon: Boolean,
                         @RequestParam(required = true) courseLength: Int,
                         @RequestParam(required = true) tuitionFees: JBigDecimal,
                         @RequestParam(required = true) tuitionFeesPaid: JBigDecimal,
                         @RequestParam(required = true) accommodationFeesPaid: JBigDecimal
                        ): ResponseEntity[ThresholdResponse] = {

    val LOGGER = LoggerFactory.getLogger(classOf[ThresholdService])
    LOGGER.info("Calculating threshold")


    val response =
      if (!validateCourseLength(courseLength)) {
        buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: Invalid courseLength", HttpStatus.BAD_REQUEST)
      } else if (!validateTuitionFees(tuitionFees)) {
        buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: Invalid tuitionFees", HttpStatus.BAD_REQUEST)
      } else if (!validateTuitionFeesPaid(tuitionFeesPaid)) {
        buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: Invalid tuitionFeesPaid", HttpStatus.BAD_REQUEST)
      } else if (!validateAccommodationFeesPaid(accommodationFeesPaid)) {
        buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: Invalid accommodationFeesPaid", HttpStatus.BAD_REQUEST)
      } else {
        val thresholdResponse: ThresholdResponse = new ThresholdResponse(
          MaintenanceThresholdCalculator.calculate(innerLondon, courseLength,
            BigDecimal(tuitionFees).setScale(2), BigDecimal(tuitionFeesPaid).setScale(2), BigDecimal(accommodationFeesPaid).setScale(2)), StatusResponse("200", "OK"))
        new ResponseEntity[ThresholdResponse](thresholdResponse, HttpStatus.OK)
      }
    response
  }


  def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus) = {
    new ResponseEntity(ThresholdResponse(null, StatusResponse(statusCode, statusMessage)), headers, status)
  }

  def validateCourseLength(courseLength: Int) = {
    val m = courseLength match {
      case x if (0 <= x && x < 10) => true
      case _ => false
    }
    m
  }

  def validateTuitionFees(tuitionFees: JBigDecimal) = {
    tuitionFees != null
  }

  def validateTuitionFeesPaid(tuitionFeesPaid: JBigDecimal) = {
    tuitionFeesPaid != null
  }

  def validateAccommodationFeesPaid(accommodationFeesPaid: JBigDecimal) = {
    accommodationFeesPaid != null
  }


}
