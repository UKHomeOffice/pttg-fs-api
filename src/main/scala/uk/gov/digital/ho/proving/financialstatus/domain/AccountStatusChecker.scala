package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate
import uk.gov.digital.ho.proving.financialstatus.acl.BankService

class AccountStatusChecker(bankService: BankService) {

  private def areDatesConsecutive(accountDailyBalances: AccountDailyBalances) = {
    val dates = accountDailyBalances.balances.map { _.date }.sortWith((date1, date2) => date1.isBefore(date2))
    val consecutive = dates.sliding(2).map { case Seq(d1, d2) => d1.plusDays(1).isEqual(d2) }.toVector
    consecutive.forall(_ == true)
  }

  def checkDailyBalancesAreAboveThreshold(account: Account, applicationRaisedDate: LocalDate, days: Int, threshold: BigDecimal) = {

    val assessmentStartDate = applicationRaisedDate.minusDays(days)
    val accountDailyBalances = bankService.fetchAccountDailyBalances(account, assessmentStartDate, applicationRaisedDate)

    val thresholdPassed = accountDailyBalances.balances.length == days &&
      areDatesConsecutive(accountDailyBalances) &&
      !accountDailyBalances.balances.exists(balance => balance.balance < threshold)

    AccountDailyBalanceCheck(applicationRaisedDate, assessmentStartDate, threshold, thresholdPassed)

  }

}
