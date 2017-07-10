package uk.gov.digital.ho.proving.financialstatus.api.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler}
import org.springframework.web.bind.{MissingPathVariableException, MissingServletRequestParameterException}
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.api.{AccountDailyBalanceStatusResponse, StatusResponse, ThresholdResponse}
import uk.gov.digital.ho.proving.financialstatus.domain.{ApplicantTypeChecker, TierChecker, VariantTypeChecker}

@ControllerAdvice
class ApiExceptionHandler @Autowired()(objectMapper: ObjectMapper,
                                       tierChecker: TierChecker,
                                       applicantTypeChecker: ApplicantTypeChecker,
                                       variantTypeChecker: VariantTypeChecker,
                                       serviceMessages: ServiceMessages) {

  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[ApiExceptionHandler])

  private val headers: HttpHeaders = new HttpHeaders
  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  private val parameterMap = Map("toDate" -> "to date", "fromDate" -> "from date", "minimum" -> "value for minimum",
    "sortCode" -> "sort code", "accountNumber" -> "account number", "dependants" -> "dependants")

  @ExceptionHandler(Array(classOf[MissingServletRequestParameterException]))
  def missingParameterHandler(exception: MissingServletRequestParameterException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, serviceMessages.REST_MISSING_PARAMETER, serviceMessages.MISSING_PARAMETER(exception.getParameterName), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[NoHandlerFoundException]))
  def requestHandlingNoHandlerFound(exception: NoHandlerFoundException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, serviceMessages.REST_MISSING_PARAMETER, serviceMessages.RESOURCE_NOT_FOUND
      , HttpStatus.NOT_FOUND)
  }

  @ExceptionHandler(Array(classOf[MissingPathVariableException]))
  def missingPathVariableException(exception: MissingPathVariableException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, serviceMessages.REST_MISSING_PARAMETER, serviceMessages.PATH_ERROR_MISSING_VALUE, HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[MethodArgumentTypeMismatchException]))
  def methodArgumentTypeMismatchException(exception: MethodArgumentTypeMismatchException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    val param = parameterMap.getOrElse(exception.getName, exception.getName)
    buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_TYPE, serviceMessages.PARAMETER_CONVERSION_ERROR(param), HttpStatus.BAD_REQUEST)
  }

//  @ExceptionHandler(Array(classOf[ApplicantTypeException]))
  //  def invalidApplicantValueHandler(exception: ApplicantTypeException): ResponseEntity[String] = {
  //    LOGGER.debug(exception.getMessage)
  //    buildErrorResponse(headers,
  //      serviceMessages.REST_INVALID_PARAMETER_VALUE,
  //      serviceMessages.INVALID_APPLICANT_TYPE(applicantTypeChecker.values.mkString(",")),
  //      HttpStatus.BAD_REQUEST)
  //  }

  @ExceptionHandler(Array(classOf[ApplicantTypeException]))
  def invalidApplicantValueHandler(exception: ApplicantTypeException): ResponseEntity[ThresholdResponse] = {
    LOGGER.debug(exception.getMessage)
    new ResponseEntity(ThresholdResponse(StatusResponse(serviceMessages.REST_API_CLIENT_ERROR,
      serviceMessages.INVALID_APPLICANT_TYPE(applicantTypeChecker.values.mkString(",")))),
      headers,
      HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[VariantTypeException]))
  def invalidVariantValueHandler(exception: VariantTypeException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers,
                        serviceMessages.REST_INVALID_PARAMETER_VALUE,
                        serviceMessages.INVALID_VARIANT_TYPE(variantTypeChecker.values.mkString(",")),
                        HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[TierTypeException]))
  def invalidTierValueHandler(exception: TierTypeException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers,
      serviceMessages.REST_INVALID_PARAMETER_VALUE,
      serviceMessages.INVALID_TIER_TYPE(tierChecker.values.mkString(",")),
      HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[DependantsException]))
  def invalidDependantsValueHandler(exception: DependantsException): ResponseEntity[ThresholdResponse] = {
    LOGGER.debug(exception.getMessage)
    new ResponseEntity(ThresholdResponse(StatusResponse(serviceMessages.REST_API_CLIENT_ERROR,
      serviceMessages.INVALID_NUM_OF_DEPENDANTS(exception.getMessage))),
      headers,
      HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[IllegalArgumentException]))
  def invalidArgumentHandler(exception: IllegalArgumentException): ResponseEntity[ThresholdResponse] = {
    LOGGER.debug(exception.getMessage)
    new ResponseEntity(ThresholdResponse(StatusResponse(serviceMessages.REST_API_CLIENT_ERROR,
      serviceMessages.INVALID_ARGUMENT(exception.getMessage))),
      headers,
      HttpStatus.BAD_REQUEST)
  }

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[String] = {
    val response = AccountDailyBalanceStatusResponse(StatusResponse(statusCode, statusMessage))
    new ResponseEntity(objectMapper.writeValueAsString(response), headers, status)
  }

}
