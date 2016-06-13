package uk.gov.digital.ho.proving.financialstatus.api.test

import java.time.LocalDate

import uk.gov.digital.ho.proving.financialstatus.domain.{AccountDailyBalance, AccountDailyBalances}

import scala.util.Random.{nextBoolean, nextFloat, nextInt}

object DataUtils {

  private def getLowerandUpperIndexValues(num: Int) =
    if (nextBoolean) {
      (nextInt(num / 2), nextInt(num / 2) * 2)
    } else {
      (nextInt(num / 2) * 2, nextInt(num / 2))
    }

  def randomDailyBalances(date: LocalDate, num: Int, lower: Float, upper: Float, forceLower: Boolean = false, forceUpper: Boolean = false) = {
    val (lowerIndex, upperIndex) = getLowerandUpperIndexValues(num)

    val randomValues = 0 to num map { index =>
      val randomValue = if (forceLower && index == lowerIndex) lower
      else if (forceUpper && index == upperIndex) upper
      else (nextFloat * (upper - lower)) + lower

      AccountDailyBalance(date.minusDays(index), BigDecimal(randomValue.toDouble).setScale(2, BigDecimal.RoundingMode.HALF_UP))
    }
    randomValues
  }

  def randomBankResponseOK(date: LocalDate, num: Int, lower: Float, upper: Float, forceLower: Boolean = false, forceUpper: Boolean = false) = {
    val dailyBalances = randomDailyBalances(date, num, lower, upper, forceLower, forceUpper)
    AccountDailyBalances(dailyBalances)
  }

  def randomBankResponseNonConsecutiveDates(date: LocalDate, num: Int, lower: Float, upper: Float, forceLower: Boolean = false, forceUpper: Boolean = false) = {
    val dailyBalances = randomDailyBalances(date, num, lower, upper, forceLower, forceUpper)
    val variance = if (nextBoolean()) 1 else 2
    dailyBalances.map( dailyBalance => AccountDailyBalance(dailyBalance.date.plusDays(variance), dailyBalance.balance))
    AccountDailyBalances(dailyBalances)
  }

}
