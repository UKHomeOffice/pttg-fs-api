package uk.gov.digital.ho.proving.financialstatus.api

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.context.annotation.{Bean, Configuration, Primary}
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

import scala.collection.JavaConverters._

@Configuration
@EnableWebMvc
class ServiceConfiguration {

    @Bean
    @Primary
    def objectMapper = {
        val m = new ObjectMapper()
        m.registerModule(DefaultScalaModule)

        val javaTimeModule = new JavaTimeModule()
        javaTimeModule.addDeserializer(classOf[LocalDate], new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-M-d")))
        m.registerModule(javaTimeModule)
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        m.enable(SerializationFeature.INDENT_OUTPUT)
        m
    }

    @Bean
    def mappingJackson2HttpMessageConverter = {
        val converter = new MappingJackson2HttpMessageConverter
        converter.setObjectMapper(objectMapper)
        converter
    }

    @Bean
    def requestMappingHandlerAdapter = {
        val requestMappingHandlerAdapter = new RequestMappingHandlerAdapter
        requestMappingHandlerAdapter.setMessageConverters(List[HttpMessageConverter[_]](mappingJackson2HttpMessageConverter).asJava)
        requestMappingHandlerAdapter
    }

}
