package com.example.postservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
// import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post {

    @Id
    @Column(name="post_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int postId;

    @Column
    private String caption;

    @Column(insertable = false)
    @CreationTimestamp
    private Timestamp time;

    @Lob
    @Column(name="image", columnDefinition="BLOB")
    private byte[] image;;

    @ManyToOne
    @JoinColumn(name = "hotel_id", referencedColumnName = "user_id")
    private User hotelId;

    @JsonIgnore
    @ManyToMany(mappedBy = "userPosts", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<User> postUsers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "postId", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();
}
