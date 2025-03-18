package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.dto.contract.ContractDTO;
import fpt.g36.gapms.models.dto.contract.ContractUpdateDTO;
import fpt.g36.gapms.models.entities.Contract;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.models.mapper.ContractMapper;
import fpt.g36.gapms.repositories.ContractRepository;
import fpt.g36.gapms.services.ContractService;
import fpt.g36.gapms.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final UserUtils userUtils;

    public ContractServiceImpl(ContractRepository contractRepository, ContractMapper contractMapper, UserUtils userUtils) {
        this.contractRepository = contractRepository;
        this.contractMapper = contractMapper;
        this.userUtils = userUtils;
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

    @Transactional
    @Override
    public Contract createContract(Long purchaseOrderId, ContractDTO contractDTO, MultipartFile file) throws IOException {
        User currUser = userUtils.getOptionalUserInfo();

        Contract contract = contractMapper.toContract(purchaseOrderId, contractDTO, currUser);

        String uploadedFileName = saveImageMultiFile(file);

        if (uploadedFileName != null) {
            contract.setPath(uploadedFileName);
        }

        return contractRepository.save(contract);
    }

    @Transactional
    @Override
    public Contract updateContract(String id, ContractDTO contractDTO, MultipartFile file) throws IOException {

        Contract contract = findById(id).get();

        ContractUpdateDTO contractUpdateDTO = contractMapper.toContractUpdateDTO(contractDTO);

        contract.setName(contractUpdateDTO.getName());

        String uploadedFileName = saveImageMultiFile(file);

        if (uploadedFileName != null) {
            contract.setPath(uploadedFileName);
        }

        return contractRepository.save(contract);
    }

    private String saveImageMultiFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String fileNameUnique = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

        Path pathDir = Paths.get("uploads/contracts");
        if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }

        Path destination = pathDir.resolve(fileNameUnique);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileNameUnique;
    }
}
