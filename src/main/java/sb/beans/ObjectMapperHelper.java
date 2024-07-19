package sb.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ObjectMapperHelper {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
