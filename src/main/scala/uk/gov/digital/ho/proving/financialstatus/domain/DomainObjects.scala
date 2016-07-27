package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

case class Account(sortCode: String, accountNumber: String)

case class AccountDailyBalances(balances: Seq[AccountDailyBalance])

case class AccountDailyBalance(date: LocalDate, balance: BigDecimal)

case class AccountDailyBalanceCheck(fromDate: LocalDate, toDate: LocalDate, minimum: BigDecimal, pass: Boolean,
                                    @JsonInclude(Include.NON_EMPTY) dateFundsNotMet: Option[LocalDate],
                                    @JsonInclude(Include.NON_EMPTY) amount: Option[BigDecimal]) {
  def apply(fromDate: LocalDate, toDate: LocalDate, minimum: BigDecimal, pass: Boolean): Unit = {
    //TODO write serializer for option (or other way of removing nulls)
    AccountDailyBalanceCheck(fromDate, toDate, minimum, pass: Boolean, None, None)
  }
}
