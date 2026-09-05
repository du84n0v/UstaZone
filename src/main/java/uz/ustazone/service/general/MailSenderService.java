package uz.ustazone.service.general;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uz.ustazone.dto.auth.MailMessageDTO;
import uz.ustazone.exception.AppBadRequestException;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class MailSenderService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private EmailHistoryService emailHistoryService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationCode(MailMessageDTO dto, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(dto.getToAccount());
            helper.setSubject(dto.getSubject());
            helper.setText(dto.getBody(), true);

            mailSender.send(message);

//            log.info("Code sent. Email: {} Code: {} ", dto.getToAccount(), code);
            emailHistoryService.create(dto, code);
        } catch (MessagingException e) {
//            log.error(e.getMessage());
            throw new RuntimeException("Email yuborishda xatolik: " + e.getMessage());
        }
    }

    public void verificationCode(String toAccount) {
        runOut(toAccount);
        MailMessageDTO mailMessage = new MailMessageDTO();
        mailMessage.setToAccount(toAccount);
        mailMessage.setSubject("UstaZone - Tasdiqlash kodi");
        String code = generateCode();

        String body = "<div style=\"font-family: Arial; max-width: 500px; margin: auto; padding: 20px; border: 1px solid #eee;\">" +
                "<h2 style=\"color: #2196F3; text-align: center;\">USTAZONE</h2>" +
                "<p>Assalomu alaykum,</p>" +
                "<p>Sizning tasdiqlash kodingiz:</p>" +
                "<div style=\"font-size: 28px; font-weight: bold; text-align: center; padding: 20px; background: #f4f4f4; border-radius: 10px;\">" +
                code + "</div>" +
                "<p style=\"color: #666;\">Iltimos, ushbu kodni hech kimga bermang.</p>" +
                "</div>";
        mailMessage.setBody(body);
        sendVerificationCode(mailMessage, code);

    }

    private void runOut(String toAccount) {
        LocalDateTime from = LocalDateTime.now().minusMinutes(1);
        int cnt = emailHistoryService.getCountAfter(toAccount, from);
        if(cnt >= 4){
            log.warn("Too many request by {}", toAccount);
            throw new AppBadRequestException("Too many request. Please try again later");
        }
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

}