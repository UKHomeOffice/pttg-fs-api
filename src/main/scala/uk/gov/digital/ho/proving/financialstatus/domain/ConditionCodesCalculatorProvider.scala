package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.stereotype.Service

/**
  * Encapsulates logic for calculating condition codes
  */
@Service
class ConditionCodesCalculatorProvider() {

  def provide(studentType: StudentType = UnknownStudent("")): ConditionCodesCalculator = studentType match {
    case GeneralStudent => new GeneralConditionCodesCalculator
    case DoctorateExtensionStudent | PostGraduateDoctorDentistStudent | StudentUnionSabbaticalOfficerStudent =>
      new OtherNonGeneralConditionCodesCalculator
      // FIXME: Handle the catch all case
//    case _ =>
  }

}

trait ConditionCodesCalculator {
  def calculateConditionCodes(): ConditionCodesCalculationResult
}
// TODO: Implement
class GeneralConditionCodesCalculator extends ConditionCodesCalculator {
  override def calculateConditionCodes(): ConditionCodesCalculationResult = ???
}
class OtherNonGeneralConditionCodesCalculator extends ConditionCodesCalculator {
  override def calculateConditionCodes(): ConditionCodesCalculationResult = ???
}
