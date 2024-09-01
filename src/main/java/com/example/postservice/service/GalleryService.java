package com.example.postservice.service;


import com.example.postservice.dto.GalleryInforDto;
import com.example.postservice.entity.Gallery;
import com.example.postservice.entity.User;
import com.example.postservice.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
    

    public ResponseEntity<GalleryInforDto> getImageByIndex(int hotelId, int index, HttpServletRequest request) {

        try {
            Pageable pageable = PageRequest.of(index, 1);

            String headerAuth = request.getHeader("Authorization");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", headerAuth);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String userUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                    .pathSegment("get-userById", String.valueOf(hotelId))
                    .toUriString();

            ResponseEntity<User> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, entity, User.class);
            User user = userResponse.getBody();

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
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity addImageByHotelId(MultipartFile image, int hotelId, HttpServletRequest request) throws IOException {
        String headerAuth = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", headerAuth);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String userUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                .pathSegment("get-userById", String.valueOf(hotelId))
                .toUriString();

        ResponseEntity<User> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, entity, User.class);
        User user = userResponse.getBody();
        
        Gallery gallery = Gallery.builder()
                            .image(image.getBytes())
                            .userId(user)
                            .build();
        galleryRepository.save(gallery);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
