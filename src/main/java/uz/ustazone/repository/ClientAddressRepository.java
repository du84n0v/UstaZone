package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.ClientAddressEntity;

import java.util.List;
import java.util.Optional;

public interface ClientAddressRepository extends CrudRepository<ClientAddressEntity, String> {

    List<ClientAddressEntity> findAllByClientIdAndVisibleTrueOrderByCreatedDateDesc(String clientId);

    Optional<ClientAddressEntity> findByIdAndClientIdAndVisibleTrue(String id, String profileId);
}
