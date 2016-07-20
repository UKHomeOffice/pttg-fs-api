package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

case class Account(sortCode: String, accountNumber: String)
case class AccountDailyBalances(balances: Seq[AccountDailyBalance])
case class AccountDailyBalance(date: LocalDate, balance: BigDecimal)
case class AccountDailyBalanceCheck(fromDate: LocalDate, toDate: LocalDate, minimum: BigDecimal, pass: Boolean, dateFundsNotMet: LocalDate, amount: BigDecimal){
  def apply(fromDate: LocalDate, toDate: LocalDate,minimum: BigDecimal, pass: Boolean): Unit ={
    //TODO write serializer for option (or other way of removing nulls)
    AccountDailyBalanceCheck(fromDate, toDate, minimum, pass: Boolean, null, null)
  }
}
