package org.example.doantn.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendCredentials(String toEmail, String username, String rawPassword) {
        String subject = "Thông tin tài khoản hệ thống";
        String text = String.format("""
                Chào bạn,

                Tài khoản học tập của bạn đã được tạo:
                ➤ MSSV: %s
                ➤ Mật khẩu: %s

                Vui lòng đăng nhập và đổi mật khẩu sau lần đầu sử dụng.

                Trân trọng.
                """, username, rawPassword);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}