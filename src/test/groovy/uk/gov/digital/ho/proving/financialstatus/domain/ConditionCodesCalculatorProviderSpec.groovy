package uk.gov.digital.ho.proving.financialstatus.domain

import spock.lang.Specification

class ConditionCodesCalculatorProviderSpec extends Specification {

    def 'provides GeneralConditionCodesCalculator for a General student'() {
        given:

        def underTest = new ConditionCodesCalculatorProvider()

        when:
        def calculator = underTest.provide(GeneralStudent$.MODULE$)

        then:
        assert calculator instanceof GeneralConditionCodesCalculator

    }

    def 'provides OtherNonGeneralConditionCodesCalculator for a Doctorate Extension student'() {
        given:

        def underTest = new ConditionCodesCalculatorProvider()

        when:
        def calculator = underTest.provide(DoctorateExtensionStudent$.MODULE$)

        then:
        assert calculator instanceof OtherNonGeneralConditionCodesCalculator
    }

    def 'provides OtherNonGeneralConditionCodesCalculator for a Post Graduate Doctor Dentist student'() {
        given:

        def underTest = new ConditionCodesCalculatorProvider()

        when:
        def calculator = underTest.provide(PostGraduateDoctorDentistStudent$.MODULE$)

        then:
        assert calculator instanceof OtherNonGeneralConditionCodesCalculator
    }

    def 'provides OtherNonGeneralConditionCodesCalculator for a Student Union Sabbatical Officer student'() {
        given:

        def underTest = new ConditionCodesCalculatorProvider()

        when:
        def calculator = underTest.provide(StudentUnionSabbaticalOfficerStudent$.MODULE$)

        then:
        assert calculator instanceof OtherNonGeneralConditionCodesCalculator
    }
}
