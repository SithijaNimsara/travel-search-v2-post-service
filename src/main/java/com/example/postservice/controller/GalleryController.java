package com.example.postservice.controller;


import com.example.postservice.dto.GalleryInforDto;
import com.example.postservice.service.GalleryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
public class GalleryController {

    @Autowired
    GalleryService galleryService;

    @PostMapping(value = "/add-gallery-image/{hotelId}", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('BUSINESS')")
//    @ApiOperation(value = "Create post", nickname = "createPostOperation")
//    @ApiResponses({
//            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
//            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
//            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
//            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity addNewImage(
            @ApiParam(value = "Added image to gallery.") @RequestPart("image") MultipartFile image,
            @ApiParam(value = "Hotel id.", required = true) @PathVariable("hotelId") int hotelId,
            HttpServletRequest request) throws IOException {
        return galleryService.addImageByHotelId(image, hotelId, request);
    }

    @GetMapping(value = "/get-gallery-image")
    @PreAuthorize("hasRole('USER') or hasRole('BUSINESS')")
//    @ApiOperation(value = "Get image by index", nickname = "getImageByIndex")
//    @ApiResponses({
//            @ApiResponse(code = SC_BAD_REQUEST, message = "Bad request", response = HttpExceptionResponse.class),
//            @ApiResponse(code = SC_UNAUTHORIZED, message = "Unauthorized", response = HttpExceptionResponse.class),
//            @ApiResponse(code = SC_NOT_FOUND, message = "Unauthorized", response = HttpExceptionResponse.class),
//            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Internal server error", response = HttpExceptionResponse.class)})
    public ResponseEntity<GalleryInforDto> getGalleryImageByIndex(
            @ApiParam(value = "Hotel id.") @RequestParam(name = "hotelId") int hotelId,
            @ApiParam(value = "Selected index.") @RequestParam("index") int index,
            HttpServletRequest request) {
        return galleryService.getImageByIndex(hotelId, index, request);
    }
}
