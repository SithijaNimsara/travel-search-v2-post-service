package com.example.postservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDto {
    @ApiModelProperty(value = "error status code if available.", required=false, example= "300")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status;

    @ApiModelProperty(value = "error message if available.", required=false, example= "Internal Server Error. Please contact Support.")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

}
