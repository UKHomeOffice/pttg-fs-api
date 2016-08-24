package uk.gov.digital.ho.proving.financialstatus.api.configuration

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation._
import org.springframework.core.env.Environment
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

import scala.collection.JavaConverters._

@PropertySources(value = Array(
  new PropertySource(value = Array("classpath:dsp-default.properties"), ignoreResourceNotFound = false),
  new PropertySource(value = Array("classpath:/developer/developer-default.properties"), ignoreResourceNotFound = true),
  new PropertySource(value = Array("classpath:/developer/${user.name}-default.properties"), ignoreResourceNotFound = true)
))
@Configuration
@ComponentScan(Array("uk.gov.digital.ho.proving.financialstatus"))
@ControllerAdvice
class ServiceConfiguration  extends WebMvcConfigurationSupport {

  @Autowired
  var environment: Environment = _

  @Bean
  @Primary
  def objectMapper: ObjectMapper = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)

    val javaTimeModule = new JavaTimeModule()
    javaTimeModule.addDeserializer(classOf[LocalDate], new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-M-d")))
    mapper.registerModule(javaTimeModule)
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    mapper.enable(SerializationFeature.INDENT_OUTPUT)

    val simpleModule = new SimpleModule
    val customSerializer = new CustomSerializer()
    simpleModule.addSerializer(classOf[BigDecimal], customSerializer)
    mapper.registerModule(simpleModule)

    mapper
  }

  @Bean
  def mappingJackson2HttpMessageConverter: MappingJackson2HttpMessageConverter = {
    val converter = new MappingJackson2HttpMessageConverter
    converter.setObjectMapper(objectMapper)
    converter
  }

  @Bean
  override def requestMappingHandlerAdapter: RequestMappingHandlerAdapter = {
    val requestMappingHandlerAdapter = super.requestMappingHandlerAdapter
    requestMappingHandlerAdapter.setMessageConverters(List[HttpMessageConverter[_]](mappingJackson2HttpMessageConverter).asJava)
    requestMappingHandlerAdapter
  }

  @Bean
  @ConfigurationProperties(prefix = "rest.connection")
  def customHttpRequestFactory: HttpComponentsClientHttpRequestFactory = {
    new HttpComponentsClientHttpRequestFactory()
  }

  @Bean
  def customRestTemplate: RestTemplate = {
    new RestTemplate(customHttpRequestFactory)
  }


}
