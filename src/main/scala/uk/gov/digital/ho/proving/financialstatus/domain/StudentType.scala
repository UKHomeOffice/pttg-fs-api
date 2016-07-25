package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class StudentTypeChecker @Autowired()(@Value("${student.type.doctorate}") val doctorate: String,
                                      @Value("${student.type.non.doctorate}") val nonDoctorate: String,
                                      @Value("${student.type.post.grad.doctor.dentist}") val doctorDentist: String) {

  val DOCTORATE = doctorate
  val NON_DOCTORATE = nonDoctorate
  val DOCTOR_DENTIST = doctorDentist

  val values = Vector(DOCTORATE, NON_DOCTORATE, DOCTOR_DENTIST)

  def getStudentType(studentType: String): StudentType = {
    studentType match {
      case DOCTORATE => Doctorate
      case NON_DOCTORATE => NonDoctorate
      case DOCTOR_DENTIST => DoctorDentist
      case _ => Unknown(studentType)
    }
  }
}

sealed trait StudentType
case class Unknown(value: String) extends StudentType
case object Doctorate extends StudentType
case object NonDoctorate extends StudentType
case object DoctorDentist extends StudentType
