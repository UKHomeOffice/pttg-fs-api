package uk.gov.digital.ho.proving.financialstatus.acl

import java.time.LocalDate

import org.springframework.http.HttpStatus

case class BankResponse(httpStatus: HttpStatus, dailyBalances: DailyBalances)
case class DailyBalances(var transactions: Seq[DailyBalance])
case class DailyBalance(var date: LocalDate, var balance: BigDecimal)
