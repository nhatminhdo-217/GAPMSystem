package fpt.g36.gapms.models.dto;

import fpt.g36.gapms.models.entities.RfqDetail;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class RfqFormDTO {

    private LocalDate desiredDeliveryDate;

    private List<RfqDetail> rfqDetails;

    public RfqFormDTO() {
    }

    public RfqFormDTO(LocalDate desiredDeliveryDate, List<RfqDetail> rfqDetails) {
        this.desiredDeliveryDate = desiredDeliveryDate;
        this.rfqDetails = rfqDetails;
    }

    public LocalDate getDesiredDeliveryDate() {
        return desiredDeliveryDate;
    }

    public void setDesiredDeliveryDate(LocalDate desiredDeliveryDate) {
        this.desiredDeliveryDate = desiredDeliveryDate;
    }

    public List<RfqDetail> getRfqDetails() {
        return rfqDetails;
    }

    public void setRfqDetails(List<RfqDetail> rfqDetails) {
        this.rfqDetails = rfqDetails;
    }
}
