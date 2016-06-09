package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate
import uk.gov.digital.ho.proving.financialstatus.acl.BankService

class AccountStatusChecker(bankService: BankService) {

  def checkDailyBalancesAreAboveThreshold(account: Account, applicationRaisedDate: LocalDate, days: Int, threshold: BigDecimal) = {

    val assessmentStartDate = applicationRaisedDate.minusDays(days)
    val balances = bankService.fetchAccountDailyBalances(account, applicationRaisedDate, assessmentStartDate)
    val minimumBalance = balances.sortWith((balance1, balance2) => balance1.balance < balance2.balance).head

    if (minimumBalance.balance < threshold) {
      AccountDailyBalanceCheck(applicationRaisedDate, assessmentStartDate, threshold, false)
    } else {
      AccountDailyBalanceCheck(applicationRaisedDate, assessmentStartDate, threshold, true)
    }

  }

}
