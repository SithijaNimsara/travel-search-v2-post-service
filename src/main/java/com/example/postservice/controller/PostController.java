package com.example.postservice.controller;


import com.example.postservice.dto.CreatePostDto;
import com.example.postservice.dto.PostInforDto;
import com.example.postservice.entity.Post;
import com.example.postservice.error.HttpExceptionResponse;
import com.example.postservice.service.PostService;
import com.sun.xml.bind.v2.TODO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static javax.servlet.http.HttpServletResponse.*;


@RestController()
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostService postService;


    @GetMapping(params = "userId")
    @PreAuthorize("hasRole('USER') or hasRole('BUSINESS')")
    public ResponseEntity<List<PostInforDto>> getAllPostById(@RequestParam(value = "userId", required = true) int userId) {
        try {
            List<PostInforDto> postInforDtos = postService.getAllPost(userId);

            if (postInforDtos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(postInforDtos, HttpStatus.OK);

        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<Void> createNewPost(@RequestPart("image") MultipartFile image,
                                              @RequestPart("data") CreatePostDto data,
                                              HttpServletRequest request) throws IOException {
        return postService.savePost(image, data, request);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<Void> deletePostById(@RequestParam(value = "postId", required = true) int postId) {
        return postService.deletePost(postId);
    }

    // TODO
    @GetMapping(params = "postId")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Post> getPostById(@RequestParam(value = "postId", required = true) int postId) {
        return postService.getPostByPostId(postId);
    }

}
