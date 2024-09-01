package com.example.postservice.repository;


import com.example.postservice.entity.Post;
//import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM user_post WHERE user_id = :userId AND post_id = :postId", nativeQuery = true)
    BigInteger checkLikeByUserIdAndPostId(@Param("userId") int userId, @Param("postId") int postId);

    @Query(value = "SELECT COUNT(*) FROM user_post WHERE post_id = :postId", nativeQuery = true)
    int countLikeByPostId(@Param("postId") int postId);

    @Query(value = "SELECT * FROM post WHERE hotel_id = :hotelId", nativeQuery = true)
    List<Post> findAllByHotelId(@Param("hotelId") int hotelId);

//    @Modifying
//    @Transactional
//    @Query(value = "DELETE FROM user_post WHERE post_id = :postId", nativeQuery = true)
//    void deleteUserPostsByPostId(@Param("postId") int postId);

}
