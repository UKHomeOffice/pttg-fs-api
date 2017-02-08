package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.time.LocalDate
import java.util.Optional

import org.springframework.http.{HttpHeaders, MediaType}

trait FinancialStatusBaseController {

  val BIG_DECIMAL_SCALE = 2

  val headers = new HttpHeaders()
  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  implicit def toOptionLocalDate(optional: Optional[LocalDate]): Option[LocalDate] = if (optional.isPresent) Some(optional.get) else None

  implicit def toOptionString(optional: Optional[String]): Option[String] = if (optional.isPresent) Some(optional.get) else None

  implicit def toOptionInt(optional: Optional[Integer]): Option[Int] = if (optional.isPresent) Some(optional.get) else None

  implicit def toOptionBoolean(optional: Optional[JBoolean]): Option[Boolean] = if (optional.isPresent) Some(optional.get) else None

  implicit def toOptionBigDecimal(optional: Optional[JBigDecimal]): Option[BigDecimal] =
    if (optional.isPresent) Some(BigDecimal(optional.get).setScale(BIG_DECIMAL_SCALE, BigDecimal.RoundingMode.HALF_UP)) else None

}
