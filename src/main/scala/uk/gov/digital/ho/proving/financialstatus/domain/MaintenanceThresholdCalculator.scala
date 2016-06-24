package uk.gov.digital.ho.proving.financialstatus.domain

object MaintenanceThresholdCalculator {

  // TODO Reference data should be outside the code
  val INNER_LONDON_VALUE = BigDecimal(1265)
  val NON_INNER_LONDON_VALUE = BigDecimal(1015)
  val MAXIMUM_ACCOMMODATION_VALUE = BigDecimal(1265)

  def calculate(innerLondon: Boolean, courseLengthInMonths: Int,
                tuitionFees: BigDecimal, tuitionFeesPaid: BigDecimal,
                accommodationFeesPaid: BigDecimal) = {

    val accommodation = if (innerLondon) INNER_LONDON_VALUE else NON_INNER_LONDON_VALUE
    (accommodation * courseLengthInMonths) + (tuitionFees - tuitionFeesPaid).max(0) - MAXIMUM_ACCOMMODATION_VALUE.min(accommodationFeesPaid)

  }

}
