package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class MaintenanceThresholdCalculator @Autowired()(@Value("${inner.london.accommodation.value}") val innerLondon: Int,
                                                  @Value("${non.inner.london.accommodation.value}") val nonInnerLondon: Int,
                                                  @Value("${maximum.accommodation.value}") val maxAccommodation: Int,
                                                  @Value("${minimum.doctorate.months.value}") val minDoctorateMonths: Int
                                                 ) {

  val INNER_LONDON_ACCOMMODATION = BigDecimal(innerLondon)
  val NON_INNER_LONDON_ACCOMMODATION = BigDecimal(nonInnerLondon)
  val MAXIMUM_ACCOMMODATION = BigDecimal(maxAccommodation)
  val MINIMUM_MONTHS_DOCTORATE = minDoctorateMonths

  def accommodation(innerLondon: Boolean) = if (innerLondon) INNER_LONDON_ACCOMMODATION else NON_INNER_LONDON_ACCOMMODATION

  def calculateNonDoctorate(innerLondon: Boolean, courseLengthInMonths: Int,
                            tuitionFees: BigDecimal, tuitionFeesPaid: BigDecimal,
                            accommodationFeesPaid: BigDecimal) = {

    ((accommodation(innerLondon) * courseLengthInMonths) + (tuitionFees - tuitionFeesPaid).max(0) - MAXIMUM_ACCOMMODATION.min(accommodationFeesPaid)).max(0)

  }

  def calculateDoctorate(innerLondon: Boolean, courseLengthInMonths: Int, accommodationFeesPaid: BigDecimal) = {

    ((accommodation(innerLondon) * courseLengthInMonths.min(MINIMUM_MONTHS_DOCTORATE)) - MAXIMUM_ACCOMMODATION.min(accommodationFeesPaid)).max(0)

  }

  def parameters = {
    s"""
       | ---------- External parameters values ----------
       |     inner.london.accommodation.value = $innerLondon
       | non.inner.london.accommodation.value = $nonInnerLondon
       |          maximum.accommodation.value = $maxAccommodation
       |       minimum.doctorate.months.value = $minDoctorateMonths
     """.stripMargin
  }
}
