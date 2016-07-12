package uk.gov.digital.ho.proving.financialstatus.domain

object MaintenanceThresholdCalculator {

  // TODO Reference data should be outside the code
  val INNER_LONDON_VALUE = BigDecimal(1265)
  val NON_INNER_LONDON_VALUE = BigDecimal(1015)
  val MAXIMUM_ACCOMMODATION_VALUE = BigDecimal(1265)
  val MINIMUM_MONTHS_DOCTORATE = 2

  def accommodation(innerLondon: Boolean) = if (innerLondon) INNER_LONDON_VALUE else NON_INNER_LONDON_VALUE

  def calculateNonDoctorate(innerLondon: Boolean, courseLengthInMonths: Int,
                            tuitionFees: BigDecimal, tuitionFeesPaid: BigDecimal,
                            accommodationFeesPaid: BigDecimal) = {

    ((accommodation(innerLondon) * courseLengthInMonths) + (tuitionFees - tuitionFeesPaid).max(0) - MAXIMUM_ACCOMMODATION_VALUE.min(accommodationFeesPaid)).max(0)

  }

  def calculateDoctorate(innerLondon: Boolean, courseLengthInMonths: Int, accommodationFeesPaid: BigDecimal) = {

    ((accommodation(innerLondon) * courseLengthInMonths.min(MINIMUM_MONTHS_DOCTORATE))  - MAXIMUM_ACCOMMODATION_VALUE.min(accommodationFeesPaid)).max(0)

  }
}
