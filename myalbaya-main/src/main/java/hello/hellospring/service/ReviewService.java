package hello.hellospring.service;

import hello.hellospring.domain.Brand;
import hello.hellospring.domain.Member;
import hello.hellospring.domain.Review;
import hello.hellospring.repository.BrandRepository;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BrandRepository brandRepository;
    private final MemberRepository memberRepository;

    public ReviewService(ReviewRepository reviewRepository, BrandRepository brandRepository, MemberRepository memberRepository) {
        this.reviewRepository = reviewRepository;
        this.brandRepository = brandRepository;
        this.memberRepository = memberRepository;
    }

    public Review saveReview(Long brandId, Long authorId, String content, double rating) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid brand ID"));
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid author ID"));


        Review review = new Review();
        review.setBrand(brand);
        review.setAuthor(author);
        review.setContent(content);
        review.setRating(rating);
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        // 브랜드의 평균 평점 업데이트
        updateBrandRating(brand);

        return savedReview;
    }

    private void updateBrandRating(Brand brand) {
        List<Review> reviews = reviewRepository.findByBrandId(brand.getId());
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        // 평균 평점을 소수점 이하 한 자리로 반올림
        BigDecimal roundedRating = BigDecimal.valueOf(averageRating)
                .setScale(1, RoundingMode.HALF_UP);
        brand.setAverageRating(roundedRating.doubleValue());

        brandRepository.save(brand);
    }

    public List<Review> findReviewsByBrandId(Long brandId) {
        return reviewRepository.findByBrandId(brandId);
    }
}