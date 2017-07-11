package uk.gov.digital.ho.proving.financialstatus.audit

import java.text.SimpleDateFormat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.audit.AuditEvent

trait LoggingAuditEventBsonMapper extends NewLineRemover {

  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[LoggingAuditEventBsonMapper])

  val AUDIT_EVENT_LOG_MARKER: String = "AUDIT"

  private val mapper: ObjectMapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JavaTimeModule())
  mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"))
  mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  mapper.enable(SerializationFeature.INDENT_OUTPUT)

  private def jsonOf(event: AuditEvent): String = mapper.writeValueAsString(event)

  def bsonOf(event: AuditEvent): Document = {
    val json = jsonOf(event)
    val jsonOnOneLine = removeNewlines(json)
    LOGGER.info(s"$AUDIT_EVENT_LOG_MARKER: $jsonOnOneLine")
    Document.parse(json)
  }

}
trait NewLineRemover {
  def removeNewlines(originalString: String): String = originalString.replaceAll("\\r\\n|\\r|\\n", " ")
}
