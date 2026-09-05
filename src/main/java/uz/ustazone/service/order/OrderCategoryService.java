package uz.ustazone.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.ustazone.entity.OrderCategoryEntity;
import uz.ustazone.repository.OrderCategoryRepository;

@Service
public class OrderCategoryService {

    @Autowired
    private OrderCategoryRepository orderCategoryRepository;

    public void save(String categoryId, String orderId) {
        OrderCategoryEntity entity = new OrderCategoryEntity();
        entity.setCategoryId(categoryId);
        entity.setOrderId(orderId);

        orderCategoryRepository.save(entity);
    }
}
