package uk.gov.digital.ho.proving.financialstatus.client

import java.util

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.http._
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.retry.{RetryCallback, RetryContext}
import org.springframework.stereotype.Service
import org.springframework.web.client.{HttpServerErrorException, ResourceAccessException, RestTemplate}

case class HttpClientResponse(httpStatus: HttpStatus, body: String)

@Service
class HttpUtils @Autowired()(rest: RestTemplate,
                             @Value("${retry.attempts}") maxAttempts: Int,
                             @Value("${retry.delay}") backOffPeriod: Long) {

  private val emptyBody = ""
  private val retryTemplate = createRetryTemplate(maxAttempts, backOffPeriod)

  class RetryableCall(url: String, requestEntity: HttpEntity[String]) extends RetryCallback[ResponseEntity[String], RuntimeException] {
    def doWithRetry(retryContext: RetryContext): ResponseEntity[String] = {
      rest.exchange(this.url, HttpMethod.GET, requestEntity, classOf[String])
    }
  }

  def performRequest(url: String, userId: String, requestId: String): HttpClientResponse = {
    val defaultHeaders = new HttpHeaders()
    defaultHeaders.add("userId", userId)
    defaultHeaders.add("requestId", requestId)
    val requestEntity = new HttpEntity[String](emptyBody, defaultHeaders)
    val responseEntity = retryTemplate.execute(new RetryableCall(url, requestEntity))
    HttpClientResponse(responseEntity.getStatusCode, responseEntity.getBody)
  }

  def exceptionsToRetry: java.util.Map[java.lang.Class[_ <: java.lang.Throwable], java.lang.Boolean] = {
    val javaMap: java.util.Map[java.lang.Class[_ <: java.lang.Throwable], java.lang.Boolean] = new util.HashMap()
    javaMap.put(classOf[ResourceAccessException], true)
    javaMap.put(classOf[HttpServerErrorException], true)
    javaMap
  }

  def createRetryTemplate(maxAttempts: Int, backOffPeriod: Long): RetryTemplate = {
    val retryTemplate = new RetryTemplate()
    val simpleRetryPolicy = new SimpleRetryPolicy(maxAttempts, exceptionsToRetry)

    val fixedBackOffPolicy = new FixedBackOffPolicy()
    fixedBackOffPolicy.setBackOffPeriod(backOffPeriod)
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy)
    retryTemplate.setRetryPolicy(simpleRetryPolicy)
    retryTemplate
  }

}
