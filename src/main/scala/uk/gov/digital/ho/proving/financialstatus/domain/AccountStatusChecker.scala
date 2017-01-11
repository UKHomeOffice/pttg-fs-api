package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.{LocalDate, Period}
import java.time.temporal.ChronoUnit.DAYS


import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.acl.BankService

import scala.util.Try

@Service
class AccountStatusChecker @Autowired()(bankService: BankService, @Value("${daily-balance.days-to-check}") val numberConsecutiveDays1: Int) {

  private def areDatesConsecutive(accountDailyBalances: AccountDailyBalances, numberConsecutiveDays: Long) = {
    val dates = accountDailyBalances.balances.map {
      _.date
    }.sortWith((date1, date2) => date1.isBefore(date2))
    val consecutive = dates.sliding(2).map { case Seq(d1, d2) => d1.plusDays(1).isEqual(d2) }.toVector
    consecutive.forall(_ == true)
  }

  def checkDailyBalancesAreAboveMinimum(account: Account, fromDate: LocalDate, toDate: LocalDate,
                                        threshold: BigDecimal, dob: LocalDate, userId: String,
                                        accountHolderConsent: Boolean): Try[AccountDailyBalanceCheck] = {

    val numberConsecutiveDays = DAYS.between(fromDate, toDate) + 1 // Inclusive of last day

    Try {
      val accountDailyBalances = bankService.fetchAccountDailyBalances(account, fromDate, toDate, dob, userId, accountHolderConsent)

      if (accountDailyBalances.balances.length < numberConsecutiveDays) {
        AccountDailyBalanceCheck(accountDailyBalances.accountHolderName, fromDate, toDate, threshold, false, Some(BalanceCheckFailure(recordCount = Some(accountDailyBalances.balances.length))))
      } else {
        val minimumBalance = accountDailyBalances.balances.minBy(_.balance)
        val thresholdPassed = accountDailyBalances.balances.length == numberConsecutiveDays &&
          areDatesConsecutive(accountDailyBalances, numberConsecutiveDays) && minimumBalance.balance >= threshold

        if (minimumBalance.balance < threshold) {
          AccountDailyBalanceCheck(accountDailyBalances.accountHolderName, fromDate, toDate, threshold, thresholdPassed,
            Some(BalanceCheckFailure(Option(minimumBalance.date), Option(minimumBalance.balance))))
        } else {
          AccountDailyBalanceCheck(accountDailyBalances.accountHolderName, fromDate, toDate, threshold, thresholdPassed)
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


