package com.ead.authuser.clients;

import com.ead.authuser.dtos.CourseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class CourseClient {

    @Autowired
    private RestTemplate restTemplate;

    private String REQUEST_URI = "http://localhost:8082";

    public Page<CourseDTO> getAllCoursesByUser(UUID userId, Pageable pageable){
        List<CourseDTO> searchResult = null;
        String requestUrl = REQUEST_URI + "/courses?userId=" + userId + "&page=" + pageable.getPageNumber() + "&size="
                + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(":", ",");
        log.debug("Request URL: {} ", requestUrl);
        log.info("Request URL: {} ", requestUrl);
        try{
            log.debug("Response Number of Elements: {} ", searchResult.size());
        } catch (HttpStatusCodeException exception){
            log.error("Error request /courses endpoint exception {} ", exception);
        }
        log.info("Ending request /courses userId {} ", userId);
        return ;
    }
}
