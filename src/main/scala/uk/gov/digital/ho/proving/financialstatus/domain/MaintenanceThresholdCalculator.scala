package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.{LocalDate, Period}

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

  def maintenancePeriod(start: LocalDate, end: LocalDate) = {
    val period = Period.between(start, end.plusDays(1))
    val months = period.getYears * 12 + (if (period.getDays > 0) period.getMonths + 1 else period.getMonths)
    months
  }

  def calculateNonDoctorate(innerLondon: Boolean,
                            tuitionFees: BigDecimal, tuitionFeesPaid: BigDecimal,
                            accommodationFeesPaid: BigDecimal,
                            dependants: Int,
                            courseStartDate: LocalDate,
                            courseEndDate: LocalDate,
                            originalCourseStartDate: Option[LocalDate],
                            isContinuation: Boolean,
                            isPreSessional: Boolean
                           ): (BigDecimal, Option[CappedValues], Option[LocalDate]) = {


    val courseLengthInMonths = maintenancePeriod(courseStartDate, courseEndDate)
    val leaveToRemain = LeaveToRemainCalculator.calculateLeaveToRemain(courseStartDate, courseEndDate, originalCourseStartDate, isPreSessional)
    val leaveToRemainInMonths = maintenancePeriod(courseStartDate, leaveToRemain)

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
      + (dependantsValue(innerLondon) * (leaveToRemainInMonths).min(nonDoctorateMaxCourseLength) * dependants)
      - accommodationFees).max(0)

    if (courseLengthCapped.isDefined || accommodationFeesCapped.isDefined) {
      (amount, Some(CappedValues(accommodationFeesCapped, courseLengthCapped)), Some(leaveToRemain))
    } else {
      (amount, None, Some(leaveToRemain))
    }
  }

  def calculateDesPgddSso(innerLondon: Boolean, courseLengthInMonths: Int, accommodationFeesPaid: BigDecimal,
                          dependants: Int): (BigDecimal, Option[CappedValues], Option[LocalDate]) = {

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
      (amount, Some(CappedValues(accommodationFeesCapped, courseLengthCapped)), None)
    } else {
      (amount, None, None)
    }

  }

  def calculateDoctorate(innerLondon: Boolean, accommodationFeesPaid: BigDecimal,
                         dependants: Int): (BigDecimal, Option[CappedValues], Option[LocalDate]) = {

    calculateDesPgddSso(innerLondon: Boolean, doctorateFixedCourseLength, accommodationFeesPaid: BigDecimal,
      dependants: Int): (BigDecimal, Option[CappedValues], Option[LocalDate])

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
