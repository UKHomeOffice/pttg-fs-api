package uk.gov.digital.ho.proving.financialstatus.api

import java.text.DecimalFormat

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebMvc
class ServerConfig

object ServiceRunner {

  private val LOGGER = LoggerFactory.getLogger(classOf[ServerConfig])

  def main(args: Array[String]) {
    val ctx: ApplicationContext = SpringApplication.run(classOf[ServerConfig])
    val dispatcherServlet: DispatcherServlet = ctx.getBean("dispatcherServlet").asInstanceOf[DispatcherServlet]
    dispatcherServlet.setThrowExceptionIfNoHandlerFound(true)

    logMemoryAllocations()

  }

  def logMemoryAllocations(): Unit = {

    val df = new DecimalFormat("#,##0.#")

    val max = Runtime.getRuntime().maxMemory()
    val total = Runtime.getRuntime().totalMemory()
    val free = Runtime.getRuntime().freeMemory()
    val used = total - free

    LOGGER.debug("Maximum memory in bytes: %sm  (%d)".format(df.format(max / (1024 * 1024)), max))
    LOGGER.debug("  Total memory in bytes: %sm  (%d)".format(df.format(total / (1024 * 1024)), total))
    LOGGER.debug("   Free memory in bytes: %sm  (%d)".format(df.format(free / (1024 * 1024)), free))
    LOGGER.debug("   Used memory in bytes: %sm  (%d)".format(df.format(used / (1024 * 1024)), used))
  }
}
