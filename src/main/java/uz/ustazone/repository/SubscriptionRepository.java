package uz.ustazone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.ustazone.entity.SubscriptionEntity;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, String> {
    Optional<SubscriptionEntity> findByUserId(String masterId);
}
