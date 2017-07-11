package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.bank.{BarclaysBankService, DailyBalances}

import scala.util.Try

@Service
class AccountStatusChecker @Autowired()(barclaysBankService: BarclaysBankService,
                                        @Value("${daily-balance.days-to-check}") val numberConsecutiveDays1: Int) {

  def areDatesConsecutive(dailyBalances: DailyBalances, numberConsecutiveDays: Long): Boolean = {
    val dates = dailyBalances.balanceRecords.map {
      _.date
    }.sortWith((date1, date2) => date1.isBefore(date2))
    val consecutive = dates.sliding(2).map { case Seq(d1, d2) => d1.plusDays(1).isEqual(d2) }.toVector
    consecutive.forall(_ == true)
  }

  def checkDailyBalancesAreAboveMinimum(account: Account, fromDate: LocalDate, toDate: LocalDate,
                                        threshold: BigDecimal, dob: LocalDate, userId: String): Try[DailyBalanceCheck] = {

    val numberConsecutiveDays = DAYS.between(fromDate, toDate) + 1 // Inclusive of last day

    Try {
      val dailyBalances = barclaysBankService.fetchAccountDailyBalances(account, fromDate, toDate, dob, userId)

      if (dailyBalances.balanceRecords.length < numberConsecutiveDays) {
        DailyBalanceCheck(dailyBalances.accountHolderName,
                                  fromDate,
                                  toDate,
                                  threshold,
                                  pass = false,
                                  Some(BalanceCheckFailure(recordCount = Some(dailyBalances.balanceRecords.length))))
      } else {
        val minimumBalance = dailyBalances.balanceRecords.minBy(_.balance)
        val thresholdPassed = dailyBalances.balanceRecords.length == numberConsecutiveDays &&
          areDatesConsecutive(dailyBalances, numberConsecutiveDays) && minimumBalance.balance >= threshold

        if (minimumBalance.balance < threshold) {
          DailyBalanceCheck(dailyBalances.accountHolderName, fromDate, toDate, threshold, thresholdPassed,
            Some(BalanceCheckFailure(Option(minimumBalance.date), Option(minimumBalance.balance))))
        } else {
          DailyBalanceCheck(dailyBalances.accountHolderName, fromDate, toDate, threshold, thresholdPassed)
        }
      }
    }
  }

  def parameters: String = {
    s"""
       | ---------- External parameters values ----------
     """.stripMargin
  }
}


