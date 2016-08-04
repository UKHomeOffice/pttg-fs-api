package uk.gov.digital.ho.proving.financialstatus.acl

import java.time.LocalDate

import com.fasterxml.jackson.databind.ObjectMapper
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalances}

trait BankService {

  val bankName: String
  val bankUrl: String
  val objectMapper: ObjectMapper

  def fetchAccountDailyBalances(account: Account, fromDate: LocalDate, toDate: LocalDate): AccountDailyBalances

  def buildUrl(account: Account, fromDate: LocalDate, toDate: LocalDate): String

}
