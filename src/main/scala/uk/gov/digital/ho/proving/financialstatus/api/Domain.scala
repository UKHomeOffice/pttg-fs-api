package uk.gov.digital.ho.proving.financialstatus.api

import java.time.LocalDate

import com.fasterxml.jackson.annotation.{JsonFormat, JsonProperty}

import scala.beans.BeanProperty

case class Account(sortCode: String, number: String)

case class AccountTransaction(@JsonProperty("kjk") date: LocalDate,
                              description: String,
                              amount: BigDecimal,
                              balance: Option[BigDecimal])
