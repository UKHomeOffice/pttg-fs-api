//package uk.gov.digital.ho.proving.financialstatus;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
//import com.fasterxml.jackson.module.scala.DefaultScalaModule;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//
//@Configuration
//class ServiceConfiguration {
//
//    @Bean
//    public ObjectMapper getMapper() {
//        ObjectMapper m = new ObjectMapper();
//
//        m.registerModule(new DefaultScalaModule());
//
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-M-d")));
//        m.registerModule(javaTimeModule);
//        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        m.enable(SerializationFeature.INDENT_OUTPUT);
//        return m;
//    }
//
//}
