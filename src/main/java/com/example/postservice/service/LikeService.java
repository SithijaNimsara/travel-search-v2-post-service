package com.example.postservice.service;


import com.example.postservice.dto.LikeRequestDto;
import com.example.postservice.dto.PostDto;
import com.example.postservice.dto.UserDto;
import com.example.postservice.entity.Post;
import com.example.postservice.entity.User;
import com.example.postservice.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class LikeService {

    @Autowired
    PostRepository postRepository;

    @Value("${external.user-service.base-url}")
    private String userServiceBaseUrl;

    @Autowired
    RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);

    public ResponseEntity<String> addLike(LikeRequestDto likeRequestDto, HttpServletRequest request) {

        String headerAuth = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", headerAuth);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String userUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                .pathSegment("user")
                .queryParam("postId", String.valueOf(likeRequestDto.getUserId()))
                .toUriString();

        UserDto user;
        try {
            ResponseEntity<UserDto> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, entity, UserDto.class);
            logger.info("User Response received: {}", userResponse);
            user = userResponse.getBody();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (user == null) {
            logger.error("User not found");
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Post post = postRepository.findById(likeRequestDto.getPostId()).orElse(null);
        if (post == null) {
            logger.error("Post not found");
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }
        else if("USER".equals(user.getRole()))  {
            PostDto postDto = PostDto.builder()
                    .postId(post.getPostId())
                    .caption(post.getCaption())
                    .time( post.getTime())
                    .image( post.getImage())
                    .hotelId(post.getHotelId())
                    .postUsers(post.getPostUsers())
                    .build();
            user.getUserPosts().add(postDto);

            String likedUserUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                    .pathSegment("user")
                    .toUriString();
            HttpEntity<UserDto> newEntity = new HttpEntity<>(user, headers);
            try {
                ResponseEntity<User> response = restTemplate.exchange(likedUserUrl, HttpMethod.POST, newEntity, User.class);
                logger.info(Objects.requireNonNull(response.getBody()).toString());
                return new ResponseEntity<>("Success", HttpStatus.CREATED);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else if ("BUSINESS".equals(user.getRole())) {
            logger.error("Error: Invalid User Role");
            return new ResponseEntity<>("Error: Invalid User Role", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Error: Something went Wrong", HttpStatus.BAD_REQUEST);
    }


}
