package com.example.postservice.dto;

import com.example.postservice.entity.Post;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int userId;

    private String name;

    private String email;

    private String password;

    private String address;

    private String state;

    private String country;

    private String role;

    private byte[] image;

    private Set<PostDto> userPosts = new HashSet<>();

}
