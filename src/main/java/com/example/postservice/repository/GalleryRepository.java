package com.example.postservice.repository;


import com.example.postservice.entity.Gallery;
import com.example.postservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Integer> {
    Page<Gallery> findByUserId(User userId, Pageable pageable);
}
