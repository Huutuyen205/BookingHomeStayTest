package BookingHomeStay.BookingHomeStay.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Cau hinh chung cho toan bo ung dung.
 *
 * RestTemplate dung de goi cac API ben ngoai bang HTTP that su (hien dung cho
 * ZaloZnsServiceImpl de goi API OAuth + gui tin ZNS cua Zalo). Khong can them
 * dependency Maven nao moi vi RestTemplate da co san trong
 * spring-boot-starter-web.
 */
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();
    }
}
