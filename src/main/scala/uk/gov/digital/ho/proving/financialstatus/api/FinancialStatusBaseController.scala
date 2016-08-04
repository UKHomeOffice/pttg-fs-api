package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.util.{Locale, Optional}

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.{HttpHeaders, MediaType}

trait FinancialStatusBaseController  {

  val BIG_DECIMAL_SCALE = 2

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
