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

    @Override
    public Page<Rfq> getAllRfqsByUserId(Long userId, Pageable pageable) {
        return rfqRepository.getRfqByUserId(userId, pageable);
    }

    @Override
    public List<Rfq> getAllRfqsByUserId(Long userId) {
        return rfqRepository.getRfqByUserId(userId);
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
    public List<Rfq> getAllApprovedRfqs() {
        return rfqRepository.getAllApprovedRfqs(BaseEnum.APPROVED);
    }

    @Override
    public Rfq getRfqBySolutionId(Long solutionId) {
        return rfqRepository.findBySolution_Id(solutionId);
    }

    @Override
    public List<Rfq> getAllRfq() {
        return rfqRepository.findAll();
    }

    @Override
    public Rfq submitRfq(Long rfqId, Long userID) {
        Rfq rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("Rfq not found with id: " + rfqId));

        User user = userRepository.findById(userID).orElseThrow(()
                -> new RuntimeException("User not found with id: " + userID));

        // Kiểm tra nếu đã gửi thì không cần làm gì thêm
        if (rfq.getIsApproved() == SendEnum.SENT) {
            throw new RuntimeException("Rfq đã được gửi trước đó.");
        }

        // Cập nhật trạng thái thành SENT
        rfq.setIsApproved(SendEnum.SENT);
        rfq.setUpdateAt(LocalDateTime.now());
        rfq.setApprovedBy(user);
        rfq.setIsSent(BaseEnum.APPROVED);

        Rfq submittedRfq = rfqRepository.save(rfq);
        rfqRepository.flush();

        List<User> technicalUsers = userRepository.findAllByRole("TECHNICAL");
        for (User technicalUser : technicalUsers) {
            NotificationDTO notification = new NotificationDTO();
            notification.setMessage("Một RFQ mới (#" + rfqId + ") đã được chấp nhận và đang chờ xử lý.");
            notification.setType(NotificationEnum.SUCCESS);
            notification.setTargetUrl("/technical/rfq-details/" + rfqId);
            notification.setTargetUserId(technicalUser.getId());
            notification.setSource("Quản lý RFQ");
            notification.setTimestamp(LocalDateTime.now());

            notificationService.saveAndSendNotification(notification);
        }

        entityManager.refresh(submittedRfq.getRfqDetails());


        return submittedRfq;
    }
}
