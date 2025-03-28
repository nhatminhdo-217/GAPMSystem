package fpt.g36.gapms.repositories;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.entities.ProductionOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {
    Page<ProductionOrder> findByStatus(BaseEnum status, Pageable pageable);

}
