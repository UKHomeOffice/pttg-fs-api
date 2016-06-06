package uk.gov.digital.ho.proving.financialstatus.api

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
class ServiceConfiguration {

    @Bean
    def getMapper():  ObjectMapper = {
        val m = new ObjectMapper()
        m.registerModule(DefaultScalaModule)

        val javaTimeModule = new JavaTimeModule()
        javaTimeModule.addDeserializer(classOf[LocalDate], new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-M-d")))
        m.registerModule(javaTimeModule)
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        m.enable(SerializationFeature.INDENT_OUTPUT)
        m
    }

}
