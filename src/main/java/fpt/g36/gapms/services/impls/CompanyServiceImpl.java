package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.CompanyUser;
import fpt.g36.gapms.repositories.CompanyRepository;
import fpt.g36.gapms.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;


    @Override
    public Company getCompanyByUserId(Long userId) {
        return companyRepository.getCompanyByUserId(userId);
    }
}
