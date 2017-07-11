package uk.gov.digital.ho.proving.financialstatus.bank

import java.time.LocalDate

import org.springframework.http.HttpStatus

case class BankResponse(httpStatus: HttpStatus, dailyBalances: DailyBalances)

case class DailyBalances(accountHolderName: String, balanceRecords: Seq[DailyBalance])

case class DailyBalance(date: LocalDate, balance: BigDecimal)
