package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class MaintenanceThresholdCalculator @Autowired()(@Value("${inner.london.accommodation.value}") val innerLondon: Int,
                                                  @Value("${non.inner.london.accommodation.value}") val nonInnerLondon: Int,
                                                  @Value("${maximum.accommodation.value}") val maxAccommodation: Int,
                                                  @Value("${minimum.doctorate.months.value}") val minDoctorateMonths: Int,
                                                  @Value("${inner.london.dependant.value}") val innerLondonDependants: Int,
                                                  @Value("${non.inner.london.dependant.value}") val nonInnerLondonDependants: Int
                                                 ) {

  val INNER_LONDON_ACCOMMODATION = BigDecimal(innerLondon)
  val NON_INNER_LONDON_ACCOMMODATION = BigDecimal(nonInnerLondon)
  val MAXIMUM_ACCOMMODATION = BigDecimal(maxAccommodation)
  val MINIMUM_MONTHS_DOCTORATE = minDoctorateMonths

  val INNER_LONDON_DEPENDANTS = BigDecimal(innerLondonDependants)
  val NON_INNER_LONDON_DEPENDANTS = BigDecimal(nonInnerLondonDependants)



  def accommodationValue(innerLondon: Boolean): BigDecimal = if (innerLondon) INNER_LONDON_ACCOMMODATION else NON_INNER_LONDON_ACCOMMODATION
  def dependantsValue(innerLondon: Boolean): BigDecimal = if (innerLondon) INNER_LONDON_DEPENDANTS else NON_INNER_LONDON_DEPENDANTS


  def calculateNonDoctorate(innerLondon: Boolean, courseLengthInMonths: Int,
                            tuitionFees: BigDecimal, tuitionFeesPaid: BigDecimal,
                            accommodationFeesPaid: BigDecimal,
                            dependants: Int
                           ): BigDecimal = {

    val amount = ((accommodationValue(innerLondon) * courseLengthInMonths)
      + (tuitionFees - tuitionFeesPaid).max(0)
      + (dependantsValue(innerLondon) * courseLengthInMonths * dependants)
      - MAXIMUM_ACCOMMODATION.min(accommodationFeesPaid)).max(0)

    amount
  }

  def calculateDoctorate(innerLondon: Boolean, courseLengthInMonths: Int, accommodationFeesPaid: BigDecimal, dependants: Int): BigDecimal = {

    val amount = ((accommodationValue(innerLondon) * courseLengthInMonths.min(MINIMUM_MONTHS_DOCTORATE))
      + (dependantsValue(innerLondon) * courseLengthInMonths * dependants)
      - MAXIMUM_ACCOMMODATION.min(accommodationFeesPaid)).max(0)
    amount
  }

  def parameters: String = {
    s"""
       | ---------- External parameters values ----------
       |     inner.london.accommodation.value = $innerLondon
       | non.inner.london.accommodation.value = $nonInnerLondon
       |          maximum.accommodation.value = $maxAccommodation
       |       minimum.doctorate.months.value = $minDoctorateMonths
     """.stripMargin
  }
}
