package uk.gov.digital.ho.proving.financialstatus.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountDailyBalance, AccountDailyBalanceCheck}

import scala.beans.BeanProperty

case class StatusResponse(code: String, message: String)

class BaseResponse(status: StatusResponse = null)

case class AccountDailyBalanceCheckResponse(account: Account, @JsonInclude(Include.NON_NULL) dailyBalanceCheck: AccountDailyBalanceCheck, @JsonInclude(Include.NON_NULL) status: StatusResponse) extends BaseResponse(status)
