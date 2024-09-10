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


@RestController()
@RequestMapping("/gallery")
public class GalleryController {

    @Autowired
    GalleryService galleryService;

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<Void> addNewImage(@RequestPart("image") MultipartFile image,
                                            @RequestParam(value = "hotelId", required = true) int hotelId,
                                            HttpServletRequest request) throws IOException {
        return galleryService.addImageByHotelId(image, hotelId, request);
    }

    @GetMapping()
    @PreAuthorize("hasRole('USER') or hasRole('BUSINESS')")
    public ResponseEntity<GalleryInforDto> getGalleryImageByIndex(@RequestParam(name = "hotelId") int hotelId,
                                                                  @RequestParam("index") int index,
                                                                  HttpServletRequest request) {
        return galleryService.getImageByIndex(hotelId, index, request);
    }
}
