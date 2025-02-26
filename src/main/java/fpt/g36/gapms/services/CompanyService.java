package fpt.g36.gapms.services;


import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.CompanyUser;

public interface CompanyService {

    Company getCompanyByUserId(Long userId);
}
