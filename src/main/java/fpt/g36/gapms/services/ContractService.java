package fpt.g36.gapms.services;


import fpt.g36.gapms.models.entities.Contract;

import java.util.Optional;

public interface ContractService{

    Optional<Contract> findById(String id);

    Boolean isContractExist(String id);
}
