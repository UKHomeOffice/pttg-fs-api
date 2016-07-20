package uk.gov.digital.ho.proving.financialstatus.client

import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.http._
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.retry.{RetryCallback, RetryContext}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

case class HttpClientResponse(httpStatus: HttpStatus, body: String)

@Service()
class HttpUtils @Autowired()(rest: RestTemplate,
                             @Value("${retry.attempts}") maxAttempts: Int,
                             @Value("${retry.delay}") backOffPeriod: Long
                            ) {
  private val headers = new HttpHeaders()
  private val emptyBody = ""
  private val retryTemplate = createRetryTemplate(maxAttempts, backOffPeriod)

  def createRetryTemplate(maxAttempts: Int, backOffPeriod: Long): RetryTemplate = {
    val retryTemplate = new RetryTemplate()
    val simpleRetryPolicy = new SimpleRetryPolicy()
    simpleRetryPolicy.setMaxAttempts(maxAttempts)

    val fixedBackOffPolicy = new FixedBackOffPolicy()
    fixedBackOffPolicy.setBackOffPeriod(backOffPeriod)
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy)
    retryTemplate.setRetryPolicy(simpleRetryPolicy)
    retryTemplate
  }

  def performRequest(url: String): HttpClientResponse = {

    val requestEntity = new HttpEntity[String](emptyBody, headers)
    val responseEntity = retryTemplate.execute(new RetryableCall(url, requestEntity))

    HttpClientResponse(responseEntity.getStatusCode, responseEntity.getBody)
  }

  class RetryableCall(url: String, requestEntity: HttpEntity[String]) extends RetryCallback[ResponseEntity[String], RuntimeException] {
    def doWithRetry(retryContext: RetryContext): ResponseEntity[String] = {
      rest.exchange(this.url, HttpMethod.GET, requestEntity, classOf[String])
    }
  }

}
