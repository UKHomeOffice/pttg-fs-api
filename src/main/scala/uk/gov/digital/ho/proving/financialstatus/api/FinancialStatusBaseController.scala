package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.util.{Locale, Optional}

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.{HttpHeaders, MediaType}

trait FinancialStatusBaseController  {
  // TODO Temporary error code until these are finalised or removed
  val TEMP_ERROR_CODE = "0000"

  val BIG_DECIMAL_SCALE = 2

  val INVALID_ACCOUNT_NUMBER = getMessage("invalid.account.number")
  val INVALID_SORT_CODE = getMessage("invalid.sort.code")
  val INVALID_MINIMUM_VALUE = getMessage("invalid.minimum.value")

  val CONNECTION_TIMEOUT = getMessage("connection.timeout")
  val CONNECTION_REFUSED = getMessage("connection.refused")
  val UNKNOWN_CONNECTION_EXCEPTION = getMessage("unknown.connection.exception")
  val INVALID_FROM_DATE = getMessage("invalid.from.date")
  val INVALID_TO_DATE = getMessage("invalid.to.date")

  val INVALID_TUITION_FEES = getMessage("invalid.tuition.fees")
  val INVALID_TUITION_FEES_PAID = getMessage("invalid.tuition.fees.paid")
  val INVALID_ACCOMMODATION_FEES_PAID = getMessage("invalid.accommodation.fees.paid")
  val INVALID_DEPENDANTS = getMessage("invalid.dependants.value")
  val INVALID_IN_LONDON = getMessage("invalid.in.london.value")

  val INVALID_SORT_CODE_VALUE = "000000"
  val INVALID_ACCOUNT_NUMBER_VALUE = "00000000"
  val INVALID_COURSE_LENGTH = getMessage("invalid.course.length")

  def INVALID_DATES(params: Int*) = getMessage("invalid.dates", params)

  def INVALID_STUDENT_TYPE(params: String*) = getMessage("invalid.student.type", params)

  def NO_RECORDS_FOR_ACCOUNT(params: String*) = getMessage("no.records.for.account", params)

  val UNEXPECTED_ERROR = getMessage("unexpected.error")

  val OK = "OK"

  val headers = new HttpHeaders()
  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  implicit def toOption[T](optional: Optional[T]): Option[T] = if (optional.isPresent) Some(optional.get) else None

  implicit def toOptionInt(optional: Optional[Integer]): Option[Int] = if (optional.isPresent) Some(optional.get) else None

  implicit def toOptionBoolean(optional: Optional[JBoolean]): Option[Boolean] = if (optional.isPresent) Some(optional.get) else None

  implicit def toOptionBigDecimal(optional: Optional[JBigDecimal]): Option[BigDecimal] =
    if (optional.isPresent) Some(BigDecimal(optional.get).setScale(BIG_DECIMAL_SCALE, BigDecimal.RoundingMode.HALF_UP)) else None

  def messageSource: ResourceBundleMessageSource

  def getMessage(message: String): String = messageSource.getMessage(message, Nil.toArray[Object], LocaleContextHolder.getLocale)

  def getMessage(message: String, params: Array[Object]): String = messageSource.getMessage(message, params, LocaleContextHolder.getLocale)

  def getMessage(message: String, params: Array[Object], locale: Locale): String = messageSource.getMessage(message, params, locale)

  def getMessage[T](message: String, params: Seq[T]): String = getMessage(message, params.map(_.asInstanceOf[Object]).toArray[Object])

  def getMessage[T](message: String, params: Seq[T], locale: Locale): String = getMessage(message, params.map(_.asInstanceOf[Object]).toArray[Object], locale)

  def logStartupInformation(): Unit = {}
}
