package uk.gov.digital.ho.proving.financialstatus.api

import com.fasterxml.jackson.annotation.{JsonInclude, JsonUnwrapped}
import com.fasterxml.jackson.annotation.JsonInclude.Include
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalance, AccountDailyBalanceCheck}

import scala.beans.BeanProperty

case class StatusResponse(code: String, message: String)

class BaseResponse(status: StatusResponse = null)

case class AccountDailyBalanceStatusResponse(account: Account, @JsonInclude(Include.NON_NULL) @JsonUnwrapped dailyBalanceCheck: AccountDailyBalanceCheck, @JsonInclude(Include.NON_NULL) status: StatusResponse) extends BaseResponse(status)

case class ThresholdResponse(threshold: BigDecimal,  @JsonInclude(Include.NON_NULL) status: StatusResponse) extends BaseResponse
