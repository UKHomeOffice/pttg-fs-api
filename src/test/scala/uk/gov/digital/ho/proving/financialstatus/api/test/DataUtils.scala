package uk.gov.digital.ho.proving.financialstatus.api.test

import java.time.LocalDate

import uk.gov.digital.ho.proving.financialstatus.api.ServiceConfiguration
import uk.gov.digital.ho.proving.financialstatus.domain.{AccountDailyBalance, AccountDailyBalances}

import scala.beans.BeanProperty
import scala.util.Random.{nextBoolean, nextFloat, nextInt}

object DataUtils {

  case class StubBalance(date: LocalDate, balance: BigDecimal)
  case class StubAccount(firstname: String, surname: String, sortCode: String, accountNumber: String, balances: Seq[StubBalance])

  private val serviceConfig = new ServiceConfiguration()
  private val converter = serviceConfig.mappingJackson2HttpMessageConverter
  private val mapper = serviceConfig.requestMappingHandlerAdapter
  private val objectMapper = serviceConfig.objectMapper

  private def generateLowerandUpperIndexValues(num: Int) =
    if (nextBoolean) {
      (nextInt(num / 2), nextInt(num / 2) * 2)
    } else {
      (nextInt(num / 2) * 2, nextInt(num / 2))
    }

  def generateRandomDailyBalances(date: LocalDate, num: Int, lower: Float, upper: Float, forceLower: Boolean = false, forceUpper: Boolean = false) = {
    val (lowerIndex, upperIndex) = generateLowerandUpperIndexValues(num)

    val randomValues = 0 until num map { index =>
      val randomValue = if (forceLower && index == lowerIndex) lower
      else if (forceUpper && index == upperIndex) upper
      else (nextFloat * (upper - lower)) + lower

      AccountDailyBalance(date.minusDays(index), BigDecimal(randomValue.toDouble).setScale(2, BigDecimal.RoundingMode.HALF_UP))
    }
    randomValues
  }

  def generateRandomBankResponseOK(date: LocalDate, num: Int, lower: Float, upper: Float, forceLower: Boolean = false, forceUpper: Boolean = false) = {
    val dailyBalances = generateRandomDailyBalances(date, num, lower, upper, forceLower, forceUpper)
    AccountDailyBalances(dailyBalances)
  }

  def generateRandomBankResponseNonConsecutiveDates(date: LocalDate, num: Int, lower: Float, upper: Float, forceLower: Boolean = false, forceUpper: Boolean = false) = {
    val dailyBalances = generateRandomDailyBalances(date, num, lower, upper, forceLower, forceUpper)
    val variance = if (nextBoolean()) 1 else 2
    dailyBalances.map(dailyBalance => AccountDailyBalance(dailyBalance.date.plusDays(variance), dailyBalance.balance))
    AccountDailyBalances(dailyBalances)
  }

}
