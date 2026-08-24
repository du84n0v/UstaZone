package uz.ustazone.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.admin.AdminUserResponseDTO;
import uz.ustazone.service.profile.admin.AdminUserService;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin User API", description = "API list for Control all Users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    // 1. Foydalanuvchilar ro'yxati (Ism yoki telefon bo'yicha qidiruv bilan)
    @GetMapping
    @Operation(summary = "Get", description = "API used for getting all users")
    public ResponseEntity<Page<AdminUserResponseDTO>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AdminUserResponseDTO> users = adminUserService.getUsers(search, PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }

    // 2. Foydalanuvchini bloklash yoki blokdan chiqarish (Toggle)
    @PutMapping("/{id}/toggle-block")
    @Operation(summary = "Put", description = "API used for update users status")
    public ResponseEntity<Void> toggleBlock(@PathVariable String id) {
        adminUserService.toggleBlock(id);
        return ResponseEntity.ok().build();
    }

    // 3. Foydalanuvchini o'chirish (Soft delete)
    @PutMapping("/{id}")
    @Operation(summary = "Put", description = "API used for delete users")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}