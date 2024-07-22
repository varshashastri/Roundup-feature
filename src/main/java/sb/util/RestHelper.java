package sb.util;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestHelper {

    @Value("${bearer.token}")
    private String token;

    @Autowired
    private RestTemplate restTemplate;

    public <T> HttpEntity<T> getHttpEntityWithHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(httpHeaders);
    }

    public <T> HttpEntity<T> getHttpEntityWithRequestBodyAndHeaders(@NonNull final T body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(body, httpHeaders);
    }

    public <T,S> ResponseEntity<S> performPut(@NonNull final String url, @NonNull final T body, @NonNull final Class<S> clazz) {
        HttpEntity<T> entity = getHttpEntityWithRequestBodyAndHeaders(body);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, clazz);
    }

    public <T,S> ResponseEntity<S> performGet(@NonNull final String url, @NonNull final Class<S> clazz) {
        HttpEntity<T> entity = getHttpEntityWithHeaders();
        return restTemplate.exchange(url, HttpMethod.GET, entity, clazz);
    }
}
