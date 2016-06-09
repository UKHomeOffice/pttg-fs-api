package uk.gov.digital.ho.proving.financialstatus

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class ServerConfig

object ServiceRunner {
  def main(args: Array[String]) {
    val ctx: ApplicationContext = SpringApplication.run(classOf[ServerConfig])
    val dispatcherServlet: DispatcherServlet = ctx.getBean("dispatcherServlet").asInstanceOf[DispatcherServlet]
    dispatcherServlet.setThrowExceptionIfNoHandlerFound(true)
  }
}
