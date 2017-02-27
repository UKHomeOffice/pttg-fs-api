package uk.gov.digital.ho.proving.financialstatus.api.test.conditioncodes

import cats.data.Validated
import scala.None$
import scala.Some
import spock.lang.Specification
import uk.gov.digital.ho.proving.financialstatus.domain.*
import uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes.*

class OtherNonGeneralConditionCodesCalculatorSpec extends Specification {

    def 'A Doctorate Extension Scheme applicant with no dependants'() {
        given:

        StudentType studentType = DoctorateExtensionStudent$.MODULE$
        def dependantsOnly = false
        def dependants = new Some(0)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("4E")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A Doctorate Extension Scheme applicant with unspecified dependants'() {
        given:

        StudentType studentType = DoctorateExtensionStudent$.MODULE$
        def dependantsOnly = false
        def dependants = None$.MODULE$

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes =new Validated.Valid( new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("4E")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A Doctorate Extension Scheme applicant with some dependants'() {
        given:

        StudentType studentType = DoctorateExtensionStudent$.MODULE$
        def dependantsOnly = false
        def dependants = new Some(2)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("4E")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A Doctorate Extension Scheme dependants only applicant'() {
        given:

        StudentType studentType = DoctorateExtensionStudent$.MODULE$
        def dependantsOnly = true
        def dependants = new Some(2)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            None$.MODULE$,
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A PostGraduate Doctor or Dentist applicant with no dependants'() {
        given:

        StudentType studentType = PostGraduateDoctorDentistStudent$.MODULE$
        def dependantsOnly = false
        def dependants = new Some(0)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A PostGraduate Doctor or Dentist applicant with unspecified dependants'() {
        given:

        StudentType studentType = PostGraduateDoctorDentistStudent$.MODULE$
        def dependantsOnly = false
        def dependants = None$.MODULE$

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A PostGraduate Doctor or Dentist applicant with some dependants'() {
        given:

        StudentType studentType = PostGraduateDoctorDentistStudent$.MODULE$
        def dependantsOnly = false
        def dependants = new Some(3)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A PostGraduate Doctor or Dentist dependant only applicant'() {
        given:

        StudentType studentType = PostGraduateDoctorDentistStudent$.MODULE$
        def dependantsOnly = true
        def dependants = new Some(3)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            None$.MODULE$,
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A Student Union Sabbatical Officer applicant with no dependants'() {
        given:

        StudentType studentType = StudentUnionSabbaticalOfficerStudent$.MODULE$
        def dependantsOnly = false
        def dependants = new Some(0)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A Student Union Sabbatical Officer applicant with unspecified dependants'() {
        given:

        StudentType studentType = StudentUnionSabbaticalOfficerStudent$.MODULE$
        def dependantsOnly = false
        def dependants = None$.MODULE$

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            None$.MODULE$,
            None$.MODULE$
        ))

        assert expectedConditionCodes == result
    }

    def 'A Student Union Sabbatical Officer applicant with some dependants'() {
        given:

        StudentType studentType = StudentUnionSabbaticalOfficerStudent$.MODULE$
        def dependantsOnly = false
        def dependants = new Some(2)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            new Some<ApplicantConditionCode>(new ApplicantConditionCode("2")),
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A Student Union Sabbatical Officer dependant only applicant'() {
        given:

        StudentType studentType = StudentUnionSabbaticalOfficerStudent$.MODULE$
        def dependantsOnly = true
        def dependants = new Some(1)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants, None$.MODULE$, None$.MODULE$,
            new UnknownCourse(""), None$.MODULE$)

        then:

        def expectedConditionCodes = new Validated.Valid(new ConditionCodesCalculationResult(
            None$.MODULE$,
            new Some<PartnerConditionCode>(new PartnerConditionCode("4B")),
            new Some<ChildConditionCode>(new ChildConditionCode("1"))
        ))

        assert expectedConditionCodes == result
    }

    def 'A general applicant using the non general calculator'() {
        given:

        StudentType studentType = GeneralStudent$.MODULE$
        def dependantsOnly = false
        def dependants = new Some(1)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants,
            None$.MODULE$, None$.MODULE$, new UnknownCourse(""), None$.MODULE$)

        then:

        assert result.invalid
    }


    def 'An unknown applicant type using the non general calculator'() {
        given:

        StudentType studentType = GeneralStudent$.MODULE$
        def dependantsOnly = false
        def dependants = new Some(1)

        def calculator = new OtherNonGeneralConditionCodesCalculator(studentType)

        when:

        def result = calculator.calculateConditionCodes(dependantsOnly, dependants,
            None$.MODULE$, None$.MODULE$, new UnknownCourse(""), None$.MODULE$)

        then:

        assert result.invalid
    }

}
