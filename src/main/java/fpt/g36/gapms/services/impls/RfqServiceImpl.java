package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.enums.BaseEnum;
import fpt.g36.gapms.enums.NotificationEnum;
import fpt.g36.gapms.enums.SendEnum;
import fpt.g36.gapms.models.dto.notification.NotificationDTO;
import fpt.g36.gapms.models.entities.Rfq;
import fpt.g36.gapms.models.entities.Solution;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.RfqRepository;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.NotificationService;
import fpt.g36.gapms.services.RfqService;
import fpt.g36.gapms.utils.NotificationUtils;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RfqServiceImpl implements RfqService {
    @Autowired
    private RfqRepository rfqRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationUtils notificationUtils;

    @Override
    public Page<Rfq> getAllRfqsByUserId(Long userId, Pageable pageable) {
        return rfqRepository.getRfqByUserId(userId, pageable);
    }

    @Override
    public Rfq saveRfq(Rfq rfq) {
        return rfqRepository.save(rfq);
    }

    @Override
    public void deleteRfqById(Long rfqId) {
        rfqRepository.deleteById(rfqId);
    }

    @Transactional
    @Override
    public Rfq getRfqById(Long rfqId) {
        return rfqRepository.findById(rfqId).orElseThrow(() -> new RuntimeException("Không tìm thấy mã lô hàng"));
    }

    @Override
    public Rfq getRfqByIdAndUserId(Long rfqId, Long userId) {
        return rfqRepository.getRfqByRfqIdAndUserId(rfqId, userId).orElseThrow(() -> new RuntimeException("Không tìm thấy mã lô hàng"));
    }


    @Override
    public Rfq editRfq(Long rfqId, LocalDate newDate) {
        Rfq rfq = rfqRepository.findById(rfqId).orElse(null);
        if (rfq != null) {
            rfq.setExpectDeliveryDate(newDate);

            rfq.setUpdateAt(LocalDateTime.now());
            return rfqRepository.save(rfq);
        }
        return null;
    }

    @Override
    @Transactional
    public Rfq submitRfq(Long rfqId, Long userId, LocalDate deadlineSolution) {
        System.err.println("=== Bắt đầu xử lý submitRfq trong service ===");
        System.err.println("ID RFQ: " + rfqId + ", User ID: " + userId + ", Deadline Solution: " + deadlineSolution);

        // Tìm RFQ
        Rfq rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> {
                    System.err.println("Không tìm thấy RFQ với ID: " + rfqId);
                    return new RuntimeException("Rfq not found with id: " + rfqId);
                });
        System.err.println("RFQ tìm thấy: " + rfq.getId() + ", Trạng thái gửi: " + rfq.getIsApproved() + ", Ngày dự kiến giao hàng: " + rfq.getExpectDeliveryDate());

        // Tìm User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    System.err.println("Không tìm thấy User với ID: " + userId);
                    return new RuntimeException("User not found with id: " + userId);
                });
        System.err.println("User tìm thấy: " + user.getUsername() + " (ID: " + user.getId() + ")");

        // Kiểm tra nếu RFQ đã được gửi
        if (rfq.getIsApproved() == SendEnum.SENT) {
            System.err.println("RFQ đã được gửi trước đó. Trạng thái: " + rfq.getIsApproved());
            throw new RuntimeException("Rfq đã được gửi trước đó.");
        }

        // Kiểm tra deadlineSolution không được để trống
        if (deadlineSolution == null) {
            System.err.println("Deadline solution không được để trống.");
            throw new RuntimeException("Deadline solution không được để trống.");
        }

        LocalDate today = LocalDate.now();
        System.err.println("Ngày hiện tại: " + today);

        // Kiểm tra: Deadline không được là ngày trong quá khứ
        if (deadlineSolution.isBefore(today)) {
            System.err.println("Deadline solution không hợp lệ: " + deadlineSolution + " nhỏ hơn ngày hiện tại " + today);
            throw new RuntimeException("Deadline solution không được là ngày trong quá khứ.");
        }

        // Kiểm tra: Deadline không được vượt quá expectDeliveryDate
        LocalDate expectDeliveryDate = rfq.getExpectDeliveryDate();
        if (deadlineSolution.isAfter(expectDeliveryDate)) {
            System.err.println("Deadline solution không hợp lệ: " + deadlineSolution + " vượt quá ngày dự kiến giao hàng " + expectDeliveryDate);
            throw new RuntimeException("Deadline solution không được vượt quá ngày dự kiến giao hàng (" + expectDeliveryDate + ").");
        }

        // Kiểm tra: Deadline phải nằm trong khoảng hợp lệ (tối đa 2 ngày sau ngày hiện tại)
        LocalDate maxDeadline = today.plusDays(2);
        System.err.println("Kiểm tra deadlineSolution có nằm trong khoảng từ " + today + " đến " + maxDeadline);
        if (deadlineSolution.isAfter(maxDeadline)) {
            System.err.println("Deadline solution không hợp lệ: " + deadlineSolution + " vượt quá " + maxDeadline);
            throw new RuntimeException("Deadline solution không được vượt quá 2 ngày sau ngày hiện tại (" + maxDeadline + ").");
        }

        // Cập nhật RFQ
        System.err.println("Cập nhật RFQ: Đặt trạng thái SENT, cập nhật ApprovedBy và DeadlineSolution");
        rfq.setIsApproved(SendEnum.SENT);
        rfq.setUpdateAt(LocalDateTime.now());
        rfq.setApprovedBy(user);
        rfq.setIsSent(BaseEnum.APPROVED);
        rfq.setDeadlineSolution(deadlineSolution);

        // Lưu RFQ
        Rfq submittedRfq = rfqRepository.save(rfq);
        rfqRepository.flush();
        System.err.println("Lưu RFQ thành công: " + submittedRfq.getId() + ", Trạng thái gửi: " + submittedRfq.getIsApproved() + ", Deadline Solution: " + submittedRfq.getDeadlineSolution());

        // Gửi thông báo
        System.err.println("Gửi thông báo đến Technical cho RFQ ID: " + rfqId);
        notificationUtils.sendRfqApproveToTechnical(rfqId);

        // Refresh RFQ Details
        System.err.println("Refresh RFQ Details");
        if (submittedRfq.getRfqDetails() != null) {
            for (Object detail : submittedRfq.getRfqDetails()) {
                entityManager.refresh(detail);
            }
        }

        System.err.println("=== Kết thúc xử lý submitRfq trong service ===");
        return submittedRfq;
    }

    //
    @Override
    public Page<Rfq> getRfqsByStatus(BaseEnum status, Pageable pageable) {
        return rfqRepository.getRfqsByStatus(status, pageable);
    }

    @Override
    public Page<Rfq> getApprovedRfqsWithoutSolution(Pageable pageable) {
        return rfqRepository.getApprovedRfqsWithoutSolution(pageable);
    }

    @Override
    public Page<Rfq> getApprovedRfqsWithSolution(Pageable pageable) {
        return rfqRepository.getApprovedRfqsWithSolution(pageable);
    }

    @Override
    public Rfq getRfqByIdAndStatus(Long rfqId, BaseEnum status) {
        return rfqRepository.getRfqByIdAndStatus(rfqId, status)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy RFQ với ID: " + rfqId));
    }

    @Override
    public Rfq getApprovedRfqWithoutSolutionById(Long rfqId) {
        return rfqRepository.getApprovedRfqWithoutSolutionById(rfqId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy RFQ với ID: " + rfqId));
    }

    @Override
    public Rfq getApprovedRfqWithSolutionById(Long rfqId) {
        return rfqRepository.getApprovedRfqWithSolutionById(rfqId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy RFQ với ID: " + rfqId));
    }

}
