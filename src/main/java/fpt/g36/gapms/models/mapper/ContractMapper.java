package fpt.g36.gapms.models.mapper;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.models.dto.contract.ContractDTO;
import fpt.g36.gapms.models.dto.contract.ContractUpdateDTO;
import fpt.g36.gapms.models.entities.Contract;
import fpt.g36.gapms.models.entities.PurchaseOrder;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.ContractRepository;
import fpt.g36.gapms.repositories.PurchaseOrderRepository;
import fpt.g36.gapms.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractMapper {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;

    public ContractMapper(PurchaseOrderRepository purchaseOrderRepository, UserRepository userRepository, ContractRepository contractRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
    }

    // Convert Contract to ContractDTO
    public ContractDTO toContractDTO(Contract contract) {
        if (contract == null) {
            return null;
        }

        ContractDTO dto = new ContractDTO();
        dto.setId(contract.getId());
        dto.setName(contract.getName());
        dto.setPath(contract.getPath());
        dto.setStatus(contract.getStatus());

        if (contract.getPurchaseOrder() != null) {
            dto.setPurchaseOrderId(contract.getPurchaseOrder().getId());
        }

        if (contract.getApprovedBy() != null) {
            dto.setApprovedByUserName(contract.getApprovedBy().getUsername());
        }

        if (contract.getCreateBy() != null) {
            dto.setCreateByUserName(contract.getCreateBy().getUsername());
        }

        return dto;
    }

    public Contract toContract(ContractDTO contractDTO, User currUser) {
        if (contractDTO == null) {
            return null;
        }

        Contract contract = new Contract();
        contract.setId(generateNewContractId());
        contract.setName(contractDTO.getName());
        contract.setStatus(BaseEnum.NOT_APPROVED);

        contract.setCreateBy(currUser);

        return contract;
    }

    public List<ContractDTO> toDTOList(List<Contract> contracts) {
        return contracts.stream()
                .map(this::toContractDTO)
                .collect(Collectors.toList());
    }

    public ContractUpdateDTO toContractUpdateDTO(ContractDTO contractDTO) {
        if (contractDTO == null) {
            return null;
        }

        ContractUpdateDTO dto = new ContractUpdateDTO();
        dto.setName(contractDTO.getName());

        return dto;
    }

    private String generateNewContractId() {
        // Lấy ID lớn nhất hiện có
        String maxId = contractRepository.findMaxContractId();

        // Sinh ID tiếp theo
        return createNextId(maxId);
    }

    private String createNextId(String currentMaxId) {

        if (currentMaxId == null || currentMaxId.isEmpty()) {
            return "HD0001";
        }

        // currentMaxId ví dụ: "HD0003"
        // Tách phần số ra: "0003"
        String numericPart = currentMaxId.substring(2); // Bỏ 'HD'

        // Chuyển sang int để +1
        int num = Integer.parseInt(numericPart);
        num++;

        // Format lại thành 4 chữ số: 4 -> "0004"
        String nextNumeric = String.format("%04d", num);

        // Ghép chuỗi
        return "HD" + nextNumeric;
    }
}
