package uk.gov.digital.ho.proving.financialstatus.api

import java.time.LocalDate

import com.fasterxml.jackson.annotation.{JsonFormat, JsonProperty}

case class Account(sortCode: String, number: String)

case class AccountTransaction(@JsonProperty("kjk") @JsonFormat(shape=JsonFormat.Shape.STRING) date: LocalDate,
                              description: String,
                              amount: BigDecimal,
                              balance: Option[BigDecimal])
