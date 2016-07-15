package uk.gov.digital.ho.proving.financialstatus.api

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.context.support.ResourceBundleMessageSource

@Configuration
class LocalizationConfiguration {

 @Bean
  def messageSource: ResourceBundleMessageSource = {
    val messageSource = new ResourceBundleMessageSource()
    messageSource.setBasename("messages")
    messageSource
  }

}
