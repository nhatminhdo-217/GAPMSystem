package fpt.g36.gapms.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.util.Objects;

@Embeddable
public class CompanyUserId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 7030011296449569570L;
    @NotNull
    @Column(name = "company_id", nullable = false)
    private long companyId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompanyUserId entity = (CompanyUserId) o;
        return Objects.equals(this.companyId, entity.companyId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, userId);
    }

    public CompanyUserId() {
    }

    public CompanyUserId(long companyId, long userId) {
        this.companyId = companyId;
        this.userId = userId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}