package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.OrderCategoryEntity;

public interface OrderCategoryRepository extends CrudRepository<OrderCategoryEntity, String> {
}
