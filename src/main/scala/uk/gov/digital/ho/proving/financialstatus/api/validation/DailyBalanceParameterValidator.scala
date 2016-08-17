package uk.gov.digital.ho.proving.financialstatus.api.validation

import java.time.LocalDate

import org.springframework.http.HttpStatus

import scala.util.{Either, Left, Right}

trait DailyBalanceParameterValidator {

  val serviceMessages: ServiceMessages

  val sortCodePattern = """^[0-9]{6}$""".r
  val accountNumberPattern = """^[0-9]{8}$""".r

  protected def validateInputs(sortCode: Option[String],
                               accountNumber: Option[String],
                               minimum: Option[BigDecimal],
                               fromDate: Option[LocalDate],
                               toDate: Option[LocalDate],
                               numberConsecutiveDays: Int,
                               dob: Option[LocalDate],
                               userId: Option[String],
                               accountHolderConsent: Option[Boolean]
                              ): Either[Seq[(String, String, HttpStatus)], ValidatedInputs] = {

    var errorList = Vector.empty[(String, String, HttpStatus)]
    val validSortCode = validateSortCode(sortCode)
    val validAccountNumber = validateAccountNumber(accountNumber)
    val validMinimum = validateMinimum(minimum)
    val validFromDate = validateDate(fromDate)
    val validToDate = validateDate(toDate)
    val validDob = validateDate(dob)
    val validUserId = validateUserId(userId)
    val validAccountHolderConsent = validateAccountHolderConsent(accountHolderConsent)

    if (validSortCode.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_SORT_CODE, HttpStatus.BAD_REQUEST))
    } else if (validAccountNumber.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST))
    } else if (validMinimum.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_MINIMUM_VALUE, HttpStatus.BAD_REQUEST))
    } else if (validFromDate.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_FROM_DATE, HttpStatus.BAD_REQUEST))
    } else if (validToDate.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_TO_DATE, HttpStatus.BAD_REQUEST))
    } else if (validDob.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_DOB_DATE, HttpStatus.BAD_REQUEST))
    } else if (validUserId.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_USER_ID, HttpStatus.BAD_REQUEST))
    } else if (validAccountHolderConsent.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_ACCOUNT_HOLDER_CONSENT, HttpStatus.BAD_REQUEST))
    }
    else {
      for {from <- fromDate
           to <- toDate
      } yield {
        if (!from.isBefore(to) || (!from.plusDays(numberConsecutiveDays - 1).equals(to))) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_DATES(numberConsecutiveDays - 1), HttpStatus.BAD_REQUEST))
        }
      }
    }

    if (errorList.isEmpty)
      Right(ValidatedInputs(validSortCode, validAccountNumber, validMinimum, validFromDate, validToDate, validDob, validUserId, validAccountHolderConsent))
    else
      Left(errorList)
  }

  private def validateAccountNumber(accountNumber: Option[String]) =
    accountNumber.filter(accNo => accountNumberPattern.findFirstIn(accNo).nonEmpty && accNo != serviceMessages.INVALID_ACCOUNT_NUMBER_VALUE)

  private def validateSortCode(sortCode: Option[String]) =
    sortCode.map(_.replace("-", "")).filter(sCode => sortCodePattern.findFirstIn(sCode).nonEmpty && sCode != serviceMessages.INVALID_SORT_CODE_VALUE)

  // At the moment we rely on Spring's date conversion to only pass a valid date
  private def validateDate(date: Option[LocalDate]) = date

  private def validateMinimum(minimum: Option[BigDecimal]) = minimum.filter(_ > 0)

  private def validateUserId(userId: Option[String]) = userId.filter(!_.isEmpty)

  private def validateAccountHolderConsent(accountHolderConsent: Option[Boolean]) = accountHolderConsent.filter(_ == true)

  case class ValidatedInputs(sortCode: Option[String],
                             accountNumber: Option[String],
                             minimum: Option[BigDecimal],
                             fromDate: Option[LocalDate],
                             toDate: Option[LocalDate],
                             dob: Option[LocalDate],
                             userId: Option[String],
                             accountHolderConsent: Option[Boolean]
                            )

}
