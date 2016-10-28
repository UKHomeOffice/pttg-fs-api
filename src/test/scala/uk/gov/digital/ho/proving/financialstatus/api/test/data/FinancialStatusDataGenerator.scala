package uk.gov.digital.ho.proving.financialstatus.api.test.data

import java.time.{LocalDate, Period}

import scala.util.Random

case class Input(
                  courseStartDate: LocalDate,
                  courseEndDate: LocalDate,
                  continuationEndDate: Option[LocalDate],
                  inLondon: Boolean,
                  tuitionFees: BigDecimal,
                  tuitionFeesPaid: BigDecimal,
                  accommodationFeesPaid: BigDecimal,
                  dependants: Int
                )

object FinancialStatusDataGenerator {

  private implicit def bigDecimal(v: Float): BigDecimal = BigDecimal(v).setScale(2, BigDecimal.RoundingMode.HALF_UP)

  private implicit def bigDecimal(v: Int): BigDecimal = BigDecimal(v).setScale(2, BigDecimal.RoundingMode.HALF_UP)

  val randomInputs: Stream[Input] = generateRandomInput #:: randomInputs.map { _ => generateRandomInput }

  private def generateRandomInput = {

    val inLondon = Random.nextBoolean
    val courseStartDate = RandomTestData.date
    val courseEndDate = RandomTestData.dateAfter(courseStartDate)
    val (continuationEndDate, dependants) = if (Random.nextBoolean) (Some(RandomTestData.dateAfter(courseEndDate)), Random.nextInt(15))
                                            else (None, if (differenceInMonths(courseStartDate, courseEndDate) > 6) Random.nextInt(15) else 0)

    val accommodationFeesPaid = bigDecimal(Random.nextFloat * 2000)
    val tuitionFees = bigDecimal(Random.nextFloat * 10000)
    val tuitionFeesPaid = bigDecimal(Random.nextFloat * 10000)

    Input(courseStartDate, courseEndDate, continuationEndDate, inLondon, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)

  }

  private def formatLocalDate(date: Option[LocalDate]): String = {
    if (date.isDefined)
      formatLocalDate(date.get)
    else "null"
  }

  private def formatLocalDate(date: LocalDate): String = {
    s"""LocalDate.of(${date.getYear},${date.getMonthValue},${date.getDayOfMonth})"""
  }

  private def generateNonDoctorateData(count: Int, inputs: Stream[Input]) = {

    val output =
      s"""inLondon | courseStartDate          | courseEndDate            | continuationEndDate | tuitionFees | tuitionFeesPaid | dependants | accommodationFeesPaid\n""" +
        inputs.take(count).map { input =>

          val (threshold, feesCapped, courseCapped, continuationCapped) = calculateNonDoctorate(input)
          val line = s"""${formatLocalDate(input.courseStartDate)}| ${formatLocalDate(input.courseEndDate)}| ${formatLocalDate(input.continuationEndDate)}| ${input.inLondon}| ${input.tuitionFees}| ${input.tuitionFeesPaid}|  ${input.accommodationFeesPaid}|${input.dependants}|| $threshold|| $feesCapped|| $courseCapped|| $continuationCapped"""
          line
        }.mkString("\n")
    output
  }

  private def differenceInMonths(start: LocalDate, end: LocalDate) = {
    val period = Period.between(start, end.plusDays(1))
    val months = 12 * period.getYears + period.getMonths + (if (period.getDays > 0) 1 else 0)
    months
  }

  private def calculateNonDoctorate(input: Input) = {

    val (applicantMaintenanceValue, dependantMaintenanceValue) = if (input.inLondon) (1265, 845) else (1015, 680)
    val courseCap = 9
    val accommodationCap = 1265

    val (courseLength, totalCourseLength, isContinuation) = if (input.continuationEndDate.isDefined) {
      (differenceInMonths(input.courseEndDate.plusDays(1), input.continuationEndDate.get), differenceInMonths(input.courseStartDate, input.continuationEndDate.get), true)
    } else {
      (differenceInMonths(input.courseStartDate, input.courseEndDate), differenceInMonths(input.courseStartDate, input.courseEndDate), false)
    }

    val wrapUpPeriod = if (totalCourseLength >= 12) 4 else 2

    val applicantMaintenance = courseLength.min(courseCap) * applicantMaintenanceValue

    val dependantMaintenance = (courseLength + wrapUpPeriod).min(courseCap) * dependantMaintenanceValue * input.dependants

    val tuitionFeesOutstanding = (input.tuitionFees - input.tuitionFeesPaid).max(0)

    val cappedAccommodationFees = input.accommodationFeesPaid.min(accommodationCap)

    val threshold = (applicantMaintenance + dependantMaintenance + tuitionFeesOutstanding - cappedAccommodationFees).max(0)


    println(input)
    println(s"   applicantMaintenance: $applicantMaintenance")
    println(s"   dependantMaintenance: $dependantMaintenance")
    println(s" tuitionFeesOutstanding: $tuitionFeesOutstanding")
    println(s"cappedAccommodationFees: $cappedAccommodationFees")
    println(s"           wrapUpPeriod: $wrapUpPeriod")
    println(s"           courseLength: ${courseLength.min(courseCap)}")
    println(s"----------------------------------------------")
    println(s"              threshold: $threshold")

    (threshold, if (input.accommodationFeesPaid > accommodationCap) accommodationCap else 0, if (courseLength > courseCap && !isContinuation) courseCap else 0, if (courseLength > courseCap && isContinuation) courseCap else 0)

  }

  def main(args: Array[String]) {

    println(generateNonDoctorateData(50, randomInputs))

  }
}
