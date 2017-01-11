package uk.gov.digital.ho.proving.financialstatus.domain

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service

@Service
class CourseTypeChecker @Autowired()(@Value("${course.type.main}") val main: String,
                                     @Value("${course.type.pre.sessional}") val preSessional: String
                                    ) {

  private val MAIN = main
  private val PRE_SESSIONAL = preSessional

  val values = Vector(MAIN, PRE_SESSIONAL)

  def getCourseType(courseType: String): CourseType = {
    courseType.toLowerCase() match {
      case MAIN => MainCourse
      case PRE_SESSIONAL => PreSessionalCourse
      case _ => UnknownCourse(courseType)
    }
  }
}

sealed trait CourseType
case class UnknownCourse(value: String) extends CourseType
case object MainCourse extends CourseType
case object PreSessionalCourse extends CourseType
