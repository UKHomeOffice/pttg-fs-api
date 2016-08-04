package uk.gov.digital.ho.proving.financialstatus.api.validation

import java.time.LocalDate

import org.springframework.http.HttpStatus
import uk.gov.digital.ho.proving.financialstatus.api.FinancialStatusBaseController

import scala.util.{Either, Left, Right}

trait DailyBalanceParameterValidator extends ServiceMessages {
  this: FinancialStatusBaseController =>

  val sortCodePattern = """^[0-9]{6}$""".r
  val accountNumberPattern = """^[0-9]{8}$""".r

  protected def validateInputs(sortCode: Option[String],
                               accountNumber: Option[String],
                               minimum: Option[BigDecimal],
                               fromDate: Option[LocalDate],
                               toDate: Option[LocalDate],
                               numberConsecutiveDays: Int): Either[Seq[(String, String, HttpStatus)], ValidatedInputs] = {

    var errorList = Vector.empty[(String, String, HttpStatus)]
    val validSortCode = validateSortCode(sortCode)
    val validAccountNumber = validateAccountNumber(accountNumber)
    val validMinimum = validateMinimum(minimum)
    val validFromDate = validateDate(fromDate)
    val validToDate = validateDate(toDate)

    if (validSortCode.isEmpty) {
      errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_SORT_CODE, HttpStatus.BAD_REQUEST))
    } else if (validAccountNumber.isEmpty) {
      errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST))
    } else if (validMinimum.isEmpty) {
      errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_MINIMUM_VALUE, HttpStatus.BAD_REQUEST))
    } else if (validFromDate.isEmpty) {
      errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_FROM_DATE, HttpStatus.BAD_REQUEST))
    } else if (validToDate.isEmpty) {
      errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_TO_DATE, HttpStatus.BAD_REQUEST))
    } else {
        for {from <- fromDate
             to <- toDate
        } yield {
          if (!from.isBefore(to) || (!from.plusDays(numberConsecutiveDays - 1).equals(to))) {
            errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_DATES(numberConsecutiveDays - 1), HttpStatus.BAD_REQUEST))
          }
        }
    }

    if (errorList.isEmpty)
      Right(ValidatedInputs(validSortCode, validAccountNumber, validMinimum, validFromDate, validToDate))
    else
      Left(errorList)
  }

  private def validateAccountNumber(accountNumber: Option[String]) =
    accountNumber.filter(accNo => accountNumberPattern.findFirstIn(accNo).nonEmpty && accNo != INVALID_ACCOUNT_NUMBER_VALUE)

  private def validateSortCode(sortCode: Option[String]) =
    sortCode.map(_.replace("-","")).filter(sCode => sortCodePattern.findFirstIn(sCode).nonEmpty && sCode != INVALID_SORT_CODE_VALUE)

  private def validateDate(date: Option[LocalDate]) = date

  private def validateMinimum(minimum: Option[BigDecimal]) = minimum.filter(_ > 0)

  case class ValidatedInputs(sortCode: Option[String], accountNumber: Option[String], minimum: Option[BigDecimal], fromDate: Option[LocalDate], toDate: Option[LocalDate])

}
