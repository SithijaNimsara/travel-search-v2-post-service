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

//    @Autowired
//    UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);

    public ResponseEntity addLike(LikeRequestDto likeRequestDto, HttpServletRequest request) {

//        User user = userRepository.findById(likeRequestDto.getUserId()).orElse(null);
        // TODO User user=

        String headerAuth = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", headerAuth);
//        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String userUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                .pathSegment("get-userById", String.valueOf(likeRequestDto.getUserId()))
                .toUriString();

        ResponseEntity<UserDto> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, entity, UserDto.class);
        UserDto user = userResponse.getBody();

        Post post = postRepository.findById(likeRequestDto.getPostId()).orElse(null);

        if(post != null && Objects.requireNonNull(user).getRole().equals("USER")) {

            PostDto postDto = PostDto.builder()
                    .postId(post.getPostId())
                    .caption(post.getCaption())
                    .time( post.getTime())
                    .image( post.getImage())
                    .hotelId(post.getHotelId())
                    .postUsers(post.getPostUsers())
                    .build();
            Set<PostDto> postDtoSet = new HashSet<>(List.of(postDto));
            user.getUserPosts().add(postDto);

//            user.getUserPosts().add(postDto);
            logger.info(user.toString());
//            UserDto userDto = new UserDto(user.getUserId(), user.getName(),
//                    user.getEmail(), user.getPassword(), user.getAddress(),
//                    user.getState(), user.getCountry(), user.getRole(),
//                    user.getImage(), postDtoSet);

//            postDto.setPostUsers( new HashSet<>(List.of(userDto)));
//            postDtoSet.add()

//            userRepository.save(user);
            String likedUserUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                    .pathSegment("liked-user")
                    .toUriString();

            HttpEntity<UserDto> newEntity = new HttpEntity<>(user, headers);

            ResponseEntity<User> response = restTemplate.exchange(likedUserUrl, HttpMethod.POST, newEntity, User.class);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }else {
            assert user != null;
            if (user.getRole().equals("BUSINESS")) {
                return new ResponseEntity<String>("Invalid User Role", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<String>("Something went Wrong", HttpStatus.BAD_REQUEST);
    }


}
