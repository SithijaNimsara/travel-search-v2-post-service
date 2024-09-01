package com.example.postservice.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="PostDetailsDto")
public class PostDetailsDto {

    @ApiModelProperty(value = "Post's id", dataType = "int")
    private int postId;

    @ApiModelProperty(value = "Post's caption", dataType = "String")
    private String caption;

    @ApiModelProperty(value = "Post's date")
    private Timestamp time;

    @ApiModelProperty(value = "Post's image")
    private byte[] postImage;


}
