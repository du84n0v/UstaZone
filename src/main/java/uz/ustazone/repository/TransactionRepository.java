package uz.ustazone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.ustazone.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
}
