package fpt.g36.gapms.models.dto.quotation;

import fpt.g36.gapms.enums.BaseEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public interface QuotationInfoProjection {
    Long quotationId();
    String userName();
    String companyName();
    String taxNumber();
    String companyAddress();
    BaseEnum isAccepted();
    Long solutionId();
    String productName();
    LocalDate expectedDate();
    LocalDate actualDate();
    String brandName();
    String categoryName();
    BigDecimal price();
    String noteColor();
    Integer quantity();
}
