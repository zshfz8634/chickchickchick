package hello.hellospring.controller;

import hello.hellospring.domain.Allow;
import hello.hellospring.domain.Brand;
import hello.hellospring.domain.Member;
import hello.hellospring.service.AllowService;
import hello.hellospring.service.BrandService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/allow")
public class AllowController {

    private final AllowService allowService;
    private final BrandService brandService;


    @Autowired
    public AllowController(AllowService allowService,BrandService brandService) {
        this.allowService = allowService;
        this.brandService=brandService;
    }

    @PostMapping("/new")
    public ResponseEntity<String> createAllow(@RequestParam("title") String title,
                                              @RequestParam("content") String content,
                                              @RequestParam("brandId") Long brandId,
                                              @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                              HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Optional<Brand> brandOptional = brandService.findById(brandId);
        if (!brandOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 브랜드 ID입니다.");
        }

        Allow allow = new Allow();
        allow.setTitle(title);
        allow.setContent(content);
        allow.setBrand(brandOptional.get());
        allow.setMember(loggedInMember);

        try {
            allowService.createAllow(allow, images);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 저장에 실패했습니다.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("근로계약서 인증 게시글이 성공적으로 생성되었습니다.");
    }

    @GetMapping
    public ResponseEntity<?> getAllAllows(HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null || !loggedInMember.getEmploymentType().equals("MASTER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 권한이 필요합니다.");
        }
        List<Allow> allows = allowService.getAllAllows();
        return ResponseEntity.ok(allows);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAllowById(@PathVariable Long id, HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null || !loggedInMember.getEmploymentType().equals("MASTER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 권한이 필요합니다.");
        }
        Optional<Allow> allow = allowService.getAllowById(id);
        return allow.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAllow(@PathVariable Long id, HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null || !loggedInMember.getEmploymentType().equals("MASTER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 권한이 필요합니다.");
        }
        allowService.deleteAllow(id);
        return ResponseEntity.ok("근로계약서 인증 게시글이 삭제되었습니다.");
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveAllow(@RequestBody Map<String, Long> request, HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null || !loggedInMember.getEmploymentType().equals("MASTER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 권한이 필요합니다.");
        }
        Long allowId = request.get("allowId");

        allowService.approveAllow(allowId);
        return ResponseEntity.ok("회원이 인증되었습니다.");
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectAllow(@RequestBody Map<String, Long> request, HttpSession session) {
        Member loggedInMember = (Member) session.getAttribute("loggedInMember");
        if (loggedInMember == null || !loggedInMember.getEmploymentType().equals("MASTER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("관리자 권한이 필요합니다.");
        }
        Long allowId = request.get("allowId");

        allowService.rejectAllow(allowId);
        return ResponseEntity.ok("근로계약서 인증 요청이 거부되었습니다.");
    }
}

