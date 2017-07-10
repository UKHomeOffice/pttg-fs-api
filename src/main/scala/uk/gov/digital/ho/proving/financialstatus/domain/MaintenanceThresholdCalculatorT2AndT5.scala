package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class MaintenanceThresholdCalculatorT2AndT5 @Autowired()(@Value("${t2t5.main.applicant.value}") val mainApplicantValue: Int,
                                                         @Value("${t2t5.dependant.applicant.value}") val dependantApplicantLength: Int,
                                                         @Value("${t5.youth.mobility.variant.amount}") val youthMobilityApplicantValue: Int) {

  private val MAIN_APPLICANT_VALUE = BigDecimal(mainApplicantValue).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  private val DEPENDANT_APPLICANT_VALUE = BigDecimal(dependantApplicantLength).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  private val YOUTH_MOBILITY_APPLICANT_VALUE = BigDecimal(youthMobilityApplicantValue).setScale(2, BigDecimal.RoundingMode.HALF_UP)

  def calculateThresholdForT2AndT5(tier: Tier, applicantType: ApplicantType, variantTypeOptional: Option[VariantType]): Option[BigDecimal] = calculateThresholdForT2AndT5(tier, applicantType, variantTypeOptional, 0)

  def calculateThresholdForT2AndT5(tier: Tier, applicantType: ApplicantType, variantTypeOptional: Option[VariantType], dependants: Int): Option[BigDecimal] = {

    if (tier == Tier5 && variantTypeOptional.nonEmpty && variantTypeOptional.get == T5YouthMobilityVariant) {
      Option(YOUTH_MOBILITY_APPLICANT_VALUE)
    } else {
      applicantType match {
        case MainApplicant => Some(MAIN_APPLICANT_VALUE + (DEPENDANT_APPLICANT_VALUE * dependants.max(0)))
        case DependantApplicant => Option(DEPENDANT_APPLICANT_VALUE * dependants.max(0))
      }
    }
  }

  def parameters: String = {
    s"""
       | ---------- External parameters values ----------
       |      t2t5.main.applicant.value = $MAIN_APPLICANT_VALUE
       | t2t5.dependant.applicant.value = $DEPENDANT_APPLICANT_VALUE
       | t5.youthmobility.applicant.value = $YOUTH_MOBILITY_APPLICANT_VALUE
     """.stripMargin
  }

}
