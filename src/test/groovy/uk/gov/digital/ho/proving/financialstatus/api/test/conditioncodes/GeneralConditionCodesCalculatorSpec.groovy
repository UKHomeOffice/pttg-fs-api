package uk.gov.digital.ho.proving.financialstatus.api.test.conditioncodes

import cats.data.Validated
import scala.None$
import scala.Some
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.BelowDegreeCourse$
import uk.gov.digital.ho.proving.financialstatus.domain.MainCourse$
import uk.gov.digital.ho.proving.financialstatus.domain.PreSessionalCourse$
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.*

import java.time.LocalDate

class GeneralConditionCodesCalculatorSpec extends Specification {

    def 'Missing course start date should give invalid'() {
        given:

        def dependantsOnly = true
        def dependants = new Some(2)
        def recognisedBodyOrHEI = new Some(true)
        def startDate = None$.MODULE$
        def endDate = new Some(LocalDate.of(2016, 6, 01))

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        assert result.invalid
    }

    def 'Missing course end date should give invalid'() {
        given:

        def dependantsOnly = true
        def dependants = new Some(2)
        def recognisedBodyOrHEI = new Some(true)
        def startDate = new Some(LocalDate.of(2016, 6, 01))
        def endDate = None$.MODULE$

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        assert result.invalid
    }

    def 'Missing course start and end dates should give invalid'() {
        given:

        def dependantsOnly = true
        def dependants = new Some(2)
        def recognisedBodyOrHEI = new Some(true)
        def startDate = None$.MODULE$
        def endDate = None$.MODULE$

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        assert result.invalid
    }

    def 'Dependants only should return values only for partner and child'() {
        given:

        def dependantsOnly = true
        def dependants = new Some(2)
        def recognisedBodyOrHEI = new Some(true)
        def startDate = new Some(LocalDate.of(2016, 5, 01))
        def endDate = new Some(LocalDate.of(2016, 6, 01))

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        assert result.valid
        def conditionCodes = result.toOption().get()
        assert conditionCodes.applicant() == None$.MODULE$
        assert conditionCodes.partner() != None$.MODULE$
        assert conditionCodes.child() != None$.MODULE$
    }

    def 'Zero dependants should return value only for applicant'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(0)
        def recognisedBodyOrHEI = new Some(true)
        def startDate = new Some(LocalDate.of(2016, 5, 01))
        def endDate = new Some(LocalDate.of(2016, 6, 01))

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        assert result.valid
        def conditionCodes = result.toOption().get()

        assert conditionCodes.applicant() != None$.MODULE$
        assert conditionCodes.partner() == None$.MODULE$
        assert conditionCodes.child() == None$.MODULE$
    }

    def 'A main degree or higher from HEI general student applicant with no dependants regardless of course length'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(0)
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A main degree or higher from HEI general student applicant with unspecified dependants regardless of course length'() {
        given:

        def dependantsOnly = false
        def dependants = None$.MODULE$
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A main degree or higher from HEI general student applicant with dependants and lasting less than 12 months'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(5)
        def startDate = new Some(LocalDate.of(2016, 5, 01))
        def endDate = new Some(LocalDate.of(2016, 6, 01))
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("3")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A main degree or higher from non-HEI general student applicant with dependants and lasting less than 12 months'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(5)
        def startDate = new Some(LocalDate.of(2016, 5, 01))
        def endDate = new Some(LocalDate.of(2016, 6, 01))
        def recognisedBodyOrHEI = new Some(false)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("3")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("3")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A main degree or higher from non-HEI general student applicant with dependants and lasting more than 12 months'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(5)
        def startDate = new Some(LocalDate.of(2016, 5, 01))
        def endDate = new Some(LocalDate.of(2019, 6, 01))
        def recognisedBodyOrHEI = new Some(false)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("3")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A main degree or higher from non-HEI general student applicant with dependants and lasting exactly 12 months'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(5)
        def startDate = new Some(LocalDate.of(2016, 5, 02))
        def endDate = new Some(LocalDate.of(2017, 5, 01))
        def recognisedBodyOrHEI = new Some(false)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("3")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A main degree or higher from HEI general student applicant with dependants and lasting over 12 months '() {
        given:

        def dependantsOnly = false
        def dependants = new Some(5)
        def startDate = new Some(LocalDate.of(2015, 9, 01))
        def endDate = new Some(LocalDate.of(2017, 6, 01))
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A main degree or higher from HEI general student applicant with dependants and lasting exactly 12 months '() {
        given:

        def dependantsOnly = false
        def dependants = new Some(5)
        def startDate = new Some(LocalDate.of(2015, 9, 02))
        def endDate = new Some(LocalDate.of(2016, 9, 01))
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, startDate, endDate,
            MainCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A pre-sessional at HEI applicant with no dependants regardless of course length'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(0)
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            PreSessionalCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2A")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A pre-sessional at Higher Education institute applicant with unspecified dependants regardless of course length'() {
        given:

        def dependantsOnly = false
        def dependants = None$.MODULE$
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            PreSessionalCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2A")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A pre-sessional at HEI applicant with dependants regardless of course length'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(5)
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            PreSessionalCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2A")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("3")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }


    def 'A below degree at HEI applicant with no dependants regardless of course length'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(0)
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            BelowDegreeCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2A")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A below degree applicant at HEI with unspecified dependants regardless of course length'() {
        given:

        def dependantsOnly = false
        def dependants = None$.MODULE$
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            BelowDegreeCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2A")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A below degree applicant at HEI with dependants regardless of course length'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(4)
        def recognisedBodyOrHEI = new Some(true)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            BelowDegreeCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2A")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("3")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A pre-sessional applicant at other with no dependants regardless of course length'() {
        given:

        def dependantsOnly = false
        def dependants = new Some(0)
        def recognisedBodyOrHEI = new Some(false)

        def calculator = new GeneralConditionCodesCalculator()

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            BelowDegreeCourse$.MODULE$, recognisedBodyOrHEI)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("3")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }
}
