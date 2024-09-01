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
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static javax.servlet.http.HttpServletResponse.*;


@RestController
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping(value = "/add-like")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Add like to post by user ID", nickname = "addLikeOperation")
    @ApiResponses({
            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity likePostByUserId(@ApiParam(value = "like information") @RequestBody LikeRequestDto likeRequestDto,
                                           HttpServletRequest request) throws JsonProcessingException {
        return likeService.addLike(likeRequestDto, request);
    }




}
