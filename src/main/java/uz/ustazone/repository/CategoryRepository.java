package uz.ustazone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.ustazone.entity.CategoryEntity;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {

    List<CategoryEntity> findAllByOrderByOrderNumAsc();
}