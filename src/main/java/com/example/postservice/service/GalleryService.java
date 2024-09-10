package com.example.postservice.service;


import com.example.postservice.dto.GalleryInforDto;
import com.example.postservice.entity.Gallery;
import com.example.postservice.entity.User;
import com.example.postservice.repository.GalleryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Service
public class GalleryService {

//    @Autowired
//    UserRepository userRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GalleryRepository galleryRepository;

    @Value("${external.user-service.base-url}")
    private String userServiceBaseUrl;

    GalleryInforDto galleryInforDto;

    private static final Logger logger = LoggerFactory.getLogger(GalleryService.class);

    public ResponseEntity<GalleryInforDto> getImageByIndex(int hotelId, int index, HttpServletRequest request) {
        try {
            Pageable pageable = PageRequest.of(index, 1);

            String headerAuth = request.getHeader("Authorization");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", headerAuth);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String userUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                    .pathSegment("user")
                    .queryParam("postId", String.valueOf(hotelId))
                    .toUriString();

            ResponseEntity<User> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, entity, User.class);
            logger.info(userResponse.toString(), userResponse.getStatusCode());
            User user = userResponse.getBody();

            if (user == null) {
                logger.error("User not found : {}", hotelId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Page<Gallery> galleryPage = galleryRepository.findByUserId(user, pageable);
            List<Gallery> galleryList = galleryPage.getContent();
            long totalElements = galleryPage.getTotalElements();

            if (totalElements>0 && totalElements>index) {
                galleryInforDto = GalleryInforDto.builder()
                        .galleryId(galleryList.get(0).getGalleryId())
                        .image(galleryList.get(0).getImage())
                        .hotelId(galleryList.get(0).getUserId().getUserId())
                        .currentPage(galleryPage.getNumber())
                        .totalItem(galleryPage.getTotalPages())
                        .build();
                return new ResponseEntity<>(galleryInforDto, HttpStatus.OK);
            }else {
                logger.error("No gallery found for index: {}", index);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (HttpClientErrorException e) {
            logger.error("Hotel ID not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Void> addImageByHotelId(MultipartFile image, int hotelId, HttpServletRequest request) throws IOException {
        String headerAuth = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", headerAuth);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String userUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                .pathSegment("user")
                .queryParam("postId", String.valueOf(hotelId))
                .toUriString();

        try {
            ResponseEntity<User> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, entity, User.class);
            User user = userResponse.getBody();

            Gallery gallery = Gallery.builder()
                    .image(image.getBytes())
                    .userId(user)
                    .build();
            galleryRepository.save(gallery);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (HttpClientErrorException e) {
            logger.error("Hotel ID not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
