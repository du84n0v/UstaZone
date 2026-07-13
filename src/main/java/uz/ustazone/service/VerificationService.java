package uz.ustazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.entity.EmailHistoryEntity;
import uz.ustazone.entity.VerificationAttemptEntity;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.repository.VerificationAttemptRepository;

import java.time.LocalDateTime;

@Service
public class VerificationService {

    @Autowired
    private VerificationAttemptRepository attemptRepository;
    @Autowired
    private VerificationAttemptService attemptService;
    @Autowired
    private EmailHistoryService emailHistoryService;

    /**
     * Faqat kod to'g'riligini va blokirovkalarni tekshiradigan umumiy metod.
     * Muammo bo'lsa AppBadRequestException otadi.
     */
    @Transactional
    public void checkVerificationCode(String email, String code) {
        LocalDateTime now = LocalDateTime.now();
        VerificationAttemptEntity attempt = attemptRepository.findByEmail(email);

        // 1. Urinishlar sonini tekshirish
        if (attempt != null && attempt.getAttemptCount() >= 3 && attempt.getLastAttempt() != null) {
            LocalDateTime expiryTime = attempt.getLastAttempt().plusMinutes(2);
            if (expiryTime.isAfter(now)) {
                throw new AppBadRequestException("Too many attempts. Please wait 2 minutes");
            } else {
                attemptService.resetAttemptCount(attempt);
            }
        }

        // 2. Oxirgi yuborilgan kodni bazadan olish
        EmailHistoryEntity lastCode = emailHistoryService.findTopByToEmailOrderByCreatedDateDesc(email);
        if (lastCode == null) {
            throw new AppBadRequestException("No code found for this email. Please click resend");
        }

        boolean isExpired = lastCode.getCreatedDate().plusMinutes(5).isBefore(now);
        boolean isWrongCode = !lastCode.getCode().equals(code);

        // 3. Kod muddati yoki xatoligini tekshirish
        if (isExpired || isWrongCode) {
            attempt = attemptService.incrementAttempt(email, attempt);
            int remaining = 3 - attempt.getAttemptCount();

            if (isExpired) {
                throw new AppBadRequestException("Code is expired. Please click resend to get a new code");
            }

            throw new AppBadRequestException("Wrong code: " + (remaining > 0 ? remaining + "-attempt left" : "Please try 2 minutes later"));
        }

        // Agar hammasi muvaffaqiyatli bo'lsa, urinishlar tarixini o'chiramiz
        if (attempt != null) {
            attemptRepository.delete(attempt);
        }
    }
}