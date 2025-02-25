package fpt.g36.gapms.services.impls;

import fpt.g36.gapms.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendVerifyMail(String email, String code, int EXPIRED_TIME) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify your email");
        message.setText("Your verification code is: " + code + ". It will be expired in " + EXPIRED_TIME + " minutes");
        mailSender.send(message);
    }

    @Override
    @Async
    public void sendResetPasswordMail(String email, String token) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset your password");
        message.setText("Your reset password token: " + token);
        mailSender.send(message);
    }

    @Override
    @Async
    public void sendPasswordEmail(String email, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Password");
        message.setText("Dear You,\n\n"
                + "Here is your temporary password: " + password + "\n\n"
                + "Please change your password after your first login.\n\n"
                + "Thank you!");

        mailSender.send(message);
    }
}
