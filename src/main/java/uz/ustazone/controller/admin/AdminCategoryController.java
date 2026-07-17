package uz.ustazone.controller.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.category.CategoryRequestDTO;
import uz.ustazone.dto.category.CategoryTreeResponseDTO;
import uz.ustazone.service.profile.admin.AdminCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    @GetMapping("/tree")
    public ResponseEntity<List<CategoryTreeResponseDTO>> getCategoryTree() {
        return ResponseEntity.ok(adminCategoryService.getTreeStructure());
    }

    @PostMapping
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
        ;
        return ResponseEntity.ok(adminCategoryService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable String id, @Valid @RequestBody CategoryRequestDTO dto) {
        return ResponseEntity.ok(adminCategoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable String id) {
        return ResponseEntity.ok(adminCategoryService.delete(id));
    }
}