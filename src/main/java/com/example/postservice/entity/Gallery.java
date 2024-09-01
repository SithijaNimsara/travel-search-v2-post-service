package com.example.postservice.entity;

import javax.persistence.*;
// import jakarta.persistence.*;
import lombok.*;


import java.util.Arrays;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Gallery {

    @Id
    @Column(name="gallery_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int galleryId;

    @Lob
    @Column(columnDefinition="LONGBLOB")
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User userId;

    @Override
    public String toString() {
        return "Gallery{" +
                "galleryId=" + galleryId +
                ", image=" + Arrays.toString(image) +
                ", userId=" + userId +
                '}';
    }
}
