package uk.gov.digital.ho.proving.financialstatus.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.{MissingPathVariableException, MissingServletRequestParameterException}
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler}
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class ApiExceptionHandler @Autowired()(objectMapper: ObjectMapper){

  /* TODO
    For some reason the exception handler is not using the configured ObjectMapper
    by default, so we have to manually convert the object to string */

  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[ApiExceptionHandler])

  private val headers: HttpHeaders = new HttpHeaders
  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  @ExceptionHandler(Array(classOf[MissingServletRequestParameterException]))
  def missingParameterHandler(exception: MissingServletRequestParameterException) = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, "0000", "Missing parameter: " + exception.getParameterName, HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[NoHandlerFoundException]))
  def requestHandlingNoHandlerFound(exception: NoHandlerFoundException) = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, "0000", "Resource not found: Please check the sort code and account number are valid values" + exception.getRequestURL, HttpStatus.NOT_FOUND)
  }

  @ExceptionHandler(Array(classOf[MissingPathVariableException]))
  def missingPathVariableException(exception: MissingPathVariableException) = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, "0000", "Path error: Missing value for " + exception.getParameter, HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[MethodArgumentTypeMismatchException]))
  def methodArgumentTypeMismatchException(exception: MethodArgumentTypeMismatchException) = {
    LOGGER.debug(exception.getMessage)
    buildErrorResponse(headers, "0000", "Parameter error: Invalid value for " + exception.getName, HttpStatus.BAD_REQUEST)
  }

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus) = {
    val response = AccountDailyBalanceStatusResponse(StatusResponse(statusCode, statusMessage))
    new ResponseEntity(objectMapper.writeValueAsString(response), headers, status)
  }

}
