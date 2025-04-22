package fpt.g36.gapms.services;

public interface SmsService {

    void sendSms(String phoneNumber, String message);

    void sendSmsNotification(Long userId, String message);
}
