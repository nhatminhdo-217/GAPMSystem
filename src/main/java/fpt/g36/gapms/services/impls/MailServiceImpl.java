package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerifyMail(String email, String code, int EXPIRED_TIME) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify your email");
        message.setText("Your verification code is: " + code + ". It will be expired in " + EXPIRED_TIME + " minutes");
        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordMail(String email, String token) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset your password");
        message.setText("Your reset password token: " + token);
        mailSender.send(message);
    }
}
