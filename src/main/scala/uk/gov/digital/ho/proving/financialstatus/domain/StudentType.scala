package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class StudentTypeChecker @Autowired()(@Value("${student.type.doctorate.extension}") val doctorate: String,
                                      @Value("${student.type.general}") val general: String,
                                      @Value("${student.type.post.grad.doctor.dentist}") val doctorDentist: String,
                                      @Value("${student.type.student.union.sabbatical.officer}") val studentUnionSabbaticalOfficer: String
                                     ) {

  private val DOCTORATE_EXTENSION_SCHEME = doctorate
  private val GENERAL_STUDENT = general
  private val POST_GRADUATE_DOCTOR_DENTIST = doctorDentist
  private val STUDENT_UNION_SABBATICAL_OFFICER = studentUnionSabbaticalOfficer

  val values = Vector(DOCTORATE_EXTENSION_SCHEME, GENERAL_STUDENT, POST_GRADUATE_DOCTOR_DENTIST, STUDENT_UNION_SABBATICAL_OFFICER)

  def getStudentType(studentType: String): StudentType = {
    studentType match {
      case DOCTORATE_EXTENSION_SCHEME => DoctorateExtensionStudent
      case GENERAL_STUDENT => GeneralStudent
      case POST_GRADUATE_DOCTOR_DENTIST => PostGraduateDoctorDentistStudent
      case STUDENT_UNION_SABBATICAL_OFFICER => StudentUnionSabbaticalOfficerStudent
      case _ => UnknownStudent(studentType)
    }
  }
}

sealed trait StudentType

case class UnknownStudent(value: String) extends StudentType

case object DoctorateExtensionStudent extends StudentType

case object GeneralStudent extends StudentType

case object PostGraduateDoctorDentistStudent extends StudentType

case object StudentUnionSabbaticalOfficerStudent extends StudentType
