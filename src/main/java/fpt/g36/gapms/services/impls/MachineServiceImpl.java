package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.models.entities.DyeMachine;
import fpt.g36.gapms.models.entities.WindingMachine;
import fpt.g36.gapms.repositories.DyeMachineRepository;
import fpt.g36.gapms.repositories.WindingMachineRepository;
import fpt.g36.gapms.services.MachineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MachineServiceImpl implements MachineService {

    private final DyeMachineRepository dyeMachineRepository;
    private final WindingMachineRepository windingMachineRepository;

    public MachineServiceImpl(DyeMachineRepository dyeMachineRepository, WindingMachineRepository windingMachineRepository) {
        this.dyeMachineRepository = dyeMachineRepository;
        this.windingMachineRepository = windingMachineRepository;
    }

    @Override
    public Page<DyeMachine> getAllDyeMachines(Pageable pageable) {
        return dyeMachineRepository.findAllByOrderByCreateAtDesc(pageable);
    }

    @Override
    public DyeMachine getDyeMachinesById(Long id) {
        return dyeMachineRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Không tìm thấy máy nhuộm với ID: " + id));
    }

    @Override
    public Page<WindingMachine> getAllWindingMachines(Pageable pageable) {
        return windingMachineRepository.findAllByOrderByCreateAtDesc(pageable);
    }

    @Override
    public WindingMachine getWindingMachinesById(Long id) {
        return windingMachineRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Không tìm thấy máy cuốn với ID: " + id));
    }

    @Override
    public DyeMachine addDyeMachine(DyeMachine dyeMachine) {
        // Thiết lập đầy đủ các thuộc tính
        DyeMachine newDyeMachine = new DyeMachine();
        newDyeMachine.setDiameter(dyeMachine.getDiameter());
        newDyeMachine.setPile(dyeMachine.getPile());
        newDyeMachine.setConePerPile(dyeMachine.getConePerPile());
        newDyeMachine.setMaxWeight(dyeMachine.getMaxWeight());
        newDyeMachine.setCapacity(dyeMachine.getCapacity());
        newDyeMachine.setDescription(dyeMachine.getDescription() != null ? dyeMachine.getDescription() : "");
        newDyeMachine.setCreateAt(LocalDateTime.now());
        newDyeMachine.setUpdateAt(LocalDateTime.now());
        newDyeMachine.setDyeStage(null); // Chưa gán DyeStage

        // Lưu và trả về đối tượng đã được lưu
        return dyeMachineRepository.save(newDyeMachine);
    }

    @Override
    public WindingMachine addWindingMachine(WindingMachine windingMachine) {
        // Thiết lập đầy đủ các thuộc tính
        WindingMachine newWindingMachine = new WindingMachine();
        //
        newWindingMachine.setMotor_speed(windingMachine.getMotor_speed());
        newWindingMachine.setSpindle(windingMachine.getSpindle());
        newWindingMachine.setCapacity(windingMachine.getCapacity());
        newWindingMachine.setDescription(windingMachine.getDescription() != null ? windingMachine.getDescription() : ""); // Mô tả có thể null
        newWindingMachine.setCreateAt(LocalDateTime.now());
        newWindingMachine.setUpdateAt(LocalDateTime.now());
        newWindingMachine.setWindingStage(null); // Chưa gán WindingStage
        // Lưu và trả về đối tượng đã được lưu
        System.err.println(newWindingMachine);
        return windingMachineRepository.save(newWindingMachine);
    }

    public Page<DyeMachine> getDyeMachinesWithNoStage(Pageable pageable) {
        return dyeMachineRepository.findByDyeStageIsNull(pageable);
    }

    public Page<WindingMachine> getWindingMachinesWithNoStage(Pageable pageable) {
        return windingMachineRepository.findByWindingStageIsNull(pageable);
    }

    @Override
    public DyeMachine updateDyeMachine(Long id, DyeMachine dyeMachine) {
        DyeMachine existingMachine = dyeMachineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy máy nhuộm với ID: " + id));

        // Kiểm tra điều kiện: máy chưa được gán vào stage
        if (existingMachine.getDyeStage() != null) {
            throw new IllegalStateException("Không thể cập nhật máy đã được gán vào stage.");
        }

        // Cập nhật các thuộc tính
        existingMachine.setDiameter(dyeMachine.getDiameter());
        existingMachine.setPile(dyeMachine.getPile());
        existingMachine.setConePerPile(dyeMachine.getConePerPile());
        existingMachine.setMaxWeight(dyeMachine.getMaxWeight());
        existingMachine.setCapacity(dyeMachine.getCapacity());
        existingMachine.setDescription(dyeMachine.getDescription() != null ? dyeMachine.getDescription() : "");
        existingMachine.setUpdateAt(LocalDateTime.now());

        return dyeMachineRepository.save(existingMachine);
    }

    @Override
    public WindingMachine updateWindingMachine(Long id, WindingMachine windingMachine) {
        WindingMachine existingMachine = windingMachineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy máy cuốn với ID: " + id));

        // Kiểm tra điều kiện: máy chưa được gán vào stage
        if (existingMachine.getWindingStage() != null) {
            throw new IllegalStateException("Không thể cập nhật máy đã được gán vào stage.");
        }

        // Cập nhật các thuộc tính
        existingMachine.setMotor_speed(windingMachine.getMotor_speed());
        existingMachine.setSpindle(windingMachine.getSpindle());
        existingMachine.setCapacity(windingMachine.getCapacity());
        existingMachine.setDescription(windingMachine.getDescription() != null ? windingMachine.getDescription() : "");
        existingMachine.setUpdateAt(LocalDateTime.now());

        return windingMachineRepository.save(existingMachine);
    }
}