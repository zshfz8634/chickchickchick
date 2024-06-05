package hello.hellospring.controller;

import hello.hellospring.domain.Brand;
import hello.hellospring.domain.EmploymentType;
import hello.hellospring.domain.Member;
import hello.hellospring.domain.Post;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.PostRepository;
import hello.hellospring.service.BrandService;
import hello.hellospring.service.LikeService;
import hello.hellospring.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
public class PostController {

    private final PostService postService;
    private final BrandService brandService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final LikeService likeService;

    @Autowired
    public PostController(PostService postService, BrandService brandService,
                          MemberRepository memberRepository, PostRepository postRepository,
                          LikeService likeService) {
        this.postService = postService;
        this.brandService = brandService;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.likeService = likeService;
    }

    @PostMapping("/brands/{brandName}/posts/employee/new")
    public ResponseEntity<String> createEmployeePost(@PathVariable("brandName") String brandName,
                                                     @RequestParam("title") String title,
                                                     @RequestParam("content") String content,
                                                     @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                     HttpSession session) throws IOException {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (loggedInMember.getEmploymentType() != EmploymentType.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("아르바이트생 회원만 게시글을 작성할 수 있습니다.");
        }

        Brand brand = brandService.findBrandByName(brandName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand name: " + brandName));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(loggedInMember);
        post.setBrand(brand);
        post.setEmploymentType(loggedInMember.getEmploymentType());
        post.setLikeCount(0);
        post.setViewCount(0);

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = postService.saveImages(images);
            post.setImageUrls(imageUrls);
        } else {
            post.setImageUrls(Collections.emptyList());
        }

        postService.createPost(post);

        return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 성공적으로 작성되었습니다.");
    }

    @PostMapping("/brands/{brandName}/posts/boss/new")
    public ResponseEntity<String> createBossPost(@PathVariable("brandName") String brandName,
                                                 @RequestParam("title") String title,
                                                 @RequestParam("content") String content,
                                                 @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                 HttpSession session) throws IOException {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (loggedInMember.getEmploymentType() != EmploymentType.BOSS) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("자영업자 회원만 게시글을 작성할 수 있습니다.");
        }

        Brand brand = brandService.findBrandByName(brandName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand name: " + brandName));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(loggedInMember);
        post.setBrand(brand);
        post.setEmploymentType(loggedInMember.getEmploymentType());
        post.setLikeCount(0);
        post.setViewCount(0);

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = postService.saveImages(images);
            post.setImageUrls(imageUrls);
        } else {
            post.setImageUrls(Collections.emptyList());
        }

        postService.createPost(post);

        return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 성공적으로 작성되었습니다.");
    }

    @PostMapping("/posts/new")
    public ResponseEntity<String> createPost(@RequestParam("title") String title,
                                             @RequestParam("content") String content,
                                             @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                             HttpSession session) throws IOException {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(loggedInMember);
        post.setEmploymentType(null); // 통합 게시판에서는 EmploymentType을 사용하지 않음
        post.setLikeCount(0);
        post.setViewCount(0);

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = postService.saveImages(images);
            post.setImageUrls(imageUrls);
        } else {
            post.setImageUrls(Collections.emptyList());
        }

        postService.createPost(post);

        return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 성공적으로 작성되었습니다.");
    }

    @GetMapping("/brands/{name}/posts/new")
    public ResponseEntity<String> showCreatePostFormForBrand(@PathVariable("name") String name) {
        return ResponseEntity.ok("게시글 작성 폼을 보여줍니다.");
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getPostsForUnifiedBoard();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/brands/{name}/posts/employee")
    public ResponseEntity<List<Post>> listPostsByBrandAndEmployee(@PathVariable("name") String name) {
        List<Post> posts = postService.findPostsByBrandNameAndEmploymentType(name, EmploymentType.EMPLOYEE);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/brands/{name}/posts/boss")
    public ResponseEntity<List<Post>> listPostsByBrandAndBoss(@PathVariable("name") String name) {
        List<Post> posts = postService.findPostsByBrandNameAndEmploymentType(name, EmploymentType.BOSS);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> showPost(@PathVariable("id") Long postId) {
        return postService.getPostById(postId)
                .map(post -> {
                    postService.increaseViewCount(postId);
                    post.getComments().size();

                    return ResponseEntity.ok(post);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable("id") Long postId, HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            if (post.getAuthor().getId().equals(loggedInMember.getId()) ||
                    loggedInMember.getEmploymentType() == EmploymentType.MASTER) {
                postRepository.deleteById(postId);
                return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("게시글 작성자만 삭제할 수 있습니다.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 게시물을 찾을 수 없습니다.");
        }
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<String> updatePost(@PathVariable("id") Long postId,
                                             HttpSession session,
                                             @RequestParam("title") String title,
                                             @RequestParam("content") String content,
                                             @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            if (post.getAuthor().getId().equals(loggedInMember.getId())) {
                if (title != null) {
                    post.setTitle(title);
                }
                if (content != null) {
                    post.setContent(content);
                }

                // 기존 이미지 삭제
                List<String> existingImageUrls = post.getImageUrls();
                postService.deleteImages(existingImageUrls);

                // 새로운 이미지 저장
                if (images != null && !images.isEmpty()) {
                    List<String> newImageUrls = postService.saveImages(images);
                    post.setImageUrls(newImageUrls);
                } else {
                    post.setImageUrls(Collections.emptyList());
                }

                postRepository.save(post);
                return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("게시글 작성자만 수정할 수 있습니다.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 게시물을 찾을 수 없습니다.");
        }

    }
        @PostMapping("/posts/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable("postId") Long postId, HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        likeService.likePost(postId, loggedInMember);
        return ResponseEntity.ok("Liked");
    }
    @GetMapping("/posts/hot/employee")
    public ResponseEntity<List<Post>> listPostsByLikeAndEmployee() {
        List<Post> posts = postService.findPostsByEmploymentTypeOrderByLikesDesc(EmploymentType.EMPLOYEE);
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/posts/hot/boss")
    public ResponseEntity<List<Post>> listPostsByLikeAndBoss() {
        List<Post> posts = postService.findPostsByEmploymentTypeOrderByLikesDesc(EmploymentType.BOSS);
        return ResponseEntity.ok(posts);
    }
}
