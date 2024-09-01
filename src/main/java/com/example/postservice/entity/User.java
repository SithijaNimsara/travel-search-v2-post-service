package com.example.postservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
//import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String address;

    @Column
    private String state;

    @Column
    private String country;

    @Column
    private String role;

    @Lob
    @Column(name="image", columnDefinition="BLOB")
    private byte[] image;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_post",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    private Set<Post> userPosts = new HashSet<>();

}
