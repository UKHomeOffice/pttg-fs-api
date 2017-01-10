package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class ApplicantTypeChecker @Autowired()(@Value("${t2t5.applicant.type.main}") val main: String,
                                        @Value("${t2t5.applicant.type.dependant}") val dependant: String
                                       ) {

  private val MAIN = main
  private val DEPENDANT = dependant

  val values = Vector(MAIN, DEPENDANT)

  def getApplicantType(courseType: String): ApplicantType = {
    courseType.toLowerCase() match {
      case MAIN => MainApplicant
      case DEPENDANT => DependantApplicant
      case _ => UnknownApplicant(courseType)
    }
  }
}

sealed trait ApplicantType
case class UnknownApplicant(value: String) extends ApplicantType
case object MainApplicant extends ApplicantType
case object DependantApplicant extends ApplicantType
