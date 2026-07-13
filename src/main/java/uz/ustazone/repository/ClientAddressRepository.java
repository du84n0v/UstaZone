package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.ClientAddressEntity;

import java.util.List;

public interface ClientAddressRepository extends CrudRepository<ClientAddressEntity, String> {
    List<ClientAddressEntity> findAllByClientId(String clientId);
}
