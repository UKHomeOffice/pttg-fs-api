package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApplicantTypeException

@Service
class ApplicantTypeChecker @Autowired() (@Value("${t2t5.applicant.type.main}") val main: String,
                                          @Value("${t2t5.applicant.type.dependant}") val dependant: String) {

  private val MAIN = main
  private val DEPENDANT = dependant

  val values = Vector(MAIN, DEPENDANT)

  def getApplicantType(applicantType: String): ApplicantType = {

    applicantType.toLowerCase() match {
      case MAIN => MainApplicant
      case DEPENDANT => DependantApplicant
      case _ => throw ApplicantTypeException("Invalid applicant type: " + applicantType)
    }
  }

  def getApplicantTypeName(applicantType: ApplicantType): String = {
    applicantType match {
      case MainApplicant => MAIN
      case DependantApplicant => DEPENDANT
    }
  }
}

sealed trait ApplicantType

case object MainApplicant extends ApplicantType
case object DependantApplicant extends ApplicantType
