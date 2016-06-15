package uk.gov.digital.ho.proving.financialstatus.api

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}

class CustomSerializer extends JsonSerializer[BigDecimal] {

  override def serialize(value: BigDecimal , jgen: JsonGenerator, provider: SerializerProvider ) {
    // Set the default BigDecimal serialization to 2 decimal places
    jgen.writeString(value.setScale(2, BigDecimal.RoundingMode.HALF_UP).toString())
  }

}
