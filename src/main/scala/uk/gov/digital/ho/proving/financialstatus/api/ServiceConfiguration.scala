package uk.gov.digital.ho.proving.financialstatus.api

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration, Primary}
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.config.annotation.{EnableWebMvc, WebMvcConfigurationSupport, WebMvcConfigurerAdapter}
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

import scala.collection.JavaConverters._

@Configuration
@ComponentScan(Array("uk.gov.digital.ho.proving.financialstatus"))
@ControllerAdvice
class ServiceConfiguration extends WebMvcConfigurationSupport{

  @Bean
  @Primary
  def objectMapper = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)

    val javaTimeModule = new JavaTimeModule()
    javaTimeModule.addDeserializer(classOf[LocalDate], new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-M-d")))
    mapper.registerModule(javaTimeModule)
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    mapper.enable(SerializationFeature.INDENT_OUTPUT)

    val simpleModule = new SimpleModule
    val customSerializer = new CustomSerializer()
    simpleModule.addSerializer(classOf[BigDecimal],customSerializer)
    mapper.registerModule(simpleModule)

    mapper
  }

  @Bean
  def mappingJackson2HttpMessageConverter = {
    val converter = new MappingJackson2HttpMessageConverter
    converter.setObjectMapper(objectMapper)
    converter
  }

  @Bean
  override def requestMappingHandlerAdapter = {
    val requestMappingHandlerAdapter = super.requestMappingHandlerAdapter
    requestMappingHandlerAdapter.setMessageConverters(List[HttpMessageConverter[_]](mappingJackson2HttpMessageConverter).asJava)
    requestMappingHandlerAdapter
  }

}
