package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.acl.BankService

import scala.util.Try

@Service
class AccountStatusChecker @Autowired()(bankService: BankService, @Value("${daily-balance.days-to-check}") val numberConsecutiveDays: Int) {

  private def areDatesConsecutive(accountDailyBalances: AccountDailyBalances) = {
    val dates = accountDailyBalances.balances.map {
      _.date
    }.sortWith((date1, date2) => date1.isBefore(date2))
    val consecutive = dates.sliding(2).map { case Seq(d1, d2) => d1.plusDays(1).isEqual(d2) }.toVector
    consecutive.forall(_ == true)
  }

  def checkDailyBalancesAreAboveMinimum(account: Account, fromDate: LocalDate, toDate: LocalDate,
                                        threshold: BigDecimal, dob: LocalDate, userId: String,
                                        accountHolderConsent: Boolean): Try[AccountDailyBalanceCheck] = {

    Try {
      val accountDailyBalances = bankService.fetchAccountDailyBalances(account, fromDate, toDate, dob, userId, accountHolderConsent)

      if (accountDailyBalances.balances.length < numberConsecutiveDays) {
        AccountDailyBalanceCheck(fromDate, toDate, threshold, false, Some(BalanceCheckFailure(recordCount = Some(accountDailyBalances.balances.length))))
      } else {
        val minimumBalance = accountDailyBalances.balances.minBy(_.balance)
        val thresholdPassed = accountDailyBalances.balances.length == numberConsecutiveDays &&
          areDatesConsecutive(accountDailyBalances) && minimumBalance.balance >= threshold

        if (minimumBalance.balance < threshold) {
          AccountDailyBalanceCheck(fromDate, toDate, threshold, thresholdPassed,
            Some(BalanceCheckFailure(Option(minimumBalance.date), Option(minimumBalance.balance))))
        } else {
          AccountDailyBalanceCheck(fromDate, toDate, threshold, thresholdPassed)
        }
      }
    }
  }

  def parameters: String = {
    s"""
       | ---------- External parameters values ----------
       |     daily-balance.days-to-check = $numberConsecutiveDays
     """.stripMargin
  }
}


