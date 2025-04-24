package fpt.g36.gapms.services.impls;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import fpt.g36.gapms.config.SmsConfig;
import fpt.g36.gapms.models.entities.User;
import fpt.g36.gapms.repositories.UserRepository;
import fpt.g36.gapms.services.SmsService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    private final SmsConfig smsConfig;
    private final UserRepository userRepository;

    public SmsServiceImpl(SmsConfig smsConfig, UserRepository userRepository) {
        this.smsConfig = smsConfig;
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void init() {
        Twilio.init(smsConfig.getAccountSid(), smsConfig.getAuthToken());
        logger.info("Twilio initialized with account: {}", smsConfig.getAccountSid());
    }

    @Override
    @Async
    public void sendSms(String phoneNumber, String message) {
        if (!smsConfig.getEnabled()){
            logger.info("SMS service is disabled. Would have sent '{}' to {}", message, phoneNumber);
        }

        try {
            if (!phoneNumber.startsWith("+")) {
                if (phoneNumber.startsWith("0")) {
                    phoneNumber = "+84" + phoneNumber.substring(1);
                } else {
                    phoneNumber = "+84" + phoneNumber;
                }
            }

            Message msg = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(smsConfig.getFromNumber()),
                    message
            ).create();

            logger.info("SMS sent to {}, SID: {}", phoneNumber, msg.getSid());
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendSmsNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            sendSms(user.getPhoneNumber(), message);
        } else {
            logger.warn("Cannot send SMS to user {} - phone number not available", userId);
        }
    }
}
