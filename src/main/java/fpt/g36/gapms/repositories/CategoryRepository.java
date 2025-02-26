package fpt.g36.gapms.repositories;

import fpt.g36.gapms.models.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT DISTINCT c FROM Category c JOIN c.cateBrandPrices cbp WHERE cbp.brand.id = :brandId AND cbp.cate.id = c.id")
    List<Category> findByCateBrandPrices_Brand_Id(@Param("brandId") Long brandId);
}
