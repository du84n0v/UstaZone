package uz.ustazone.service.profile.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.category.CategoryRequestDTO;
import uz.ustazone.dto.category.CategoryTreeResponseDTO;
import uz.ustazone.entity.CategoryEntity;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryTreeResponseDTO> getTreeStructure() {
        // 1. Bazadan hamma kategoriyalarni bitta so'rovda olamiz
        List<CategoryEntity> allCategories = categoryRepository.findAllByOrderByOrderNumAsc();

        // 2. Faqat asosiy (parent'i null bo'lgan) kategoriyalarni ajratib, daraxt qilamiz
        return allCategories.stream()
                .filter(c -> c.getParent() == null)
                .map(parent -> entityToTreeDto(parent, allCategories))
                .collect(Collectors.toList());
    }

    private CategoryTreeResponseDTO entityToTreeDto(CategoryEntity current, List<CategoryEntity> all) {
        List<CategoryTreeResponseDTO> subs = new ArrayList<>();

        for (CategoryEntity category : all) {
            // LAZY parent'ga teginib N+1 keltirib chiqarmaslik uchun children ro'yxatidan qidiramiz
            // Yoki current.getChildren().contains(category) mantiqidan foydalanamiz
            if (current.getChildren() != null && current.getChildren().contains(category)) {
                // Rekursiv ravishda bolasining ostidagilarni ham tekshirib ketaveradi
                subs.add(entityToTreeDto(category, all));
            }
        }

        return new CategoryTreeResponseDTO(
                current.getId(),
                current.getNameUz(),
                current.getNameRu(),
                current.getNameEn(),
                current.getIcon(),
                current.getColor(),
                current.getOrderNum(),
                current.getSlug(),
                subs.size(), // nechta sub borligi
                subs
        );
    }

    @Transactional
    public String create(CategoryRequestDTO dto) {
        CategoryEntity entity = new CategoryEntity();
        dtoToEntity(dto, entity);
        categoryRepository.save(entity);
        return "Added";
    }

    @Transactional
    public String update(String id, CategoryRequestDTO dto) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Kategoriya topilmadi"));
        
        dtoToEntity(dto, entity);
        categoryRepository.save(entity);
        return "Updated";
    }

    @Transactional
    public String delete(String id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Kategoriya topilmadi"));
        
        // cascade = CascadeType.ALL bo'lgani uchun ostidagi barcha bolalari ham birga o'chib ketadi
        categoryRepository.delete(entity);
        return "Deleted";
    }

    private void dtoToEntity(CategoryRequestDTO dto, CategoryEntity entity) {
        entity.setNameUz(dto.getNameUz());
        entity.setNameRu(dto.getNameRu());
        entity.setNameEn(dto.getNameEn());
        entity.setIcon(dto.getIcon());
        entity.setColor(dto.getColor());
        entity.setOrderNum(dto.getOrderNum());
        entity.setSlug(dto.getSlug());

        if (dto.getParentId() != null && !dto.getParentId().isBlank()) {
            CategoryEntity parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new ItemNotFoundException("Asosiy kategoriya topilmadi"));
            entity.setParent(parent);
        } else {
            entity.setParent(null);
        }
    }
}