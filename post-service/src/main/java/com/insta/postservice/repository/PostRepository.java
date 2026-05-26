package com.insta.postservice.repository;
import com.insta.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Post> findAllByOrderByCreatedAtDesc();
    @Query("SELECT p FROM Post p WHERE p.caption LIKE %:q%")
    List<Post> searchByCaption(@Param("q") String q);
    @Query("SELECT DISTINCT p FROM Post p JOIN p.hashtags h WHERE h LIKE %:tag%")
    List<Post> searchByHashtag(@Param("tag") String tag);
}
