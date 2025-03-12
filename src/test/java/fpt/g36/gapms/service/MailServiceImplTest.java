package fpt.g36.gapms.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import fpt.g36.gapms.services.impls.MailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

class MailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailServiceImpl mailService;

    private String email;
    private String code;
    private int EXPIRED_TIME;
    private String token;
    private String password;
    private Long quotationId;
    private String name;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Giả lập các giá trị cần thiết
        email = "test@example.com";
        code = "123456";
        EXPIRED_TIME = 15;
        token = "resetToken123";
        password = "temporaryPassword";
        quotationId = 1001L;
        name = "John Doe";
    }

    @Test
    void sendVerifyMail_Success() {
        // Giả lập đối tượng SimpleMailMessage
        SimpleMailMessage mockMessage = new SimpleMailMessage();

        // Kiểm tra phương thức sendVerifyMail
        mailService.sendVerifyMail(email, code, EXPIRED_TIME);

        // Kiểm tra việc gọi mailSender.send() đúng 1 lần
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendResetPasswordMail_Success() {
        // Kiểm tra phương thức sendResetPasswordMail
        mailService.sendResetPasswordMail(email, token);

        // Kiểm tra phương thức send() của mailSender đã được gọi
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordEmail_Success() {
        // Kiểm tra phương thức sendPasswordEmail
        mailService.sendPasswordEmail(email, password);

        // Kiểm tra phương thức send() của mailSender đã được gọi
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendQuotationEmail_Success() {
        // Kiểm tra phương thức sendQuotationEmail
        mailService.sendQuotationEmail(email, name, quotationId);

        // Kiểm tra phương thức send() của mailSender đã được gọi
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}

