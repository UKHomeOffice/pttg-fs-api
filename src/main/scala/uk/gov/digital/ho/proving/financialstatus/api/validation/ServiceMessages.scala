package uk.gov.digital.ho.proving.financialstatus.api.validation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.stereotype.Component

@Component
class ServiceMessages @Autowired()(val messageSource: ResourceBundleMessageSource) {

  val INVALID_ACCOUNT_NUMBER = getMessage("invalid.account.number")
  val INVALID_SORT_CODE = getMessage("invalid.sort.code")
  val INVALID_MINIMUM_VALUE = getMessage("invalid.minimum.value")

  val CONNECTION_TIMEOUT = getMessage("connection.timeout")
  val CONNECTION_REFUSED = getMessage("connection.refused")
  val UNKNOWN_CONNECTION_EXCEPTION = getMessage("unknown.connection.exception")
  val INVALID_FROM_DATE = getMessage("invalid.from.date")
  val INVALID_TO_DATE = getMessage("invalid.to.date")
  val INVALID_DOB_DATE = getMessage("invalid.dob.date")
  val INVALID_USER_ID = getMessage("invalid.user.id")
  val INVALID_ACCOUNT_HOLDER_CONSENT = getMessage("invalid.account.holder.consent")

  val INVALID_TUITION_FEES = getMessage("invalid.tuition.fees")
  val INVALID_TUITION_FEES_PAID = getMessage("invalid.tuition.fees.paid")
  val INVALID_ACCOMMODATION_FEES_PAID = getMessage("invalid.accommodation.fees.paid")
  val INVALID_DEPENDANTS = getMessage("invalid.dependants.value")
  val INVALID_IN_LONDON = getMessage("invalid.in.london.value")

  val INVALID_SORT_CODE_VALUE = "000000"
  val INVALID_ACCOUNT_NUMBER_VALUE = "00000000"
  val INVALID_COURSE_LENGTH = getMessage("invalid.course.length")
  val INVALID_COURSE_LENGTH_DEPENDANTS = getMessage("invalid.course.length.dependants")

  val RESOURCE_NOT_FOUND= getMessage("resource.not.found")
  val PATH_ERROR_MISSING_VALUE=getMessage("path.error.missing.value")

  val INVALID_COURSE_START_DATE = getMessage("invalid.course.start.date")
  val INVALID_COURSE_END_DATE = getMessage("invalid.course.end.date")
  val INVALID_CONTINUATION_END_DATE = getMessage("invalid.continuation.end.date")

  val INVALID_COURSE_START_DATE_VALUE = getMessage("invalid.course.start.date.value")
  val INVALID_COURSE_END_DATE_VALUE = getMessage("invalid.course.end.date.value")
  val INVALID_CONTINUATION_END_DATE_VALUE = getMessage("invalid.continuation.end.date.value")

  def INVALID_DATES(params: Int*) = getMessage("invalid.dates", params)

  def INVALID_STUDENT_TYPE(params: String*) = getMessage("invalid.student.type", params)

  def NO_RECORDS_FOR_ACCOUNT(params: String*) = getMessage("no.records.for.account", params)

  def MISSING_PARAMETER(params: String*)= getMessage("missing.parameter", params)

  def PARAMETER_CONVERSION_ERROR(params: String*) = getMessage("parameter.conversion.error", params)

  val UNEXPECTED_ERROR = getMessage("unexpected.error")
  val OK = "OK"

  val REST_MISSING_PARAMETER = getMessage("rest.missing.parameter")
  val REST_INVALID_PARAMETER_TYPE = getMessage("rest.invalid.parameter.type")
  val REST_INVALID_PARAMETER_FORMAT = getMessage("rest.invalid.parameter.format")
  val REST_INVALID_PARAMETER_VALUE = getMessage("rest.invalid.parameter.value")
  val REST_INTERNAL_ERROR = getMessage("rest.internal.error")
  val REST_API_SERVER_ERROR = getMessage("rest.api.server.error")
  val REST_API_CLIENT_ERROR = getMessage("rest.api.client.error")
  val REST_API_CONNECTION_ERROR = getMessage("rest.api.connection.error")

  def getMessage(message: String): String = messageSource.getMessage(message, Nil.toArray[Object], LocaleContextHolder.getLocale)
  def getMessage[T](message: String, params: Seq[T]): String = getMessage(message, params.map(_.asInstanceOf[Object]).toArray[Object])
  def getMessage(message: String, params: Array[Object]): String = messageSource.getMessage(message, params, LocaleContextHolder.getLocale)

}
