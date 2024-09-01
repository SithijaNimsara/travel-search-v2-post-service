package com.example.postservice.controller;


import com.example.postservice.dto.CreatePostDto;
import com.example.postservice.dto.PostInforDto;
import com.example.postservice.entity.Post;
import com.example.postservice.error.HttpExceptionResponse;
import com.example.postservice.service.PostService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.*;


@RestController
public class PostController {

    @Autowired
    PostService postService;


    @GetMapping(value = "/all-post/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('BUSINESS')")
    @ApiOperation(value = "Get all post by it's ID", nickname = "getAllPostByIdOperation")
    @ApiResponses({
            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity<List<PostInforDto>> getAllPostById(
            @ApiParam(value = "Get all the post by ID.", required = true) @PathVariable("userId") int userId) {
        return postService.getAllPost(userId);
    }

    @PostMapping(value = "/create-post", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('BUSINESS')")
    @ApiOperation(value = "Create post", nickname = "createPostOperation")
    @ApiResponses({
            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity createNewPost(
            @ApiParam(value = "Post's image.") @RequestPart("image") MultipartFile image,
            @ApiParam(value = "Post's details") @RequestPart("data") CreatePostDto data,
            HttpServletRequest request) throws IOException {
        return postService.savePost(image, data, request);
    }

    @DeleteMapping(value = "/delete-post/{postId}")
    @PreAuthorize("hasRole('BUSINESS')")
    @ApiOperation(value = "Delete post", nickname = "deletePostOperation")
    @ApiResponses({
            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity deletePostById(
            @ApiParam(value = "Delete post by ID.", required = true) @PathVariable("postId") int postId) {
        return postService.deletePost(postId);
    }

    @GetMapping(value = "/get-postById/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Post> getPostById(
            @ApiParam(value = "Get all the post by ID.", required = true) @PathVariable("postId") int postId) {
        return postService.getPostByPostId(postId);
    }

}
