package fpt.g36.gapms.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_user")
public class CompanyUser {
    @EmbeddedId
    private CompanyUserId id;

    @MapsId("companyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public CompanyUser() {
    }

    public CompanyUser(CompanyUserId id, Company company, User user) {
        this.id = id;
        this.company = company;
        this.user = user;
    }

    public CompanyUserId getId() {
        return id;
    }

    public void setId(CompanyUserId id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}