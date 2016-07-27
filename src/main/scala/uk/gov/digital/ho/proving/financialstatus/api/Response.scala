package uk.gov.digital.ho.proving.financialstatus.api

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonInclude, JsonUnwrapped}
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalanceCheck}

case class StatusResponse(code: String, message: String)
/*
  Providing two more constructors which pass null as the value for the 'account' and 'dailyBalanceCheck' fields.  This
  is to stop nulls leaking into the main Scala codebase.

  Jackson's Scala module serializes None to 'null' which we don't want, so we have to set the value of these to null
  and use the @JsonInclude(Include.NON_NULL) annotation to ignore them.
 */
case object AccountDailyBalanceStatusResponse {
  def apply(dailyBalanceCheck: Option[AccountDailyBalanceCheck], status: StatusResponse): AccountDailyBalanceStatusResponse = {
    AccountDailyBalanceStatusResponse(None, dailyBalanceCheck, status)
  }

  def apply(status: StatusResponse): AccountDailyBalanceStatusResponse = {
    AccountDailyBalanceStatusResponse(None, None, status)
  }
}
case class AccountDailyBalanceStatusResponse(@JsonInclude(Include.NON_EMPTY) account: Option[Account],
                                             @JsonInclude(Include.NON_EMPTY) @JsonUnwrapped dailyBalanceCheck: Option[AccountDailyBalanceCheck],
                                             @JsonInclude(Include.NON_EMPTY) status: StatusResponse)

case object ThresholdResponse {
  def apply(status: StatusResponse): ThresholdResponse = ThresholdResponse(None, status)
}
case class ThresholdResponse(@JsonInclude(Include.NON_EMPTY)threshold: Option[BigDecimal], @JsonInclude(Include.NON_NULL) status: StatusResponse)

