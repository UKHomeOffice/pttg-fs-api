package uk.gov.digital.ho.proving.financialstatus.audit

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.boot.actuate.audit.{AuditEvent, AuditEventRepository}
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

class JsonLoggingNonPersistentAuditEventRepository extends AuditEventRepository {

  val LOGGER: Logger = LoggerFactory.getLogger(classOf[JsonLoggingNonPersistentAuditEventRepository])

  val AUDIT_EVENT_LOG_MARKER: String = "AUDIT"

  private var mapper: ObjectMapper = new ObjectMapper

  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JavaTimeModule())
  mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"))
  mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  mapper.enable(SerializationFeature.INDENT_OUTPUT)

  override def add(event: AuditEvent): Unit = {
    val json = jsonOf(event)
    LOGGER.info(s"$AUDIT_EVENT_LOG_MARKER: $json")
  }

  def jsonOf(event: AuditEvent): String = {
    mapper.writeValueAsString(event)
  }

  override def find(after: Date): util.List[AuditEvent] = List.empty.asJava

  override def find(principal: String, after: Date): util.List[AuditEvent] = List.empty.asJava

  override def find(principal: String, after: Date, `type`: String): util.List[AuditEvent] = List.empty.asJava
}
