package uk.gov.digital.ho.proving.financialstatus.api

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.springframework.context.annotation.{Bean, Configuration, Primary}
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class ServiceConfiguration {

    @Bean
    @Primary
    def getMapper():  ObjectMapper = {
        val m = new ObjectMapper()
        m.registerModule(DefaultScalaModule)

//        val javaTimeModule = new JavaTimeModule()
//        javaTimeModule.addDeserializer(classOf[LocalDate], new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-M-d")))
//        m.registerModule(javaTimeModule)
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        m.enable(SerializationFeature.INDENT_OUTPUT)
        m
    }

//    @Bean
//    def getMapperBuilder: Jackson2ObjectMapperBuilder = {
//        val builder = new Jackson2ObjectMapperBuilder()
//        builder.configure(getMapper())
//        builder
//    }

}
