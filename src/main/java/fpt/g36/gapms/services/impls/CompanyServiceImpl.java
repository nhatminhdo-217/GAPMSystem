package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.CompanyDTO;
import fpt.g36.gapms.models.entities.Company;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.CompanyRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository;
    private UserRepository userRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Optional<Company> findByUserId(Long userId) {
        return companyRepository.findCompanyByUserId(userId);
    }

    @Transactional
    public Company addCompany(Long userId, CompanyDTO companyDTO) {
        System.err.println("Starting addCompany for userId: " + userId);
        System.err.println("CompanyDTO: " + companyDTO.getName() + ", " + companyDTO.getEmail() + ", " +
                companyDTO.getPhoneNumber() + ", " + companyDTO.getAddress() + ", " + companyDTO.getTaxNumber());

        User user;
        try {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            System.err.println("User found: " + user.getId());
        } catch (Exception e) {
            System.err.println("Error finding user: " + e.getClass().getName() + " - " + e.getMessage());
            throw e; // Ném lại để controller bắt
        }

        Company company = new Company();
        company.setName(companyDTO.getName());
        company.setEmail(companyDTO.getEmail());
        company.setPhoneNumber(companyDTO.getPhoneNumber());
        company.setAddress(companyDTO.getAddress());
        company.setTaxNumber(companyDTO.getTaxNumber());
        company.getUsers().add(user);
        System.err.println("Company created with users: " + company.getUsers().size());

        Company savedCompany;
        try {
            savedCompany = companyRepository.save(company);
            System.err.println("Company saved with ID: " + savedCompany.getId());
        } catch (Exception e) {
            System.err.println("Error saving company: " + e.getClass().getName() + " - " + e.getMessage());
            throw e;
        }
        return savedCompany;
    }

    @Transactional
    public Company updateCompany(Long userId, CompanyDTO companyDTO) {
        System.err.println("Starting updateCompany for userId: " + userId);
        System.err.println("CompanyDTO: " + companyDTO);

        // Tìm Company của User
        Company company = companyRepository.findCompanyByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No company found for this user."));
        System.err.println("Company found: " + company.getId());

        // Cập nhật thông tin công ty
        company.setName(companyDTO.getName());
        company.setEmail(companyDTO.getEmail());
        company.setPhoneNumber(companyDTO.getPhoneNumber());
        company.setAddress(companyDTO.getAddress());
        company.setTaxNumber(companyDTO.getTaxNumber());
        System.err.println("Company updated but not saved yet.");

        // Lưu lại thay đổi
        Company updatedCompany;
        try {
            updatedCompany = companyRepository.save(company);
            System.err.println("Company updated and saved with ID: " + updatedCompany.getId());
        } catch (Exception e) {
            System.err.println("Error saving updated company: " + e.getClass().getName() + " - " + e.getMessage());
            throw e;
        }

        return updatedCompany;
    }

    @Override
    public Company getCompanyByUserId(Long userId) {
        return companyRepository.getCompanyByUserId(userId);
    }
}
