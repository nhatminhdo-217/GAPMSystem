package fpt.g36.gapms.services;


import fpt.g36.gapms.models.dto.contract.ContractDTO;
import fpt.g36.gapms.models.dto.contract.ContractUpdateDTO;
import fpt.g36.gapms.models.entities.Contract;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface ContractService{

    Optional<Contract> findById(String id);

    Boolean isContractExist(String id);

    Contract createContract(Long purchaseOrderId, ContractDTO contractDTO, MultipartFile file) throws IOException;

    Contract updateContract(String id, ContractDTO contractDTO, MultipartFile file) throws IOException;
}
