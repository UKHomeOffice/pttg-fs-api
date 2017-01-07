package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class StudentTypeChecker @Autowired()(@Value("${student.type.doctorate}") val doctorate: String,
                                      @Value("${student.type.non.doctorate}") val nonDoctorate: String,
                                      @Value("${student.type.post.grad.doctor.dentist}") val doctorDentist: String,
                                      @Value("${student.type.student.sabbatical.officer}") val studentSabbaticalOfficer: String
                                     ) {

  private val DOCTORATE = doctorate
  private val NON_DOCTORATE = nonDoctorate
  private val DOCTOR_DENTIST = doctorDentist
  private val STUDENT_SABBATICAL_OFFICER = studentSabbaticalOfficer

  val values = Vector(DOCTORATE, NON_DOCTORATE, DOCTOR_DENTIST, STUDENT_SABBATICAL_OFFICER)

  def getStudentType(studentType: String): StudentType = {
    studentType match {
      case DOCTORATE => DoctorateStudent
      case NON_DOCTORATE => NonDoctorateStudent
      case DOCTOR_DENTIST => DoctorDentistStudent
      case STUDENT_SABBATICAL_OFFICER => StudentSabbaticalOfficer
      case _ => UnknownStudent(studentType)
    }
  }
}

sealed trait StudentType
case class UnknownStudent(value: String) extends StudentType
case object DoctorateStudent extends StudentType
case object NonDoctorateStudent extends StudentType
case object DoctorDentistStudent extends StudentType
case object StudentSabbaticalOfficer extends StudentType
