package com.example.postservice.error;



import com.example.postservice.dto.ErrorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class HttpExceptionResponse {
    public final List<ErrorDto> errors;
}
