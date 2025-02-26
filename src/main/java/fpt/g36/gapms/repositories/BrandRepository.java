package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("select b from Brand b where b.production.id = :productId")
    List<Brand> findByProductId(Long productId);
}
