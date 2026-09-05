package uz.ustazone.service.general;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.entity.VerificationAttemptEntity;
import uz.ustazone.repository.VerificationAttemptRepository;

import java.time.LocalDateTime;

@Service
public class VerificationAttemptService {

    @Autowired
    private VerificationAttemptRepository attemptRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public VerificationAttemptEntity incrementAttempt(String email, VerificationAttemptEntity attempt) {
        if (attempt == null) {
            attempt = new VerificationAttemptEntity();
            attempt.setEmail(email);
            attempt.setAttemptCount(1);
            attempt.setLastAttempt(LocalDateTime.now());
            return attemptRepository.save(attempt);
        }
        attempt.setAttemptCount(attempt.getAttemptCount() + 1);
        attempt.setLastAttempt(LocalDateTime.now());
        return attemptRepository.save(attempt);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetAttemptCount(VerificationAttemptEntity attempt) {
        attempt.setAttemptCount(0);
        attemptRepository.save(attempt);
    }
}