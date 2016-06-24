package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.PropertySource
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}
import uk.gov.digital.ho.proving.financialstatus.domain.MaintenanceThresholdCalculator

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatusservice/v1/maintenance"))
class ThresholdService {

  // TODO Temporary error code until these are finalised or removed
  val TEMP_ERROR_CODE = "0000"

  val courseLengthMinimum = 1
  val courseLengthMaximum = 9

  val headers = new HttpHeaders()
  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET))
  def calculateThreshold(innerLondon: Boolean,
                         courseLength: Int,
                         tuitionFees: JBigDecimal,
                         tuitionFeesPaid: JBigDecimal,
                         accommodationFeesPaid: JBigDecimal
                        ): ResponseEntity[ThresholdResponse] = {

    val LOGGER = LoggerFactory.getLogger(classOf[ThresholdService])
    LOGGER.info("Calculating threshold")

    val response = if (!validateCourseLength(courseLength)) buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: course length must be 1-9 inclusive", HttpStatus.BAD_REQUEST)
    else if (!validateTuitionFees(tuitionFees)) buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: tuition fees must be greater than or equal to zero", HttpStatus.BAD_REQUEST)
    else if (!validateTuitionFeesPaid(tuitionFeesPaid)) buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: tuition fees paid must be greater than or equal to zero", HttpStatus.BAD_REQUEST)
    else if (!validateAccommodationFeesPaid(accommodationFeesPaid)) buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: accommodation fees paid must be greater than or equal to zero", HttpStatus.BAD_REQUEST)
    else new ResponseEntity[ThresholdResponse](
      ThresholdResponse(
        MaintenanceThresholdCalculator.calculate(innerLondon, courseLength,
          BigDecimal(tuitionFees).setScale(2), BigDecimal(tuitionFeesPaid).setScale(2),
          BigDecimal(accommodationFeesPaid).setScale(2)),
        StatusResponse("200", "OK")
      ),
      HttpStatus.OK)

    response
  }

  def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus) = {
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)
  }

  def validateCourseLength(length: BigDecimal) = length >= courseLengthMinimum && length <= courseLengthMaximum

  def validateTuitionFees(fees: BigDecimal) = fees >= 0

  def validateTuitionFeesPaid(feesPaid: BigDecimal) = feesPaid >= 0

  def validateAccommodationFeesPaid(accommodationPaid: BigDecimal) = accommodationPaid >= 0
}
