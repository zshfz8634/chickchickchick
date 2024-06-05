package hello.hellospring.controller;

import hello.hellospring.domain.EmploymentType;
import hello.hellospring.domain.Member;
import hello.hellospring.domain.HirePost;
import hello.hellospring.service.HirePostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hire")
public class HirePostController {

    private final HirePostService hirePostService;

    public HirePostController(HirePostService hirePostService) {
        this.hirePostService = hirePostService;
    }

    @PostMapping("/new")
    public ResponseEntity<String> createPost(@RequestBody HirePostForm form, HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (loggedInMember.getEmploymentType() != EmploymentType.BOSS) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("자영업자만 게시글을 작성할 수 있습니다.");
        }

        hirePostService.createPost(form.getTitle(), form.getContent(), form.getHourlyWage(), loggedInMember);
        return ResponseEntity.status(HttpStatus.CREATED).body("채용 게시글이 성공적으로 작성되었습니다.");
    }
    @GetMapping("/{id}")
    public ResponseEntity<HirePost> showHirePost(@PathVariable("id") Long postId) {
        return hirePostService.getPostById(postId)
                .map(post -> {
                    hirePostService.increaseViewCount(postId);  // 조회수 증가
                    post.getComments().size(); // 강제로 댓글을 로드
                    return ResponseEntity.ok(post);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<HirePost>> getAllPosts() {
        List<HirePost> posts = hirePostService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable("id") Long postId, HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            hirePostService.deletePost(postId, loggedInMember.getId(), loggedInMember.getEmploymentType());
            return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
