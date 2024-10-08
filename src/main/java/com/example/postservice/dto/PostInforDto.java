package com.example.postservice.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="PostInforDto")
public class PostInforDto implements Serializable {

    @ApiModelProperty(value = "Post object for Post.")
    private PostDetailsDto postDetailsDto;

    @ApiModelProperty(value = "Hotel object for User.")
    private HotelDetailsDto hotelDetailsDto;

    @ApiModelProperty(value = "Hotel object for User.")
    private LikeDetailsDto likeDetailsDto;
}
