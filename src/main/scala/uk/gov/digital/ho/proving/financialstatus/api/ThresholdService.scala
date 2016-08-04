package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.util.Optional

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.api.validation.ThresholdParameterValidator
import uk.gov.digital.ho.proving.financialstatus.domain._

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatusservice/v1/maintenance"))
@ControllerAdvice
class ThresholdService @Autowired()(val maintenanceThresholdCalculator: MaintenanceThresholdCalculator,
                                    val messageSource: ResourceBundleMessageSource,
                                    val studentTypeChecker: StudentTypeChecker
                                   ) extends FinancialStatusBaseController with ThresholdParameterValidator{

  val LOGGER = LoggerFactory.getLogger(classOf[ThresholdService])
  val courseLengthPattern = """^[1-9]$""".r

  logStartupInformation()

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def calculateThreshold(@RequestParam(value = "studentType") studentType: Optional[String],
                         @RequestParam(value = "inLondon") inLondon: Optional[JBoolean],
                         @RequestParam(value = "courseLength", required = false) courseLength: Optional[Integer],
                         @RequestParam(value = "tuitionFees", required = false) tuitionFees: Optional[JBigDecimal],
                         @RequestParam(value = "tuitionFeesPaid", required = false) tuitionFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "accommodationFeesPaid") accommodationFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "dependants", required = false, defaultValue = "0") dependants: Optional[Integer]
                        ): ResponseEntity[ThresholdResponse] = {

    val validatedStudentType = studentTypeChecker.getStudentType(studentType.getOrElse("Unknown"))

    calculateThresholdForStudentType(validatedStudentType, inLondon, courseLength, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
  }

  private def calculateThresholdForStudentType(studentType: StudentType,
                                               inLondon: Option[Boolean],
                                               courseLength: Option[Int],
                                               tuitionFees: Option[BigDecimal],
                                               tuitionFeesPaid: Option[BigDecimal],
                                               accommodationFeesPaid: Option[BigDecimal],
                                               dependants: Option[Int]): ResponseEntity[ThresholdResponse] = {
    studentType match {

      case NonDoctorate =>
        val courseMinLength = maintenanceThresholdCalculator.nonDoctorateMinCourseLength
        val validatedInputs = validateInputs(NonDoctorate, inLondon, courseLength, tuitionFees, tuitionFeesPaid,
          accommodationFeesPaid, dependants, courseMinLength)

        calculateThreshold(validatedInputs, calculateNonDoctorate)

      case Doctorate =>
        val fixedCourseLength = maintenanceThresholdCalculator.doctorateFixedCourseLength
        val validatedInputs = validateInputs(Doctorate, inLondon, None, None, None,
          accommodationFeesPaid, dependants, fixedCourseLength)

        calculateThreshold(validatedInputs, calculateDoctorate)

      case DoctorDentist | StudentSabbaticalOfficer =>
        val courseMinLength = maintenanceThresholdCalculator.pgddSsoMinCourseLength
        val validatedInputs = validateInputs(DoctorDentist, inLondon, courseLength, None, None,
          accommodationFeesPaid, dependants, courseMinLength)

        calculateThreshold(validatedInputs, calculateDoctorDentistSabbatical)

      case Unknown(unknownType) =>
        buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_STUDENT_TYPE(studentTypeChecker.values.mkString(",")), HttpStatus.BAD_REQUEST)
    }
  }

  private def calculateThreshold(validatedInputs: Either[Seq[(String, String, HttpStatus)], ValidatedInputs], calculate: ValidatedInputs => Option[ThresholdResponse]) = {

    validatedInputs match {
      case Right(inputs) =>
        val thresholdResponse = calculate(inputs)
        thresholdResponse match {
          case Some(response) => new ResponseEntity[ThresholdResponse](response, HttpStatus.OK)
          case None => buildErrorResponse(headers, TEMP_ERROR_CODE, UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
        }
      case Left(errorList) =>
        // We should be returning all the error messages and not just the first
        buildErrorResponse(headers, errorList.head._1, errorList.head._2, errorList.head._3)
    }
  }

  private def calculateDoctorate(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
    } yield {
      val (threshold, cappedValues) = maintenanceThresholdCalculator.calculateDoctorate(inner, aFeesPaid, deps)
      new ThresholdResponse(Some(threshold), cappedValues, StatusResponse(HttpStatus.OK.toString, OK))
    }
  }

  private def calculateDoctorDentistSabbatical(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         length <- inputs.courseLength
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
    } yield {
      val (threshold, cappedValues) = maintenanceThresholdCalculator.calculateDesPgddSso(inner, length, aFeesPaid, deps)
      new ThresholdResponse(Some(threshold), cappedValues, StatusResponse(HttpStatus.OK.toString, OK))
    }
  }

  private def calculateNonDoctorate(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         length <- inputs.courseLength
         tFees <- inputs.tuitionFees
         tFeesPaid <- inputs.tuitionFeesPaid
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
    } yield {
      val (threshold, cappedValues) = maintenanceThresholdCalculator.calculateNonDoctorate(inner, length, tFees, tFeesPaid, aFeesPaid, deps)
      new ThresholdResponse(Some(threshold), cappedValues, StatusResponse(HttpStatus.OK.toString, OK))
    }
  }

  override def logStartupInformation(): Unit = LOGGER.info(maintenanceThresholdCalculator.parameters)

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] =
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)

}
