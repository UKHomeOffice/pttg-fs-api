package uk.gov.digital.ho.proving.financialstatus.client

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod, HttpStatus}
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

case class HttpClientResponse(httpStatus: HttpStatus, body: String)

@Service()
class HttpUtils @Autowired()(rest: RestTemplate) {

  private val headers = new HttpHeaders()
  private val emptyBody = ""

  def performRequest(url: String) = {

    val requestEntity = new HttpEntity[String](emptyBody, headers)
    val responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, classOf[String])

    HttpClientResponse(responseEntity.getStatusCode, responseEntity.getBody)

  }
}
