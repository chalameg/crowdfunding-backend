package com.dxvalley.crowdfunding.messageManager.email;

import io.micrometer.common.lang.NonNull;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }

    @Override
    @Async
    public void send(@NonNull String recipientEmail, @NonNull String emailBody, @NonNull String emailSubject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(emailBody, true);
            helper.setTo(recipientEmail);
            helper.setSubject(emailSubject);
            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            LOGGER.error("Failed to send email", e);
            throw new RuntimeException("Cannot sent email due to Internal Server Error. Please try again later");
        }
    }
}
