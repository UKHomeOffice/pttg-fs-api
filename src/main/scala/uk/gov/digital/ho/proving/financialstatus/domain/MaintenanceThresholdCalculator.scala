package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.api.CappedValues

@Service
class MaintenanceThresholdCalculator @Autowired()(@Value("${inner.london.accommodation.value}") val innerLondon: Int,
                                                  @Value("${non.inner.london.accommodation.value}") val nonInnerLondon: Int,
                                                  @Value("${maximum.accommodation.value}") val maxAccommodation: Int,
                                                  @Value("${inner.london.dependant.value}") val innerLondonDependants: Int,
                                                  @Value("${non.inner.london.dependant.value}") val nonInnerLondonDependants: Int,
                                                  @Value("${non.doctorate.minimum.course.length}") val nonDoctorateMinCourseLength: Int,
                                                  @Value("${non.doctorate.maximum.course.length}") val nonDoctorateMaxCourseLength: Int,
                                                  @Value("${non.doctorate.minimum.course.length.with.dependants}") val minNonDoctorateCourseLengthWithDependants: Int,
                                                  @Value("${pgdd.sso.minimum.course.length}") val pgddSsoMinCourseLength: Int,
                                                  @Value("${pgdd.sso.maximum.course.length}") val pgddSsoMaxCourseLength: Int,
                                                  @Value("${doctorate.fixed.course.length}") val doctorateFixedCourseLength: Int
                                                 ) {

  val INNER_LONDON_ACCOMMODATION = BigDecimal(innerLondon).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  val NON_INNER_LONDON_ACCOMMODATION = BigDecimal(nonInnerLondon).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  val MAXIMUM_ACCOMMODATION = BigDecimal(maxAccommodation).setScale(2, BigDecimal.RoundingMode.HALF_UP)

  val INNER_LONDON_DEPENDANTS = BigDecimal(innerLondonDependants).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  val NON_INNER_LONDON_DEPENDANTS = BigDecimal(nonInnerLondonDependants).setScale(2, BigDecimal.RoundingMode.HALF_UP)

  def accommodationValue(innerLondon: Boolean): BigDecimal = if (innerLondon) INNER_LONDON_ACCOMMODATION else NON_INNER_LONDON_ACCOMMODATION

  def dependantsValue(innerLondon: Boolean): BigDecimal = if (innerLondon) INNER_LONDON_DEPENDANTS else NON_INNER_LONDON_DEPENDANTS


  def calculateNonDoctorate(innerLondon: Boolean, courseLengthInMonths: Int,
                            tuitionFees: BigDecimal, tuitionFeesPaid: BigDecimal,
                            accommodationFeesPaid: BigDecimal,
                            dependants: Int, leaveToRemain: Int, isExtension: Boolean
                           ): (BigDecimal, Option[CappedValues]) = {

    val (courseLength, courseLengthCapped) = if (courseLengthInMonths > nonDoctorateMaxCourseLength) {
      (nonDoctorateMaxCourseLength, Some(nonDoctorateMaxCourseLength))
    } else {
      (courseLengthInMonths, None)
    }
    val (accommodationFees, accommodationFeesCapped) = if (accommodationFeesPaid > MAXIMUM_ACCOMMODATION) {
      (MAXIMUM_ACCOMMODATION, Some(MAXIMUM_ACCOMMODATION))
    } else {
      (accommodationFeesPaid, None)
    }

    val amount = ((accommodationValue(innerLondon) * courseLength)
      + (tuitionFees - tuitionFeesPaid).max(0)
      + (dependantsValue(innerLondon) * (leaveToRemain).min(nonDoctorateMaxCourseLength) * dependants)
      - accommodationFees).max(0)

    if (courseLengthCapped.isDefined || accommodationFeesCapped.isDefined) {
      (amount, if (isExtension) Some(CappedValues(accommodationFeesCapped, None, courseLengthCapped)) else Some(CappedValues(accommodationFeesCapped, courseLengthCapped, None)))
    } else {
      (amount, None)
    }
  }

  def calculateDesPgddSso(innerLondon: Boolean, courseLengthInMonths: Int, accommodationFeesPaid: BigDecimal,
                          dependants: Int): (BigDecimal, Option[CappedValues]) = {

    val (courseLength, courseLengthCapped) = if (courseLengthInMonths > pgddSsoMaxCourseLength) {
      (pgddSsoMaxCourseLength, Some(pgddSsoMaxCourseLength))
    } else {
      (courseLengthInMonths, None)
    }


    val (accommodationFees, accommodationFeesCapped) = if (accommodationFeesPaid > MAXIMUM_ACCOMMODATION) {
      (MAXIMUM_ACCOMMODATION, Some(MAXIMUM_ACCOMMODATION))
    } else {
      (accommodationFeesPaid, None)
    }

    val amount = ((accommodationValue(innerLondon) * courseLength)
      + (dependantsValue(innerLondon) * courseLength * dependants)
      - accommodationFees).max(0)

    if (courseLengthCapped.isDefined || accommodationFeesCapped.isDefined) {
      (amount, Some(CappedValues(accommodationFeesCapped, courseLengthCapped)))
    } else {
      (amount, None)
    }

  }

  def calculateDoctorate(innerLondon: Boolean, accommodationFeesPaid: BigDecimal,
                         dependants: Int): (BigDecimal, Option[CappedValues]) = {

    calculateDesPgddSso(innerLondon: Boolean, doctorateFixedCourseLength, accommodationFeesPaid: BigDecimal,
      dependants: Int): (BigDecimal, Option[CappedValues])

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
