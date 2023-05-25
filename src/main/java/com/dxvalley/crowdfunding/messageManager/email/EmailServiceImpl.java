package com.dxvalley.crowdfunding.messageManager.email;

import com.dxvalley.crowdfunding.utils.ApiResponse;
import io.micrometer.common.lang.NonNull;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the EmailService interface for sending emails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Checks if the given email address is valid.
     *
     * @param email the email address to validate
     * @return true if the email is valid, false otherwise
     */
    @Override
    public boolean isValidEmail(String email) {
        String threadName = Thread.currentThread().getName(); // Get the current thread name
        System.err.println("valid email on thread: " + threadName);

        if (email == null) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Sends an email to the specified recipient asynchronously.
     *
     * @param recipientEmail the recipient's email address
     * @param emailBody      the content of the email
     * @param emailSubject   the subject of the email
     * @return a ResponseEntity indicating the status of the email sending operation
     */
    @Override
    @Async("asyncExecutor")
    public CompletableFuture<ApiResponse> send(@NonNull String recipientEmail, @NonNull String emailBody, @NonNull String emailSubject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(emailBody, true);
            helper.setTo(recipientEmail);
            helper.setSubject(emailSubject);
            mailSender.send(mimeMessage);

            return CompletableFuture.completedFuture(new ApiResponse(HttpStatus.OK, "Email sent successfully."));
        } catch (MessagingException | MailException ex) {
            log.error("An error occurred in {}.{} while sending email. Details: {}",
                    getClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    ex.getMessage());

            return CompletableFuture.completedFuture(
                    new ApiResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Cannot send email due to Internal Server Error. Please try again later."));
        }
    }
}
