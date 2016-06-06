package uk.gov.digital.ho.proving.financialstatus.api

import java.time.LocalDate

case class Account(sortCode: String, number: String)

case class AccountTransaction(date: LocalDate, description: String, amount: BigDecimal, balance: BigDecimal)
