package uk.gov.digital.ho.proving.financialstatus.api

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.{JsonInclude, JsonUnwrapped}
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalanceCheck}

case class StatusResponse(code: String, message: String)

case class CappedValues(@JsonInclude(Include.NON_EMPTY) accommodationFeesPaid: Option[BigDecimal],
                        @JsonInclude(Include.NON_EMPTY) courseLength: Option[Int])

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
  def apply(status: StatusResponse): ThresholdResponse = ThresholdResponse(None, None, status)
}

case class ThresholdResponse(@JsonInclude(Include.NON_EMPTY) threshold: Option[BigDecimal],
                             @JsonInclude(Include.NON_EMPTY) cappedValues: Option[CappedValues],
                             @JsonInclude(Include.NON_NULL) status: StatusResponse)

