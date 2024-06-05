package hello.hellospring.repository;

import hello.hellospring.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);
    Optional<Brand> findBrandById(Long brandId);
    Optional<Brand> findById(Long id);
    @Query("SELECT b FROM Brand b ORDER BY b.averageRating DESC")
    List<Brand> findAllOrderByAverageRatingDesc();
}