package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

import com.fasterxml.jackson.annotation.{JsonIgnoreProperties, JsonInclude}
import com.fasterxml.jackson.annotation.JsonInclude.Include

case class Account(sortCode: String, accountNumber: String)

case class AccountDailyBalanceCheck(accountHolderName: String, fromDate: LocalDate, toDate: LocalDate, minimum: BigDecimal, pass: Boolean,
                                    @JsonInclude(Include.NON_EMPTY) failureReason: Option[BalanceCheckFailure] = None)

case class BalanceCheckFailure(@JsonInclude(Include.NON_EMPTY) lowestBalanceDate: Option[LocalDate] = None,
                               @JsonInclude(Include.NON_EMPTY) lowestBalanceValue: Option[BigDecimal] = None,
                               @JsonInclude(Include.NON_EMPTY) recordCount: Option[Int] = None)

@JsonIgnoreProperties(ignoreUnknown = true)
case class UserProfile(id: String, firstName: String, lastName: String, email: String)
