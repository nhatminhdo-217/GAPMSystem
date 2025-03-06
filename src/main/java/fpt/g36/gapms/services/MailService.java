package fpt.g36.gapms.services;

public interface MailService {

    void sendVerifyMail(String email, String code, int EXPIRED_TIME);

    void sendResetPasswordMail(String email, String token);

    void sendPasswordEmail(String recipientEmail, String password);

    void sendQuotationEmail(String recipientEmail, String name, Long quotationId);
}
