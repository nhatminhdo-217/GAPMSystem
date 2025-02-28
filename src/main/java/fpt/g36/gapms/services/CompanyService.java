package fpt.g36.gapms.services;

import fpt.g36.gapms.models.dto.CompanyDTO;
import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.CompanyUser;

import java.util.Optional;

public interface CompanyService {
    Optional<Company> findByUserId(Long userId);

    Company addCompany(Long userId, CompanyDTO companyDTO);

    Company updateCompany(Long userId, CompanyDTO companyDTO);

    Company getCompanyByUserId(Long userId);
}
