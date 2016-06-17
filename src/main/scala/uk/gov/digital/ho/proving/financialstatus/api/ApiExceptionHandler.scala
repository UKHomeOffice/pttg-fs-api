package uk.gov.digital.ho.proving.financialstatus.api

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler}
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class ApiExceptionHandler {

  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[ApiExceptionHandler])

  @ExceptionHandler(Array(classOf[MissingServletRequestParameterException]))
  def missingParameterHandler(exception: MissingServletRequestParameterException): Any = {
    LOGGER.debug(exception.getMessage)
    val headers: HttpHeaders = new HttpHeaders
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    buildErrorResponse(headers, "0000", "Missing parameter: " + exception.getParameterName, HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Array(classOf[NoHandlerFoundException]))
  def requestHandlingNoHandlerFound(exception: NoHandlerFoundException): Any = {
    LOGGER.debug(exception.getMessage)
    val headers: HttpHeaders = new HttpHeaders
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    buildErrorResponse(headers, "0000", "Resource not found: " + exception.getRequestURL, HttpStatus.NOT_FOUND)
  }

  @ExceptionHandler(Array(classOf[MethodArgumentTypeMismatchException]))
  def methodArgumentTypeMismatchException(exception: MethodArgumentTypeMismatchException): Any = {
    LOGGER.debug(exception.getMessage)
    val headers: HttpHeaders = new HttpHeaders
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    buildErrorResponse(headers, "0000", "Parameter error: Invalid value for " + exception.getName, HttpStatus.BAD_REQUEST)
  }

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[BaseResponse] = {
    val response: BaseResponse = new BaseResponse(StatusResponse(statusCode, statusMessage))
    new ResponseEntity(response, headers, status)
  }

}
