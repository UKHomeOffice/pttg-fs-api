package uk.gov.digital.ho.proving.financialstatus.api.test.data

import scala.util.Random._

object RandomTestData {

  import java.time.LocalDate

  val courseStartDate = LocalDate.ofEpochDay((Math.random() * 10000).toInt)
  val courseEndDate = courseStartDate.plusDays((Math.random() * 400).toInt)
  val continuationEndDate = courseEndDate.plusDays((Math.random() * 400).toInt)

  def date = LocalDate.ofEpochDay(((Math.random() * 30000) + 1000).toInt)

  def dateAfter(date: LocalDate) = date.plusDays(((Math.random() * 400) + 1).toInt)

}
