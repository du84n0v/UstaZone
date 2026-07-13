package uz.ustazone.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.OrderEntity;
import uz.ustazone.enums.GeneralStatusEnum;
import uz.ustazone.enums.OrderStatusEnum;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OrderRepository extends CrudRepository<OrderEntity, Long> {

    Long countByMasterIdAndStatusIn(String masterId, Collection<OrderStatusEnum> statuses);

    Long countByMasterIdAndStatus(String masterId, OrderStatusEnum status); // COMPLETED status bilan chaqiriladi

    Long countByClientId(String clientId);

    Long countByClientIdAndStatusIn(String clientId, List<OrderStatusEnum> activeStatuses);

    List<OrderEntity> findAllByClientIdAndStatusInOrderByCreatedDateDesc(String clientId, List<OrderStatusEnum> activeStatuses);
}