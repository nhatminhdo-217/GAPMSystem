package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "production_order_detail")
public class ProductionOrderDetail extends BaseEntity {

    @Column(name = "thread_mass", precision = 10, scale = 4)
    private BigDecimal thread_mass;

    private Boolean light_env; // 0: ánh đèn (AD), 1: ánh sáng tự nhiên (TN)

    @Lob
    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "production_order_id", nullable = false)
    private ProductionOrder productionOrder;


}
