package uk.gov.digital.ho.proving.financialstatus.api.validation

import uk.gov.digital.ho.proving.financialstatus.api.FinancialStatusBaseController

trait ServiceMessages {
  this: FinancialStatusBaseController =>

  // TODO Temporary error code until these are finalised or removed
  val TEMP_ERROR_CODE = "0000"

  val INVALID_ACCOUNT_NUMBER = getMessage("invalid.account.number")
  val INVALID_SORT_CODE = getMessage("invalid.sort.code")
  val INVALID_MINIMUM_VALUE = getMessage("invalid.minimum.value")

  val CONNECTION_TIMEOUT = getMessage("connection.timeout")
  val CONNECTION_REFUSED = getMessage("connection.refused")
  val UNKNOWN_CONNECTION_EXCEPTION = getMessage("unknown.connection.exception")
  val INVALID_FROM_DATE = getMessage("invalid.from.date")
  val INVALID_TO_DATE = getMessage("invalid.to.date")

  val INVALID_TUITION_FEES = getMessage("invalid.tuition.fees")
  val INVALID_TUITION_FEES_PAID = getMessage("invalid.tuition.fees.paid")
  val INVALID_ACCOMMODATION_FEES_PAID = getMessage("invalid.accommodation.fees.paid")
  val INVALID_DEPENDANTS = getMessage("invalid.dependants.value")
  val INVALID_IN_LONDON = getMessage("invalid.in.london.value")

  val INVALID_SORT_CODE_VALUE = "000000"
  val INVALID_ACCOUNT_NUMBER_VALUE = "00000000"
  val INVALID_COURSE_LENGTH = getMessage("invalid.course.length")

  def INVALID_DATES(params: Int*) = getMessage("invalid.dates", params)
  def INVALID_STUDENT_TYPE(params: String*) = getMessage("invalid.student.type", params)
  def NO_RECORDS_FOR_ACCOUNT(params: String*) = getMessage("no.records.for.account", params)

  val UNEXPECTED_ERROR = getMessage("unexpected.error")
  val OK = "OK"
}
