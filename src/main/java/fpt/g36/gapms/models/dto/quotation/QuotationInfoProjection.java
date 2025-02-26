package fpt.g36.gapms.models.dto.quotation;

import java.math.BigDecimal;
import java.util.Date;

public interface QuotationInfoProjection {
    Long getQuotationId();
    String getUserName();
    String getCompanyName();
    String getTaxNumber();
    String getProductName();
    Date getExpectedDate();
    Date getActualDate();
    String getReason();
    String getBrandName();
    String getCategoryName();
    boolean isHasColor();
    BigDecimal getPrice();
    String getNoteColor();
}
