package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.Contract;
import fpt.g36.gapms.repositories.ContractRepository;
import fpt.g36.gapms.services.ContractService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    public ContractServiceImpl(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @Override
    public Optional<Contract> findById(String id) {

        if (isContractExist(id)) {
            return contractRepository.findById(id);
        }
        return Optional.empty();
    }

    @Override
    public Boolean isContractExist(String id) {
        return contractRepository.existsById(id);
    }
}
