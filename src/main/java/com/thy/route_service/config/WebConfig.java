package com.thy.route_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //  Tüm endpoint'ler için geçerli
                .allowedOrigins("http://localhost:5175")
                //.allowedOrigins(""https://thy-app.com")       // Production, ortama göre değişiypr
                // "https://test-thy-app.com"    // Test environment
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type")
                .allowCredentials(true) // Cookie, HTTP Authentication gönderebilir
                .maxAge(3600);//  Preflight response'u 1 saat cache'le (saniye cinsinden)
    }
    /*
    WebConfig, CORS (Cross-Origin Resource Sharing) politikalarını yapılandırdığımız dosya.
     Frontend uygulaması farklı bir domain'den backend API'ye istek atabilsin diye izin veriyoruz.
      Yoksa browser'ın güvenlik politikası nedeniyle istekler bloke edilir.
     */
}