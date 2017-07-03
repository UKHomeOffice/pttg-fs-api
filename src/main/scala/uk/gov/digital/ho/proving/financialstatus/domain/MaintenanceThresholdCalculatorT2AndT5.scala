package uk.gov.digital.ho.proving.financialstatus.domain

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class MaintenanceThresholdCalculatorT2AndT5 @Autowired()(@Value("${t2t5.main.applicant.value}") val mainApplicantValue: Int,
                                                         @Value("${t2t5.dependant.applicant.value}") val dependantApplicantLength: Int,
                                                         @Value("${t5.youthmobility.applicant.value}") val youthMobilityApplicantValue: Int) {

  private val MAIN_APPLICANT_VALUE = BigDecimal(mainApplicantValue).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  private val DEPENDANT_APPLICANT_VALUE = BigDecimal(dependantApplicantLength).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  private val YOUTHMOBILITY_APPLICANT_VALUE = BigDecimal(youthMobilityApplicantValue).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  private val YOUTHMOBILITY_APPLICANT_TYPE = Option("youth")
  private val TEMPORARY_APPLICANT_TYPE = Option("Temporary")

  private val LOGGER = LoggerFactory.getLogger(classOf[MaintenanceThresholdCalculatorT2AndT5])

  def calculateThresholdForT2AndT5(tier: String, applicantType: ApplicantType, variantType: Option[String]): Option[BigDecimal] = calculateThresholdForT2AndT5(tier, applicantType, variantType, 0)

  def calculateThresholdForT2AndT5(tier: String, applicantType: ApplicantType, variantType: Option[String], dependants: Int): Option[BigDecimal] = {
    if (variantType == Option("Temporary")) {
      LOGGER.error("\n\nCOMPARISON MATCHES\n\n")
    }

    if (tier == "t5" && variantType == YOUTHMOBILITY_APPLICANT_TYPE) {
      Option(YOUTHMOBILITY_APPLICANT_VALUE)
//      variantType match {
//        case YOUTHMOBILITY_APPLICANT_TYPE => Option(YOUTHMOBILITY_APPLICANT_VALUE)
//        case TEMPORARY_APPLICANT_TYPE =>
//          applicantType match {
//            case MainApplicant => Some(MAIN_APPLICANT_VALUE + (DEPENDANT_APPLICANT_VALUE * dependants.max(0)))
//            case DependantApplicant => Option(DEPENDANT_APPLICANT_VALUE * dependants.max(0))
//            case UnknownApplicant(_) => None
//          }
//      }
    } else {
      applicantType match {
        case MainApplicant => Some(MAIN_APPLICANT_VALUE + (DEPENDANT_APPLICANT_VALUE * dependants.max(0)))
        case DependantApplicant => Option(DEPENDANT_APPLICANT_VALUE * dependants.max(0))
        case UnknownApplicant(_) => None
      }
    }
  }

  def parameters: String = {
    s"""
       | ---------- External parameters values ----------
       |      t2t5.main.applicant.value = $MAIN_APPLICANT_VALUE
       | t2t5.dependant.applicant.value = $DEPENDANT_APPLICANT_VALUE
       | t5.youthmobility.applicant.value = $YOUTHMOBILITY_APPLICANT_VALUE
     """.stripMargin
  }

}
