package uk.gov.digital.ho.proving.financialstatus.acl

import java.time.LocalDate

import org.springframework.http.HttpStatus

case class BankResponse(httpStatus: HttpStatus, dailyBalances: DailyBalances)

case class DailyBalances(accountHolderName: String, sortCode: String, accountNumber: String, balanceRecords: Seq[DailyBalance])

case class DailyBalance(date: LocalDate, balance: BigDecimal)
