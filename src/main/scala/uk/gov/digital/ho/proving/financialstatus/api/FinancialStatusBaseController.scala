package uk.gov.digital.ho.proving.financialstatus.api

import java.util.Locale

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import uk.gov.digital.ho.proving.financialstatus.monitor.{Auditor, Timer}

trait FinancialStatusBaseController extends Auditor with Timer {

  val messageSource: ResourceBundleMessageSource

  def getMessage(message: String): String = messageSource.getMessage(message, null, LocaleContextHolder.getLocale)

  def getMessage(message: String, params: Array[Object]): String = messageSource.getMessage(message, params, LocaleContextHolder.getLocale)

  def getMessage(message: String, params: Array[Object], locale: Locale): String = messageSource.getMessage(message, params, locale)

  def getMessage[T](message: String, params: Seq[T]): String = getMessage(message, params.map(_.asInstanceOf[Object]).toArray[Object])

  def getMessage[T](message: String, params: Seq[T], locale: Locale): String = getMessage(message, params.map(_.asInstanceOf[Object]).toArray[Object], locale)

  def logStartupInformation(): Unit = {}
}
