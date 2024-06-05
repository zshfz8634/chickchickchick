package hello.hellospring.service;

import hello.hellospring.domain.EmploymentType;
import hello.hellospring.domain.Member;
import hello.hellospring.domain.HirePost;
import hello.hellospring.repository.HirePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HirePostService {

    private final HirePostRepository hirePostRepository;

    @Autowired
    public HirePostService(HirePostRepository hirePostRepository) {
        this.hirePostRepository = hirePostRepository;
    }

    public HirePost createPost(String title, String content, double salary, Member author) {
        HirePost post = new HirePost();
        post.setTitle(title);
        post.setContent(content);
        post.setSalary(salary);
        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());

        return hirePostRepository.save(post);
    }

    public List<HirePost> getAllPosts() {
        return hirePostRepository.findAll();
    }

    public Optional<HirePost> getPostById(Long postId) {
        return hirePostRepository.findById(postId);
    }

    public void deletePost(Long postId, Long authorId,EmploymentType employmentType) {
        HirePost post = getPostById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        if (!post.getAuthor().getId().equals(authorId)|| employmentType == EmploymentType.MASTER) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        hirePostRepository.delete(post);
    }


    public void increaseViewCount(Long postId) {
        hirePostRepository.findById(postId).ifPresent(post -> {
            post.setViewCount(post.getViewCount() + 1);
            hirePostRepository.save(post);
        });
    }

}
