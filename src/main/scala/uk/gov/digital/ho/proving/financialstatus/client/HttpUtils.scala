package uk.gov.digital.ho.proving.financialstatus.client

import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod, HttpStatus}
import org.springframework.web.client.RestTemplate

import scala.util.Try

case class HttpClientResponse(httpStatus: HttpStatus, body: String)

object HttpUtils {

  private val headers = new HttpHeaders()
  private val rest = new RestTemplate()
  private val emptyBody = ""

  def performRequest(url: String) = {

    val requestEntity = new HttpEntity[String](emptyBody, headers)
    val responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, classOf[String])

    HttpClientResponse(responseEntity.getStatusCode, responseEntity.getBody)

  }
}
