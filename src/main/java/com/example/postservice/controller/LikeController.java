package com.example.postservice.controller;


import com.example.postservice.dto.LikeRequestDto;
import com.example.postservice.error.HttpExceptionResponse;
import com.example.postservice.service.LikeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.http.HttpServletResponse.*;


@RestController()
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> likePostByUserId(@RequestBody LikeRequestDto likeRequestDto,
                                                   HttpServletRequest request) throws JsonProcessingException {
        return likeService.addLike(likeRequestDto, request);
    }




}
