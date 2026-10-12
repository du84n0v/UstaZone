package uz.ustazone.controller.general;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ustazone.dto.category.CategoryTreeResponseDTO;
import uz.ustazone.service.profile.admin.AdminCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Public Category API", description = "Barcha uchun ochiq kategoriyalar daraxti")
public class PublicCategoryController {

    @Autowired
    private AdminCategoryService adminCategoryService;

    @GetMapping("/tree")
    @Operation(summary = "Categories Tree", description = "Barcha kategoriyalar va sub-kategoriyalar daraxti (Ro'yxatdan o'tmaganlar uchun ham ochiq)")
    public ResponseEntity<List<CategoryTreeResponseDTO>> getTree() {
        return ResponseEntity.ok(adminCategoryService.getTreeStructure());
    }
}