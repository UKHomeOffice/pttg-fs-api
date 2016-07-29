package uk.gov.digital.ho.proving.financialstatus.api.test

import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS

import uk.gov.digital.ho.proving.financialstatus.domain.{AccountDailyBalance, AccountDailyBalances}

import scala.util.Random.{nextBoolean, nextFloat, nextInt}

object DataUtils {


  private def generateLowerandUpperIndexValues(num: Int) =
    if (nextBoolean) {
      (nextInt(num / 2), nextInt(num / 2) * 2)
    } else {
      (nextInt(num / 2) * 2, nextInt(num / 2))
    }

  def generateRandomDailyBalances(fromDate: LocalDate, toDate: LocalDate, lower: Float, upper: Float,
                                  forceLower: Boolean = false, forceUpper: Boolean = false): Seq[AccountDailyBalance] = {
    val (lowerIndex, upperIndex) = generateLowerandUpperIndexValues(DAYS.between(fromDate, toDate).toInt)

    val randomValues = (0 to DAYS.between(fromDate, toDate).toInt) map { index =>
      val randomValue = if (forceLower && index == lowerIndex) lower
      else if (forceUpper && index == upperIndex) upper
      else (nextFloat * (upper - lower)) + lower

      AccountDailyBalance(toDate.minusDays(index), BigDecimal(randomValue.toDouble).setScale(2, BigDecimal.RoundingMode.HALF_UP))
    }
    randomValues
  }

  def generateDailyBalancesForFail(fromDate: LocalDate, toDate: LocalDate, lower: Float, upper: Float, amount: Float, offset: Int): AccountDailyBalances = {
    val (lowerIndex, upperIndex) = generateLowerandUpperIndexValues(DAYS.between(fromDate, toDate).toInt)

    val dailyBalances = (0 to DAYS.between(fromDate, toDate).toInt) map { index =>
      val value = if (index == offset) BigDecimal(amount.toDouble) else BigDecimal((nextFloat * (upper - lower)) + lower)
      AccountDailyBalance(toDate.minusDays(index), value.setScale(2, BigDecimal.RoundingMode.HALF_UP))
    }

    AccountDailyBalances(dailyBalances)
  }

  def generateRandomBankResponseOK(fromDate: LocalDate, toDate: LocalDate, lower: Float, upper: Float,
                                   forceLower: Boolean = false, forceUpper: Boolean = false): AccountDailyBalances = {
    val dailyBalances = generateRandomDailyBalances(fromDate, toDate, lower, upper, forceLower, forceUpper)
    AccountDailyBalances(dailyBalances)
  }

  def generateRandomBankResponseNonConsecutiveDates(fromDate: LocalDate, toDate: LocalDate, lower: Float,
                                                    upper: Float, forceLower: Boolean = false, forceUpper: Boolean = false): AccountDailyBalances = {
    val dailyBalances = generateRandomDailyBalances(fromDate, toDate, lower, upper, forceLower, forceUpper)
    val variance = if (nextBoolean()) 1 else 2
    dailyBalances.map(dailyBalance => AccountDailyBalance(dailyBalance.date.plusDays(variance), dailyBalance.balance))
    AccountDailyBalances(dailyBalances)
  }

}
