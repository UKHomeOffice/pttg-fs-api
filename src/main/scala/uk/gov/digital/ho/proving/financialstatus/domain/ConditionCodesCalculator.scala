package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.stereotype.Service

/**
  * Encapsulates logic for calculating condition codes
  */
trait ConditionCodesCalculator {

  def calculateConditionCodes(): ConditionCodesCalculationResult

}

@Service
class ConditionCodesCalculatorImpl extends ConditionCodesCalculator {
  // TODO: Implement
  override def calculateConditionCodes(): ConditionCodesCalculationResult = ???
}
