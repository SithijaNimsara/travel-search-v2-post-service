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
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    RestTemplate restTemplate;


    @Value("${external.user-service.base-url}")
    private String userServiceBaseUrl;

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);


    public ResponseEntity<List<PostInforDto>> getAllPost(int userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInforDto user = (UserInforDto) authentication.getPrincipal();
        String role = user.getRole();
//        String role = userRepository.getRoleById(userId);
        logger.info("user "+user);
        List<Post> post=null;
        if(Objects.equals(role, "BUSINESS")) {
            post = postRepository.findAllByHotelId(userId);
        }else if(Objects.equals(role, "USER")) {
            post = postRepository.findAll();
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
//        List<Post> post = postRepository.findAll();

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


        return new ResponseEntity<>(postInforDtos, HttpStatus.OK);
    }


    public ResponseEntity savePost(MultipartFile image, CreatePostDto createPostDto, HttpServletRequest request) throws IOException {

        String headerAuth = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", headerAuth);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String userUrl = UriComponentsBuilder.fromHttpUrl(userServiceBaseUrl)
                .pathSegment("get-userById", String.valueOf(createPostDto.getHotelId()))
                .toUriString();

        ResponseEntity<User> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, entity, User.class);
        User user = userResponse.getBody();

        Post post = Post.builder()
                        .caption(createPostDto.getCaption())
                        .image(image.getBytes())
                        .hotelId(user)
                        .build();
        postRepository.save(post);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    public ResponseEntity deletePost(int postId){
        Post post = postRepository.findById(postId).orElse(null);
        if(post != null) {
//            postRepository.deleteUserPostsByPostId(postId);
            postRepository.deleteById(postId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Post> getPostByPostId(int postId) {
        Post post = postRepository.findById(postId).orElse(null);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

}
