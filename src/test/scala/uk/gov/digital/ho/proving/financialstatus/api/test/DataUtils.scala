package uk.gov.digital.ho.proving.financialstatus.api.test

import java.math.{BigDecimal => JBigDecimal}
import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS

import uk.gov.digital.ho.proving.financialstatus.api.CappedValues
import uk.gov.digital.ho.proving.financialstatus.audit.{LoggingAuditEventBsonMapper, NewLineRemover}
import uk.gov.digital.ho.proving.financialstatus.bank.{DailyBalance, DailyBalances}
import uk.gov.digital.ho.proving.financialstatus.domain.UserProfile

import scala.util.Random.{nextBoolean, nextFloat, nextInt}

object DataUtils {


  private def generateLowerandUpperIndexValues(num: Int) =
    if (nextBoolean) {
      (nextInt(num / 2), nextInt(num / 2) * 2)
    } else {
      (nextInt(num / 2) * 2, nextInt(num / 2))
    }

  def generateRandomDailyBalances(fromDate: LocalDate, toDate: LocalDate, lower: Float, upper: Float,
                                  forceLower: Boolean = false, forceUpper: Boolean = false): Seq[DailyBalance] = {
    val (lowerIndex, upperIndex) = generateLowerandUpperIndexValues(DAYS.between(fromDate, toDate).toInt)

    val randomValues = (0 to DAYS.between(fromDate, toDate).toInt) map { index =>
      val randomValue = if (forceLower && index == lowerIndex) lower
      else if (forceUpper && index == upperIndex) upper
      else (nextFloat * (upper - lower)) + lower

      DailyBalance(toDate.minusDays(index), BigDecimal(randomValue.toDouble).setScale(2, BigDecimal.RoundingMode.HALF_UP))
    }
    randomValues
  }

  def generateDailyBalancesForFail(fromDate: LocalDate, toDate: LocalDate, lower: Float, upper: Float, amount: Float, offset: Int): DailyBalances = {
    val (lowerIndex, upperIndex) = generateLowerandUpperIndexValues(DAYS.between(fromDate, toDate).toInt)

    val dailyBalances = (0 to DAYS.between(fromDate, toDate).toInt) map { index =>
      val value = if (index == offset) BigDecimal(amount.toDouble) else BigDecimal(((nextFloat * (upper - lower)) + lower).toDouble)
      DailyBalance(toDate.minusDays(index), value.setScale(2, BigDecimal.RoundingMode.HALF_UP))
    }

    DailyBalances("Fred Flintstone", dailyBalances)
  }

  def generateRandomBankResponseOK(fromDate: LocalDate, toDate: LocalDate, lower: Float, upper: Float,
                                   forceLower: Boolean = false, forceUpper: Boolean = false): DailyBalances = {
    val dailyBalances = generateRandomDailyBalances(fromDate, toDate, lower, upper, forceLower, forceUpper)
    DailyBalances("Fred Flintstone", dailyBalances)
  }

  def generateRandomBankResponseNonConsecutiveDates(fromDate: LocalDate, toDate: LocalDate, lower: Float,
                                                    upper: Float, forceLower: Boolean = false, forceUpper: Boolean = false): DailyBalances = {
    val dailyBalances = generateRandomDailyBalances(fromDate, toDate, lower, upper, forceLower, forceUpper)
    val variance = if (nextBoolean()) 1 else 2
    dailyBalances.map(dailyBalance => DailyBalance(dailyBalance.date.plusDays(variance), dailyBalance.balance))
    DailyBalances("Fred Flintstone", dailyBalances)
  }

  def generateDate(startDate: LocalDate, years: Int = 0, months: Int = 1, days: Int = 0): LocalDate = startDate.plusYears(years).plusMonths(months).plusDays(days)

  // Groovy has issues comparing Scala's Option class
  // so these are just a helper methods

  def compareAccommodationFees(first: BigDecimal, second: Option[BigDecimal]): Boolean = {
    first.setScale(2, BigDecimal.RoundingMode.HALF_UP) == second.getOrElse(new BigDecimal(JBigDecimal.ZERO)).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  }

  def compareCourseLength(first: Int, second: Option[Int]): Boolean = {
    first == second.getOrElse(0)
  }

  def getCappedValues(cappedValues: Option[CappedValues]): CappedValues = cappedValues.getOrElse(CappedValues(Some(new BigDecimal(JBigDecimal.ZERO)), Some(0)))

  def buildScalaOption[T](value: T): Option[T] = Option(value)

  def buildScalaBigDecimal(value: java.math.BigDecimal): BigDecimal =  new scala.math.BigDecimal(value)

  def buildUserProfile(id: String) = UserProfile(id, "Fred", "Flintstone", "fred@bedrock.com")

  def createAuditEventBsonMapper(): LoggingAuditEventBsonMapper = new LoggingAuditEventBsonMapper {}

  def createNewLineRemover(): NewLineRemover = new NewLineRemover {}
}
