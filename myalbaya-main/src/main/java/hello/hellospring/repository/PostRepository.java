package hello.hellospring.repository;

import hello.hellospring.domain.EmploymentType;
import hello.hellospring.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 게시글 저장 메서드 추가
    Post save(Post post);

    List<Post> findByBrandId(Long id);

    List<Post> findByBrandName(String brandName);
    List<Post> findByBrandNameAndEmploymentType(String brandName, EmploymentType employmentType);
    List<Post> findByAuthorId(Long authorId);

    List<Post> findByEmploymentTypeIsNull();
    @Query("SELECT p FROM Post p WHERE p.employmentType = :employmentType ORDER BY p.likes DESC")
    List<Post> findPostsByEmploymentTypeOrderByLikesDesc(EmploymentType employmentType);


}



