package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "thread")
public class Thread extends BaseEntity{

    @NotNull
    @Column(name = "name", length = 100)
    private String name;

    @Lob
    @Column(name = "description", length = 255)
    private String description;

    @NotNull
    @Column(name = "convert_rate", precision = 10, scale = 4)
    private BigDecimal convert_rate;

    @NotNull
    @OneToOne
    @JoinColumn(name = "process_id", nullable = false)
    private WindingProcess process;

    @OneToMany(mappedBy = "thread")
    private List<Product> products;

    public Thread() {
    }

    public Thread(Long id, LocalDateTime createAt, LocalDateTime updateAt, String name, String description, BigDecimal convert_rate, WindingProcess process, List<Product> products) {
        super(id, createAt, updateAt);
        this.name = name;
        this.description = description;
        this.convert_rate = convert_rate;
        this.process = process;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getConvert_rate() {
        return convert_rate;
    }

    public void setConvert_rate(BigDecimal convert_rate) {
        this.convert_rate = convert_rate;
    }

    public WindingProcess getProcess() {
        return process;
    }

    public void setProcess(WindingProcess process) {
        this.process = process;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
