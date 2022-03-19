package com.ead.authuser.clients;

import com.ead.authuser.dtos.CourseDTO;
import com.ead.authuser.dtos.ResponsePageDTO;
import com.ead.authuser.services.UtilsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class CourseClient {

    @Autowired
    private UtilsService utilsService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ead.api.url.course}")
    private String REQUEST_URL_COURSE;

    //@Retry(name = "default-retry-instance", fallbackMethod = "retryFallback")
    @CircuitBreaker(name = "default-circuitbreaker-instance")
    public Page<CourseDTO> getAllCoursesByUser(UUID userId, Pageable pageable){
        List<CourseDTO> searchResult = null;
        String requestUrl = REQUEST_URL_COURSE + utilsService.generateUrlGetAllCoursesByUser(userId, pageable);
        log.debug("Request URL: {} ", requestUrl);
        log.info("Request URL: {} ", requestUrl);
        try{
            ParameterizedTypeReference<ResponsePageDTO<CourseDTO>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ResponsePageDTO<CourseDTO>> responseEntity = restTemplate.exchange(requestUrl, HttpMethod.GET,null, responseType);
            searchResult = responseEntity.getBody().getContent();
            log.debug("Response Number of Elements (Courses): {} ", searchResult.size());
        } catch (HttpStatusCodeException exception){
            log.error("Error request /courses endpoint exception {} ", exception);
        }
        log.info("Ending request /courses userId {} ", userId);
        return new PageImpl<>(searchResult);
    }

    public Page<CourseDTO> retryFallback(UUID userId, Pageable pageable, Throwable exception){
        log.error("Inside retryFallback Method, caused by -> {}", exception.toString());
        List<CourseDTO> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }

    public Page<CourseDTO> circuitBreakerFallback(UUID userId, Pageable pageable, Throwable exception) {
        log.error("Inside circuitBreakerFallback Method, caused by -> {}", exception.toString());
        List<CourseDTO> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }

}
