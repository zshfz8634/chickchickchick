package hello.hellospring.service;

import hello.hellospring.domain.Brand;
import hello.hellospring.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BrandService {
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Optional<Brand> findBrandByName(String name) {
        return brandRepository.findByName(name);
    }

    public Optional<Long> findBrandIdByName(String name) {
        return brandRepository.findByName(name).map(Brand::getId);
    }

    // 새로운 메서드 추가
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
    public List<Brand> getBrandsOrderByAverageRating() {
        return brandRepository.findAllOrderByAverageRatingDesc();
    }
    public Optional<Brand> findById(Long id) {
        return brandRepository.findById(id);
    }
}