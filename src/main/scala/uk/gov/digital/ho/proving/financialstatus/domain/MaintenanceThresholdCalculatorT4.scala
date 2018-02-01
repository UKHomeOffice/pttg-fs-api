package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.{Clock, LocalDate, Period}

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.api.CappedValues

@Service
class MaintenanceThresholdCalculatorT4 @Autowired()(val clock: Clock,
                                                    @Value("${inner.london.accommodation.value}") val innerLondon: Int,
                                                    @Value("${non.inner.london.accommodation.value}") val nonInnerLondon: Int,
                                                    @Value("${maximum.accommodation.value}") val maxAccommodation: Int,
                                                    @Value("${inner.london.dependant.value}") val innerLondonDependants: Int,
                                                    @Value("${non.inner.london.dependant.value}") val nonInnerLondonDependants: Int,
                                                    @Value("${general.capped.course.length}") val generalCappedCourseLength: Int,
                                                    @Value("${pgdd.capped.course.length}") val pgddCappedCourseLength: Int,
                                                    @Value("${doctorate.fixed.course.length}") val doctorateFixedCourseLength: Int,
                                                    @Value("${suso.capped.course.length}") val susoCappedCourseLength: Int
                                                   ) {

  private val INNER_LONDON_ACCOMMODATION = BigDecimal(innerLondon).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  private val NON_INNER_LONDON_ACCOMMODATION = BigDecimal(nonInnerLondon).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  private val MAXIMUM_ACCOMMODATION = BigDecimal(maxAccommodation).setScale(2, BigDecimal.RoundingMode.HALF_UP)

  private val INNER_LONDON_DEPENDANTS = BigDecimal(innerLondonDependants).setScale(2, BigDecimal.RoundingMode.HALF_UP)
  private val NON_INNER_LONDON_DEPENDANTS = BigDecimal(nonInnerLondonDependants).setScale(2, BigDecimal.RoundingMode.HALF_UP)

  def accommodationValue(innerLondon: Boolean): BigDecimal = if (innerLondon) INNER_LONDON_ACCOMMODATION else NON_INNER_LONDON_ACCOMMODATION

  def dependantsValue(innerLondon: Boolean): BigDecimal = if (innerLondon) INNER_LONDON_DEPENDANTS else NON_INNER_LONDON_DEPENDANTS

  private def maintenancePeriod(start: LocalDate, end: LocalDate) = {
    val period = Period.between(start, end.plusDays(1))
    val months = period.getYears * 12 + (if (period.getDays > 0) period.getMonths + 1 else period.getMonths)
    months
  }

  def calculateGeneral(innerLondon: Boolean,
                       tuitionFees: BigDecimal, tuitionFeesPaid: BigDecimal,
                       accommodationFeesPaid: BigDecimal,
                       dependants: Int,
                       courseStartDate: LocalDate,
                       courseEndDate: LocalDate,
                       originalCourseStartDate: Option[LocalDate],
                      // isContinuation: Boolean,
                       isPreSessional: Boolean,
                       dependantsOnly: Boolean
                      ): (BigDecimal, Option[CappedValues], Option[LocalDate]) = {


    val courseLengthInMonths = maintenancePeriod(courseStartDate, courseEndDate)
    val leaveToRemain = LeaveToRemainCalculator.calculateLeaveToRemain(clock, courseStartDate, courseEndDate, originalCourseStartDate, isPreSessional)
    val leaveToRemainInMonths = maintenancePeriod(courseStartDate, leaveToRemain)

    val (courseLength, courseLengthCapped) = if (courseLengthInMonths > generalCappedCourseLength) {
      (generalCappedCourseLength, Some(generalCappedCourseLength))
    } else {
      (courseLengthInMonths, None)
    }
    val (accommodationFees, accommodationFeesCapped) = if (accommodationFeesPaid > MAXIMUM_ACCOMMODATION) {
      (MAXIMUM_ACCOMMODATION, Some(MAXIMUM_ACCOMMODATION))
    } else {
      (accommodationFeesPaid, None)
    }

    val dependantsAmount = dependantsValue(innerLondon) * leaveToRemainInMonths.min(generalCappedCourseLength) * dependants

    val amount = if (dependantsOnly) {
      dependantsAmount
    } else {
      ((accommodationValue(innerLondon) * courseLength)
        + (tuitionFees - tuitionFeesPaid).max(0)
        + dependantsAmount
        - accommodationFees).max(0)
    }
    if (courseLengthCapped.isDefined || accommodationFeesCapped.isDefined) {
      (amount, Some(CappedValues(accommodationFeesCapped, courseLengthCapped)), Some(leaveToRemain))
    } else {
      (amount, None, Some(leaveToRemain))
    }
  }


  def calculateStudentUnionSabbaticalOfficer(innerLondon: Boolean,
                                             accommodationFeesPaid: BigDecimal,
                                             dependants: Int,
                                             courseStartDate: LocalDate,
                                             courseEndDate: LocalDate,
                                             originalCourseStartDate: Option[LocalDate],
                                             dependantsOnly: Boolean
                                            ): (BigDecimal, Option[CappedValues], Option[LocalDate]) = {


    val courseLengthInMonths = maintenancePeriod(courseStartDate, courseEndDate)
    val leaveToRemain = LeaveToRemainCalculator.calculateLeaveToRemain(clock, courseStartDate, courseEndDate, originalCourseStartDate, preSessional = false)
    val leaveToRemainInMonths = maintenancePeriod(courseStartDate, leaveToRemain)

    val (courseLength, courseLengthCapped) = if (courseLengthInMonths > susoCappedCourseLength) {
      (susoCappedCourseLength, Some(susoCappedCourseLength))
    } else {
      (courseLengthInMonths, None)
    }
    val (accommodationFees, accommodationFeesCapped) = if (accommodationFeesPaid > MAXIMUM_ACCOMMODATION) {
      (MAXIMUM_ACCOMMODATION, Some(MAXIMUM_ACCOMMODATION))
    } else {
      (accommodationFeesPaid, None)
    }

    val dependantsAmount = dependantsValue(innerLondon) * leaveToRemainInMonths.min(susoCappedCourseLength) * dependants

    val amount = if (dependantsOnly) {
      dependantsAmount
    } else {
      ((accommodationValue(innerLondon) * courseLength)
        + dependantsAmount
        - accommodationFees).max(0)
    }
    if (courseLengthCapped.isDefined || accommodationFeesCapped.isDefined) {
      (amount, Some(CappedValues(accommodationFeesCapped, courseLengthCapped)), Some(leaveToRemain))
    } else {
      (amount, None, Some(leaveToRemain))
    }
  }


  def calculatePostGraduateDoctorDentist(innerLondon: Boolean,
                                         accommodationFeesPaid: BigDecimal,
                                         dependants: Int,
                                         courseStartDate: LocalDate,
                                         courseEndDate: LocalDate,
                                         originalCourseStartDate: Option[LocalDate],
//                                         isContinuation: Boolean,
                                         dependantsOnly: Boolean
                                        ): (BigDecimal, Option[CappedValues], Option[LocalDate]) = {


    val courseLengthInMonths = maintenancePeriod(courseStartDate, courseEndDate)
    val leaveToRemain = LeaveToRemainCalculator.calculateFixedLeaveToRemain(courseEndDate, Period.ofMonths(1))
    val leaveToRemainInMonths = maintenancePeriod(courseStartDate, leaveToRemain)

    val (courseLength, courseLengthCapped) = if (courseLengthInMonths > susoCappedCourseLength) {
      (susoCappedCourseLength, Some(susoCappedCourseLength))
    } else {
      (courseLengthInMonths, None)
    }
    val (accommodationFees, accommodationFeesCapped) = if (accommodationFeesPaid > MAXIMUM_ACCOMMODATION) {
      (MAXIMUM_ACCOMMODATION, Some(MAXIMUM_ACCOMMODATION))
    } else {
      (accommodationFeesPaid, None)
    }

    val dependantsAmount = dependantsValue(innerLondon) * leaveToRemainInMonths.min(susoCappedCourseLength) * dependants

    val amount = if (dependantsOnly) {
      dependantsAmount
    } else {
      ((accommodationValue(innerLondon) * courseLength)
        + dependantsAmount
        - accommodationFees).max(0)
    }
    if (courseLengthCapped.isDefined || accommodationFeesCapped.isDefined) {
      (amount, Some(CappedValues(accommodationFeesCapped, courseLengthCapped)), Some(leaveToRemain))
    } else {
      (amount, None, Some(leaveToRemain))
    }
  }

  def calculateDoctorateExtensionScheme(innerLondon: Boolean, accommodationFeesPaid: BigDecimal,
                                        dependants: Int, dependantsOnly: Boolean): (BigDecimal, Option[CappedValues], Option[LocalDate]) = {

    val (accommodationFees, accommodationFeesCapped) = if (accommodationFeesPaid > MAXIMUM_ACCOMMODATION) {
      (MAXIMUM_ACCOMMODATION, Some(MAXIMUM_ACCOMMODATION))
    } else {
      (accommodationFeesPaid, None)
    }

    val dependantsAmount = dependantsValue(innerLondon) * doctorateFixedCourseLength * dependants

    val amount = if (dependantsOnly) {
      dependantsAmount
    } else {
      ((accommodationValue(innerLondon) * doctorateFixedCourseLength)
        + dependantsAmount
        - accommodationFees).max(0)
    }

    if (accommodationFeesCapped.isDefined) {
      (amount, Some(CappedValues(accommodationFeesCapped)), None)
    } else {
      (amount, None, None)
    }

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
