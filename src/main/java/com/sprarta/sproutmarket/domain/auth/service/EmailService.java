package com.sprarta.sproutmarket.domain.auth.service;

import com.sprarta.sproutmarket.domain.common.RedisUtil;
import com.sprarta.sproutmarket.domain.common.enums.ErrorStatus;
import com.sprarta.sproutmarket.domain.common.exception.ApiException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    public void sendEmail(String redisKey, String email) {
        int authNumber = createAuthNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            String body = "";
            body += "<h3> 가입 인증 번호입니다. </h3>";
            body += "<h1> " + authNumber + " </h1>";

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(SENDER_EMAIL); // 보내는 이
            helper.setTo(email);          // 받는 이
            helper.setSubject("이메일 인증"); // 이메일 제목
            helper.setText(body, true);
        } catch (MessagingException e) {
            throw new ApiException(ErrorStatus.FAIL_EMAIL_SENDING);
        }

        // 메일 전송
        javaMailSender.send(message);

        // redis 저장
        redisUtil.authEmail(redisKey, authNumber);
    }

    public int createAuthNumber() {
        return (int)((Math.random() * 1000000));
    }
}
