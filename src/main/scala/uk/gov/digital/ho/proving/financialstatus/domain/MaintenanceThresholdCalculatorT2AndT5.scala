package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class MaintenanceThresholdCalculatorT2AndT5 @Autowired()(@Value("${t2t5.main.applicant.value}") val mainApplicantValue: Int,
                                                         @Value("${t2t5.dependant.applicant.value}") val dependantApplicantLength: Int) {

  private val MAIN_APPLICANT_VALUE = BigDecimal(mainApplicantValue).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  private val DEPENDANT_APPLICANT_VALUE = BigDecimal(dependantApplicantLength).setScale(2, BigDecimal.RoundingMode.HALF_UP)

  def calculateThresholdForT2AndT5(applicantType: ApplicantType): Option[BigDecimal] = calculateThresholdForT2AndT5(applicantType, 0)

  def calculateThresholdForT2AndT5(applicantType: ApplicantType, dependants: Int): Option[BigDecimal] = {
    applicantType match {
      case MainApplicant => Some(MAIN_APPLICANT_VALUE + (DEPENDANT_APPLICANT_VALUE * dependants.max(0)))
      case DependantApplicant => Some(DEPENDANT_APPLICANT_VALUE)
      case UnknownApplicant(_) => None
    }
  }

  def parameters: String = {
    s"""
       | ---------- External parameters values ----------
       |      t2t5.main.applicant.value = $MAIN_APPLICANT_VALUE
       | t2t5.dependant.applicant.value = $DEPENDANT_APPLICANT_VALUE
     """.stripMargin
  }

}
