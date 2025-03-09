package fpt.g36.gapms.models.dto.quotation;

import fpt.g36.gapms.enums.BaseEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public interface QuotationInfoProjection {
    Long getQuotationId();
    String getUserName();
    String getCompanyName();
    String getTaxNumber();
    String getCompanyAddress();
    BaseEnum getIsAccepted();
    Long getSolutionId();
    String getProductName();
    LocalDate getExpectedDate();
    LocalDate getActualDate();
    String getBrandName();
    String getCategoryName();
    BigDecimal getPrice();
    String getNoteColor();
    Integer getQuantity();
}
