package uz.ustazone.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.ustazone.dto.auth.MailMessageDTO;
import uz.ustazone.entity.EmailHistoryEntity;
import uz.ustazone.repository.EmailHistoryRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmailHistoryService {

    @Autowired
    private EmailHistoryRepository historyRepository;

    public void create(MailMessageDTO dto, String code) {
        EmailHistoryEntity history = new EmailHistoryEntity();
        history.setToContact(dto.getToAccount());
        history.setTitle(dto.getSubject());
        history.setMessage(dto.getBody());
        history.setCode(code);

        historyRepository.save(history);
    }

    public int getCountAfter(String toAccount, LocalDateTime from) {
        return historyRepository.countByToEmailAfter(toAccount, from);
    }

    public EmailHistoryEntity getLastHistoryByEmail(String email) {
        return historyRepository.findTopByToContactOrderByCreatedDateDesc(email);
    }

    public EmailHistoryEntity findTopByToEmailOrderByCreatedDateDesc(String email) {
        return historyRepository.findTopByToContactOrderByCreatedDateDesc(email);
    }
}
