package com.zerobase.challengeproject.member.components;

import com.zerobase.challengeproject.exception.CustomException;
import com.zerobase.challengeproject.exception.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailComponents {

    private final JavaMailSender mailSender;

    /**
     * 메일을 보내기 위한 메서드
     * @param to 회원 가입을 시도하는 유저의 이메일
     * @param subject 메일의 제목
     * @param body 회원 가입 축하문, 이메일 인증 링크
     * @return 메일 전송 유무
     */
    public boolean send(String to, String subject, String body) {

        MimeMessagePreparator msg = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body, true);
        };
        try {
            mailSender.send(msg);
            return true;
        }catch (Exception e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }

    }
}
