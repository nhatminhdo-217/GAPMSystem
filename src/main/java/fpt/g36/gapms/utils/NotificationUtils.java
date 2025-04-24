package fpt.g36.gapms.utils;

import fpt.g36.gapms.enums.NotificationEnum;
import fpt.g36.gapms.models.dto.notification.NotificationDTO;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.NotificationService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationUtils {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public NotificationUtils(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public void sendNotificationToRole(String role, String msg, NotificationEnum type,
                                       String targetUrl, String src, Boolean sendSms) {
        List<User> users = userRepository.findAllByRole(role);
        for (User user : users) {
            NotificationDTO notification = createNotification(
                    msg, type, targetUrl, user.getId(), src
            );
            notificationService.saveAndSendMultiChannelNotification(notification, sendSms);
        }
    }

    public void sendNotificationToUser(Long userId, String msg, NotificationEnum type, String targetUrl, String src, Boolean sendSms) {
        NotificationDTO notification = createNotification(
                msg, type, targetUrl, userId, src
        );
        notificationService.saveAndSendMultiChannelNotification(notification, sendSms);
    }

    // This method is used to create a notification object
    public void sendRfqApproveToTechnical(Long rfqId) {
        String msg = "Một RFQ mới (#" + rfqId + ") đã được chấp nhận và đang chờ xử lý.";
        String targetUrl = "/technical/rfq-details/" + rfqId;
        sendNotificationToRole("TECHNICAL", msg, NotificationEnum.SUCCESS, targetUrl, "Quản lý RFQ", true);
    }

    private NotificationDTO createNotification(String message, NotificationEnum type,
                                               String targetUrl, Long targetUserId, String source) {
        NotificationDTO notification = new NotificationDTO();
        notification.setMessage(message);
        notification.setType(type);
        notification.setTargetUrl(targetUrl);
        notification.setTargetUserId(targetUserId);
        notification.setSource(source);
        notification.setTimestamp(LocalDateTime.now());
        return notification;
    }


    public void addNewRfqToCustomerFromSale(Long rfqId) {
        String msg = "Đơn báo giá mới đã được lưu vào hệ thống, hiện đang chờ để được xử lý.";
        String targetUrl = "/sale-staff/cus-rfq-details/" + rfqId;
        sendNotificationToRole("SALE_STAFF", msg, NotificationEnum.SUCCESS, targetUrl, "Đơn báo giá mới", false);
    }

    public void sentQuotationToSaleStaffToCustomer(Long rfqId, Long userId) {
        String msg = "Đơn báo giá đã được duyệt, quý khách vui lòng vào và xác nhận.";
        String targetUrl = "/quotation/quotation-customer/" + rfqId;
        sendNotificationToUser(userId, msg, NotificationEnum.SUCCESS, targetUrl, "Xác Nhận Báo Giá",false);
    }


    public void sentFalseEasyToQaFromTechnical(Long rsId) {
        String msg = "Kết quả kiểm tra lỗi, hãy kiểm tra.";
        String targetUrl = "/risk-solution/technical/detail/" + rsId;
        sendNotificationToRole("TECHNICAL", msg, NotificationEnum.SUCCESS, targetUrl, "Lỗi Nhẹ",false);
    }

    public void sentFalseSeriousToQaFromTechnical(Long rsId) {
        String msg = "Kết quả kiểm tra lỗi, hãy kiểm tra.";
        String targetUrl = "/risk-solution/technical/detail/" + rsId;
        sendNotificationToRole("TECHNICAL", msg, NotificationEnum.SUCCESS, targetUrl, "Lỗi Nặng",false);
    }

    public void sentSuccessStageToLeaderFromQA(Long dyeBatchId, String role) {
        if(role.equalsIgnoreCase("QA_DYE")){
        String msg = "Công đoạn nhuộm đã hoàn thành, chờ kiểm tra";
        String targetUrl = "/work-order/technology-process/" + dyeBatchId;
        sendNotificationToRole("QA_DYE", msg, NotificationEnum.SUCCESS, targetUrl, "Nhuộm Hoàn Thành",false);
        }else if(role.equalsIgnoreCase("QA_WINDING")){
            String msg = "Công đoạn đánh côn đã hoàn thành, chờ kiểm tra";
            String targetUrl = "/work-order/technology-process/" + dyeBatchId;
            sendNotificationToRole("QA_WINDING", msg, NotificationEnum.SUCCESS, targetUrl, "Đánh Côn Hoàn Thành",false);
        }else{
            String msg = "Công đoạn đóng gói đã hoàn thành, chờ kiểm tra";
            String targetUrl = "/work-order/technology-process/" + dyeBatchId;
            sendNotificationToRole("QA_PACKAGING", msg, NotificationEnum.SUCCESS, targetUrl, "Đóng Gói Hoàn Thành",false);
        }
    }


    public void sentSuccessTestToQaFromLeader(Long dyeBatchId, String role) {
        if(role.equalsIgnoreCase("LEAD_WINDING")){
            String msg = "Công đoạn nhuộm đạt chuẩn chất lượng, chờ côn";
            String targetUrl = "/work-order/technology-process/" + dyeBatchId;
            sendNotificationToRole("LEAD_WINDING", msg, NotificationEnum.SUCCESS, targetUrl, "Nhuộm Đạt Chuẩn",false);
        }else if(role.equalsIgnoreCase("LEAD_PACKAGING")){
            String msg = "Công đoạn đánh côn đạt chuẩn chất lượng, chờ đóng gói";
            String targetUrl = "/work-order/technology-process/" + dyeBatchId;
            sendNotificationToRole("LEAD_PACKAGING", msg, NotificationEnum.SUCCESS, targetUrl, "Đánh Côn Đạt Chuẩn",false);
        }
    }

    public void sentFalseSeriousFromTechnicalToPo(Long rsId) {
        String msg = "Phiếu yêu cầu cấp sợi đã được tạo, hãy phê duyệt.";
        String targetUrl = "/risk-solution/production-manage/detail/" + rsId;
        sendNotificationToRole("PRODUCTION_MANAGER", msg, NotificationEnum.SUCCESS, targetUrl, "Phiếu Yêu Cầu Cấp Sợi",false);
    }

    public void sentRedoStageFromPoToDyeLeader(Long dyeBatchId) {
        String msg = "Phiếu yêu cầu cấp sợi đã được duyệt, hãy hoàn thành lại mẻ hàng.";
        String targetUrl = "/work-order/technology-process/" + dyeBatchId;
        sendNotificationToRole("LEAD_DYE", msg, NotificationEnum.SUCCESS, targetUrl, "Làm Lại Mẻ Lỗi",false);
    }

    public void sentRedoStageFromTechnicalToDyeLeader(Long dyeBatchId) {
        String msg = "Mẻ lỗi đã được duyệt, hãy hoàn thành lại mẻ hàng.";
        String targetUrl = "/work-order/technology-process/" + dyeBatchId;
        sendNotificationToRole("LEAD_DYE", msg, NotificationEnum.SUCCESS, targetUrl, "Làm Lại Mẻ Lỗi",false);
    }
}
