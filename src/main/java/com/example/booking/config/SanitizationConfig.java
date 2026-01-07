package com.example.booking.config;

import com.example.booking.service.XssSanitizerService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class SanitizationConfig {

    @Bean
    public Module xssSanitizationModule(XssSanitizerService xssSanitizerService) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StringSanitizerDeserializer(xssSanitizerService));
        return module;
    }

    public static class StringSanitizerDeserializer extends JsonDeserializer<String> {
        private final XssSanitizerService xssSanitizerService;

        public StringSanitizerDeserializer(XssSanitizerService xssSanitizerService) {
            this.xssSanitizerService = xssSanitizerService;
        }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            return xssSanitizerService.sanitize(value);
        }
    }
}
