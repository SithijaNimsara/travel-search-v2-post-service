package com.example.postservice.service;



import com.example.postservice.dto.*;
import com.example.postservice.entity.Post;
import com.example.postservice.entity.User;
import com.example.postservice.repository.PostRepository;
import com.example.postservice.security.AuthEntryPointJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class
PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    RestTemplate restTemplate;


    @Value("${external.user-service.base-url}")
    private String userServiceBaseUrl;

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Cacheable(value = "posts", key = "#userId")
    public List<PostInforDto> getAllPost(int userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.error("User is not authenticated");
                throw new IllegalStateException("User is not authenticated");
            }
            UserInforDto user = (UserInforDto) authentication.getPrincipal();
            if (user == null) {
                logger.error("User details are not found in the security context");
                throw new IllegalStateException("User details are missing");
            }

            String role = user.getRole();
            List<Post> post=null;
            if(Objects.equals(role, "BUSINESS")) {
                post = postRepository.findAllByHotelId(userId);
            }else if(Objects.equals(role, "USER")) {
                post = postRepository.findAll();
            }else {
                logger.error("Invalid user role: {}", role);
                throw new IllegalArgumentException("Invalid user role");
            }
            if (post == null || post.isEmpty()) {
                logger.info("No posts found for user with ID: {}", userId);
                throw new IllegalArgumentException("No posts found");
            }

            int logInUserId = user.getUserId();
            List<PostInforDto> postInforDtos = post.stream().map(element -> {
                PostDetailsDto postDetailsDto = PostDetailsDto.builder()
                        .postId(element.getPostId())
                        .caption(element.getCaption())
                        .time(element.getTime())
                        .postImage(element.getImage())
                        .build();

                HotelDetailsDto hotelDetailsDto = HotelDetailsDto.builder()
                        .hotelId(element.getHotelId().getUserId())
                        .name(element.getHotelId().getName())
                        .hotelImage(element.getHotelId().getImage())
                        .build();

                int likeCount = postRepository.countLikeByPostId(element.getPostId());
                BigInteger isLike = postRepository.checkLikeByUserIdAndPostId(logInUserId, element.getPostId());

                LikeDetailsDto likeDetailsDto = LikeDetailsDto.builder()
                        .likeCount(likeCount)
                        .liked((isLike.compareTo(BigInteger.valueOf(0)) > 0))
                        .build();

                PostInforDto postInforDto = PostInforDto.builder()
                        .postDetailsDto(postDetailsDto)
                        .hotelDetailsDto(hotelDetailsDto)
                        .likeDetailsDto(likeDetailsDto)
                        .build();
                return postInforDto;
            }).collect(Collectors.toList());

            return postInforDtos;
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
            throw new RuntimeException("Internal server error", e);
        }
    }


    public ResponseEntity<Void> savePost(MultipartFile image, CreatePostDto createPostDto, HttpServletRequest request) throws IOException {

        String headerAuth = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", headerAuth);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String userUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                .pathSegment("user")
                .queryParam("postId", String.valueOf(createPostDto.getHotelId()))
                .toUriString();

        try {
            ResponseEntity<User> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, entity, User.class);
            User user = userResponse.getBody();

            Post post = Post.builder()
                    .caption(createPostDto.getCaption())
                    .image(image.getBytes())
                    .hotelId(user)
                    .build();
            postRepository.save(post);
            logger.info("Post saved successfully");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (HttpClientErrorException e) {
            logger.error("Hotel ID not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            logger.error("Exception: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Void> deletePost(int postId){
        try {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isPresent()) {
                postRepository.deleteById(postId);
                logger.info("Post with ID {} deleted successfully", postId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else {
                logger.warn("Post with ID {} not found", postId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException e) {
            logger.error("Error occurred while deleting post with ID {}: {}", postId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Post> getPostByPostId(int postId) {
        Post post;
        try {
            post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("RuntimeException- {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
