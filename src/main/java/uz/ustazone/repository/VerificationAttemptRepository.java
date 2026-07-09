package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.VerificationAttemptEntity;

public interface VerificationAttemptRepository extends CrudRepository<VerificationAttemptEntity, Integer> {

    VerificationAttemptEntity findByEmail(String email);
}