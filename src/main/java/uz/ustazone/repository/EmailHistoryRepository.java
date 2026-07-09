package uz.ustazone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.EmailHistoryEntity;

import java.time.LocalDateTime;

public interface EmailHistoryRepository extends CrudRepository<EmailHistoryEntity, Integer> {

    @Query("SELECT COUNT(eh) FROM EmailHistoryEntity eh " +
            " WHERE eh.toContact = ?1 AND eh.createdDate >= ?2 ")
    int countByToEmailAfter(String toAccount, LocalDateTime from);

    EmailHistoryEntity findTopByToContactOrderByCreatedDateDesc(String email);

    Page<EmailHistoryEntity> findAll(Pageable pageable);

    Page<EmailHistoryEntity> getAllByToContact(String email, Pageable pageable);
}
