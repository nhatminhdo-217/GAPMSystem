package fpt.g36.gapms.models.dto.quotation;

import java.math.BigDecimal;
import java.util.Date;

public interface QuotationInforCustomerProjection {
    Long getQuotationId();
    Boolean getIsCanceled();
    String getIsAccepted();
    Long getRfqId();
    int getQuantity();
    String getUserName();
    String getCompanyName();
    String getTaxNumber();
    String getProductName();
    Date getExpectedDate();
    Date getActualDate();
    String getReason();
    String getBrandName();
    String getCategoryName();
    Boolean getIsColor();
    BigDecimal getPrice();
    String getNoteColor();
}
