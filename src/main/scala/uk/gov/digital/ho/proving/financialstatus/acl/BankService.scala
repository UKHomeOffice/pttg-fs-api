package uk.gov.digital.ho.proving.financialstatus.acl

import java.time.LocalDate

import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalance}

trait BankService {

  def fetchAccountDailyBalances(account: Account, fromDate: LocalDate, toDate: LocalDate): Seq[AccountDailyBalance]

}
