package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

case class Account(sortCode: String, accountNumber: String)
case class AccountDailyBalance(date: LocalDate, balance: BigDecimal)
case class AccountDailyBalanceCheck(applicationRaisedDate: LocalDate, assessmentStartDate: LocalDate, threshold: BigDecimal, minimumAboveThreshold: Boolean)
