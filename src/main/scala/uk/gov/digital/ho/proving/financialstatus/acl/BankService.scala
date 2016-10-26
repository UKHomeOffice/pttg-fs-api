package uk.gov.digital.ho.proving.financialstatus.acl

import java.time.LocalDate

import com.fasterxml.jackson.databind.ObjectMapper
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalances}

trait BankService {

  val bankName: String
  val bankUrl: String
  val objectMapper: ObjectMapper

  def fetchAccountDailyBalances(account: Account, fromDate: LocalDate, toDate: LocalDate, dob: LocalDate, userId: String, accountHolderConsent: Boolean): AccountDailyBalances

  def buildUrl(account: Account, fromDate: LocalDate, toDate: LocalDate, dob: LocalDate, userId: String, accountHolderConsent: Boolean): String

}
