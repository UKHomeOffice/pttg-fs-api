package uk.gov.digital.ho.proving.financialstatus.api

import java.time.LocalDate
import java.util.Optional

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonUnwrapped
import uk.gov.digital.ho.proving.financialstatus.domain.Account
import uk.gov.digital.ho.proving.financialstatus.domain.DailyBalanceCheck

case class StatusResponse(code: String, message: String)

case class CappedValues(@JsonInclude(Include.NON_EMPTY) accommodationFeesPaid: Option[BigDecimal],
                        @JsonInclude(Include.NON_EMPTY) courseLength: Option[Int] = None)

case object AccountDailyBalanceStatusResponse {
  def apply(dailyBalanceCheck: Option[DailyBalanceCheck], status: StatusResponse): AccountDailyBalanceStatusResponse = {
    AccountDailyBalanceStatusResponse(None, dailyBalanceCheck, status)
  }

  def apply(status: StatusResponse): AccountDailyBalanceStatusResponse = {
    AccountDailyBalanceStatusResponse(None, None, status)
  }
}

case class AccountDailyBalanceStatusResponse(@JsonInclude(Include.NON_EMPTY) account: Option[Account],
                                             @JsonInclude(Include.NON_EMPTY) @JsonUnwrapped dailyBalanceCheck: Option[DailyBalanceCheck],
                                             @JsonInclude(Include.NON_EMPTY) status: StatusResponse)

case object ThresholdResponse {
  def apply(status: StatusResponse): ThresholdResponse = ThresholdResponse(None, None, None, status)
}

case class ThresholdResponse(@JsonInclude(Include.NON_EMPTY) threshold: Option[BigDecimal],
                             @JsonInclude(Include.NON_EMPTY) leaveEndDate: Option[LocalDate],
                             @JsonInclude(Include.NON_EMPTY) cappedValues: Option[CappedValues],
                             @JsonInclude(Include.NON_NULL) status: StatusResponse)


case class BankConsentResponse(@JsonInclude(Include.NON_EMPTY) consent: Option[String],
                               @JsonInclude(Include.NON_NULL) status: StatusResponse)

case class ConditionCodesResponse(@JsonInclude(Include.NON_EMPTY) applicantConditionCode: Option[String],
                                  @JsonInclude(Include.NON_EMPTY) partnerConditionCode: Option[String],
                                  @JsonInclude(Include.NON_EMPTY) childConditionCode: Option[String],
                                  @JsonInclude(Include.NON_NULL) status: StatusResponse)
