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
@ApiModel(value="LikeDetailsDto")
public class LikeDetailsDto {

    private int likeCount;

    private boolean liked;
}
