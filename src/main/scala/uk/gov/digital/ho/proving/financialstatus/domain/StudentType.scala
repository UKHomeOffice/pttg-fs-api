package uk.gov.digital.ho.proving.financialstatus.domain

object StudentType {

  val DOCTORATE = "doctorate"
  val NON_DOCTORATE = "nondoctorate"

  val values = Vector(DOCTORATE, NON_DOCTORATE)

  def getStudentType(studentType: String): StudentType = {
    studentType match {
      case DOCTORATE => Doctorate
      case NON_DOCTORATE => NonDoctorate
      case _ => Unknown(studentType)
    }
  }
}

sealed trait StudentType
case class Unknown(value: String) extends StudentType
case object Doctorate extends StudentType
case object NonDoctorate extends StudentType
