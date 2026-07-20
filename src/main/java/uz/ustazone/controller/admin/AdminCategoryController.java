package uz.ustazone.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Category API", description = "API list for Crud Category")
public class AdminCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    @GetMapping("/tree")
    @Operation(summary = "Categories", description = "API used for getting all categories with subs")
    public ResponseEntity<List<CategoryTreeResponseDTO>> getCategoryTree() {
        return ResponseEntity.ok(adminCategoryService.getTreeStructure());
    }

    @PostMapping
    @Operation(summary = "Create", description = "API used for creating Category ans subs")
    public ResponseEntity<String> createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
        return ResponseEntity.ok(adminCategoryService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update", description = "API used for Updateing category")
    public ResponseEntity<String> updateCategory(@PathVariable String id, @Valid @RequestBody CategoryRequestDTO dto) {
        return ResponseEntity.ok(adminCategoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete", description = "API used for deleting category")
    public ResponseEntity<String> deleteCategory(@PathVariable String id) {
        return ResponseEntity.ok(adminCategoryService.delete(id));
    }
}