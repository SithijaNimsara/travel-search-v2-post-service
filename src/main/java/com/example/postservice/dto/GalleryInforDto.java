package com.example.postservice.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="GalleryInforDto")
public class GalleryInforDto {

    private int galleryId;

    private byte[] image;

    private int hotelId;

    private int currentPage;

    private int totalItem;

}
