package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate
import uk.gov.digital.ho.proving.financialstatus.acl.BankService

class AccountStatusChecker(bankService: BankService) {

  private val REQUIRED_NUMBER_BALANCES = 28

  private def areDatesConsecutive(accountDailyBalances: AccountDailyBalances) = {
    val dates = accountDailyBalances.balances.map { _.date }.sortWith((date1, date2) => date1.isBefore(date2))
    val consecutive = dates.sliding(2).map { case Seq(d1, d2) => d1.plusDays(1).isEqual(d2) }.toVector
    consecutive.forall(_ == true)
  }

  def checkDailyBalancesAreAboveMinimum(account: Account, fromDate: LocalDate, toDate: LocalDate, threshold: BigDecimal) = {

    val accountDailyBalances = bankService.fetchAccountDailyBalances(account, fromDate, toDate)

    val thresholdPassed = accountDailyBalances.balances.length == REQUIRED_NUMBER_BALANCES &&
      areDatesConsecutive(accountDailyBalances) &&
      !accountDailyBalances.balances.exists(balance => balance.balance < threshold)

    AccountDailyBalanceCheck(fromDate, toDate, threshold, thresholdPassed)

  }

}
