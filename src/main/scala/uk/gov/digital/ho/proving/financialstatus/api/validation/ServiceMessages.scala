package uk.gov.digital.ho.proving.financialstatus.api.validation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.stereotype.Component

@Component
class ServiceMessages @Autowired()(val messageSource: ResourceBundleMessageSource) {

  val INVALID_ACCOUNT_NUMBER: String = getMessage("invalid.account.number")
  val INVALID_SORT_CODE: String = getMessage("invalid.sort.code")
  val INVALID_MINIMUM_VALUE: String = getMessage("invalid.minimum.value")

  val CONNECTION_TIMEOUT: String = getMessage("connection.timeout")
  val CONNECTION_REFUSED: String = getMessage("connection.refused")
  val UNKNOWN_CONNECTION_EXCEPTION: String = getMessage("unknown.connection.exception")
  val INVALID_FROM_DATE: String = getMessage("invalid.from.date")
  val INVALID_TO_DATE: String = getMessage("invalid.to.date")
  val INVALID_DOB_DATE: String = getMessage("invalid.dob.date")
  val INVALID_USER_ID: String = getMessage("invalid.user.id")

  val INVALID_TUITION_FEES: String = getMessage("invalid.tuition.fees")
  val INVALID_TUITION_FEES_PAID: String = getMessage("invalid.tuition.fees.paid")
  val INVALID_ACCOMMODATION_FEES_PAID: String = getMessage("invalid.accommodation.fees.paid")
  val INVALID_DEPENDANTS: String = getMessage("invalid.dependants.value")
  val INVALID_DEPENDANTS_NOTALLOWED: String = getMessage("invalid.dependants.notallowed")
  val INVALID_IN_LONDON: String = getMessage("invalid.in.london.value")
  val INVALID_IN_DEPENDANTS_ONLY: String = getMessage("invalid.dependants.only.value")

  val INVALID_SORT_CODE_VALUE = "000000"
  val INVALID_ACCOUNT_NUMBER_VALUE = "00000000"
  val INVALID_COURSE_LENGTH: String = getMessage("invalid.course.length")

  val RESOURCE_NOT_FOUND: String = getMessage("resource.not.found")
  val PATH_ERROR_MISSING_VALUE: String = getMessage("path.error.missing.value")

  val INVALID_COURSE_START_DATE: String = getMessage("invalid.course.start.date")
  val INVALID_COURSE_END_DATE: String = getMessage("invalid.course.end.date")
  val INVALID_CONTINUATION_END_DATE: String = getMessage("invalid.continuation.end.date")

  val INVALID_COURSE_START_DATE_VALUE: String = getMessage("invalid.course.start.date.value")
  val INVALID_COURSE_END_DATE_VALUE: String = getMessage("invalid.course.end.date.value")
  val INVALID_ORIGINAL_COURSE_START_DATE_VALUE: String = getMessage("invalid.original.course.start.date.value")

  val INVALID_USER_PROFILE: String = getMessage("unable.to.retrieve.user.profile")

  val INVALID_DATES: String = getMessage("invalid.dates")

  def INVALID_STUDENT_TYPE(params: String*): String = getMessage("invalid.student.type", params)

  def INVALID_COURSE_TYPE(params: String*): String = getMessage("invalid.course.type", params)

  def INVALID_APPLICANT_TYPE(params: String*): String = getMessage("invalid.applicant.type", params)
  def INVALID_VARIANT_TYPE(params: String*): String = getMessage("invalid.variant.type", params)
  def INVALID_TIER_TYPE(params: String*): String = getMessage("invalid.tier.type", params)
  def INVALID_NUM_OF_DEPENDANTS(params: String*): String = getMessage("invalid.num.of_dependants", params)
  def INVALID_ARGUMENT(params: String*): String = getMessage("invalid.argument", params)

  def USER_CONSENT_NOT_GIVEN(params: String*): String = getMessage("user.consent.not.given", params)

  def NO_RECORDS_FOR_ACCOUNT(params: String*): String = getMessage("no.records.for.account", params)

  def MISSING_PARAMETER(params: String*): String = getMessage("missing.parameter", params)

  def PARAMETER_CONVERSION_ERROR(params: String*): String = getMessage("parameter.conversion.error", params)

  val UNEXPECTED_ERROR: String = getMessage("unexpected.error")
  val OK: String = "OK"

  val REST_MISSING_PARAMETER: String = getMessage("rest.missing.parameter")
  val REST_INVALID_PARAMETER_TYPE: String = getMessage("rest.invalid.parameter.type")
  val REST_INVALID_PARAMETER_FORMAT: String = getMessage("rest.invalid.parameter.format")
  val REST_INVALID_PARAMETER_VALUE: String = getMessage("rest.invalid.parameter.value")
  val REST_INTERNAL_ERROR: String = getMessage("rest.internal.error")
  val REST_API_SERVER_ERROR: String = getMessage("rest.api.server.error")
  val REST_API_CLIENT_ERROR: String = getMessage("rest.api.client.error")
  val REST_API_CONNECTION_ERROR: String = getMessage("rest.api.connection.error")

  def getMessage(message: String): String = messageSource.getMessage(message, Nil.toArray[Object], LocaleContextHolder.getLocale)

  def getMessage[T](message: String, params: Seq[T]): String = getMessage(message, params.map(_.asInstanceOf[Object]).toArray[Object])

  def getMessage(message: String, params: Array[Object]): String = messageSource.getMessage(message, params, LocaleContextHolder.getLocale)

}
