package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

case class Account(sortCode: String, accountNumber: String)

case class AccountDailyBalances(accountHolderName: String, balances: Seq[AccountDailyBalance])

case class AccountDailyBalance(date: LocalDate, balance: BigDecimal)

case class AccountDailyBalanceCheck(accountHolderName: String, fromDate: LocalDate, toDate: LocalDate, minimum: BigDecimal, pass: Boolean,
                                    @JsonInclude(Include.NON_EMPTY) failureReason: Option[BalanceCheckFailure] = None)

case class BalanceCheckFailure(@JsonInclude(Include.NON_EMPTY) lowestBalanceDate: Option[LocalDate] = None,
                               @JsonInclude(Include.NON_EMPTY) lowestBalanceValue: Option[BigDecimal] = None,
                               @JsonInclude(Include.NON_EMPTY) recordCount: Option[Int] = None)
