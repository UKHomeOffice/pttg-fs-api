package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class MaintenanceThresholdCalculator @Autowired()(@Value("${inner.london.accommodation.value}") val innerLondon: Int,
                                                  @Value("${non.inner.london.accommodation.value}") val nonInnerLondon: Int,
                                                  @Value("${maximum.accommodation.value}") val maxAccommodation: Int,
                                                  @Value("${inner.london.dependant.value}") val innerLondonDependants: Int,
                                                  @Value("${non.inner.london.dependant.value}") val nonInnerLondonDependants: Int,
                                                  @Value("${non.doctorate.minimum.course.length}") val nonDoctorateMinCourseLength: Int,
                                                  @Value("${non.doctorate.maximum.course.length}") val nonDoctorateMaxCourseLength: Int,
                                                  @Value("${doctorate.minimum.course.length}") val doctorateMinCourseLength: Int,
                                                  @Value("${doctorate.maximum.course.length}") val doctorateMaxCourseLength: Int
                                                 ) {

  val INNER_LONDON_ACCOMMODATION = BigDecimal(innerLondon)
  val NON_INNER_LONDON_ACCOMMODATION = BigDecimal(nonInnerLondon)
  val MAXIMUM_ACCOMMODATION = BigDecimal(maxAccommodation)

  val INNER_LONDON_DEPENDANTS = BigDecimal(innerLondonDependants)
  val NON_INNER_LONDON_DEPENDANTS = BigDecimal(nonInnerLondonDependants)

  def accommodationValue(innerLondon: Boolean): BigDecimal = if (innerLondon) INNER_LONDON_ACCOMMODATION else NON_INNER_LONDON_ACCOMMODATION

  def dependantsValue(innerLondon: Boolean): BigDecimal = if (innerLondon) INNER_LONDON_DEPENDANTS else NON_INNER_LONDON_DEPENDANTS


  def calculateNonDoctorate(innerLondon: Boolean, courseLengthInMonths: Int,
                            tuitionFees: BigDecimal, tuitionFeesPaid: BigDecimal,
                            accommodationFeesPaid: BigDecimal,
                            dependants: Int
                           ): BigDecimal = {

    val amount = ((accommodationValue(innerLondon) * courseLengthInMonths.min(nonDoctorateMaxCourseLength))
      + (tuitionFees - tuitionFeesPaid).max(0)
      + (dependantsValue(innerLondon) * courseLengthInMonths.min(nonDoctorateMaxCourseLength) * dependants)
      - MAXIMUM_ACCOMMODATION.min(accommodationFeesPaid)).max(0)

    amount
  }

  def calculateDoctorate(innerLondon: Boolean, courseLengthInMonths: Int, accommodationFeesPaid: BigDecimal, dependants: Int): BigDecimal = {

    val amount = ((accommodationValue(innerLondon) * courseLengthInMonths.min(doctorateMaxCourseLength))
      + (dependantsValue(innerLondon) * courseLengthInMonths.min(doctorateMaxCourseLength) * dependants)
      - MAXIMUM_ACCOMMODATION.min(accommodationFeesPaid)).max(0)
    amount
  }

  def parameters: String = {
    s"""
       | ---------- External parameters values ----------
       |     inner.london.accommodation.value = $innerLondon
       | non.inner.london.accommodation.value = $nonInnerLondon
       |          maximum.accommodation.value = $maxAccommodation
     """.stripMargin
  }
}
