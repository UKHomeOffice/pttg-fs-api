package uk.gov.digital.ho.proving.financialstatus.api.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler}
import org.springframework.web.bind.{MissingPathVariableException, MissingServletRequestParameterException}
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import uk.gov.digital.ho.proving.financialstatus.api.{AccountDailyBalanceStatusResponse, StatusResponse}

@ControllerAdvice
class ApiExceptionHandler @Autowired()(objectMapper: ObjectMapper) {

  /* TODO
    For some reason the exception handler is not using the configured ObjectMapper
    by default, so we have to manually convert the object to string */

  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[ApiExceptionHandler])

  private val headers: HttpHeaders = new HttpHeaders
  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  private val parameterMap = Map("toDate" -> "to date", "fromDate" -> "from date", "minimum" -> "value for minimum",
    "sortCode" -> "sort code", "accountNumber" -> "account number", "dependants" -> "dependants")

  val TEMP_ERROR_CODE = "0000"

  @ExceptionHandler(Array(classOf[MissingServletRequestParameterException]))
  def missingParameterHandler(exception: MissingServletRequestParameterException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, TEMP_ERROR_CODE, "Missing parameter: " + exception.getParameterName, HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[NoHandlerFoundException]))
  def requestHandlingNoHandlerFound(exception: NoHandlerFoundException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, TEMP_ERROR_CODE, "Resource not found: Please check URL and parameters are valid" + exception.getRequestURL
      , HttpStatus.NOT_FOUND)
  }

  @ExceptionHandler(Array(classOf[MissingPathVariableException]))
  def missingPathVariableException(exception: MissingPathVariableException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, TEMP_ERROR_CODE, "Path error: Missing value for " + exception.getParameter, HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[MethodArgumentTypeMismatchException]))
  def methodArgumentTypeMismatchException(exception: MethodArgumentTypeMismatchException): ResponseEntity[String] = {
    LOGGER.debug(exception.getMessage)
    val param = parameterMap.getOrElse(exception.getName, exception.getName)
    buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter conversion error: Invalid " + param, HttpStatus.BAD_REQUEST)
  }

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[String] = {
    val response = AccountDailyBalanceStatusResponse(StatusResponse(statusCode, statusMessage))
    new ResponseEntity(objectMapper.writeValueAsString(response), headers, status)
  }

}
