package uk.gov.digital.ho.proving.financialstatus.api

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

case class ResponseStatus(code: String, message: String)

class BaseResponse( status: ResponseStatus = null)

case class TransactionsResponse(account: Account, @JsonInclude(Include.NON_NULL) transactions: AccountTransaction, @JsonInclude(Include.NON_NULL) status: ResponseStatus) extends BaseResponse(status)
