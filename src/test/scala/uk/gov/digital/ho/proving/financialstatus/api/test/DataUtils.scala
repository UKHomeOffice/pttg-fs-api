package uk.gov.digital.ho.proving.financialstatus.api.test

import java.time.LocalDate

import uk.gov.digital.ho.proving.financialstatus.domain.AccountDailyBalance

object DataUtils {

  def randomDailyBalances(date: LocalDate, num: Int, lower: Float, upper: Float) = {
     0 to num map { index =>
       val randomValue = (scala.util.Random.nextFloat() * (upper - lower)) + lower
       AccountDailyBalance(date.minusDays(index), BigDecimal(randomValue))
    }
  }

}
