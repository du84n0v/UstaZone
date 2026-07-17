package uz.ustazone.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDTO {
    @NotBlank(message = "Kategoriya nomi bo'sh bo'lmasligi kerak")
    private String nameUz;
    @NotBlank(message = "Kategoriya nomi bo'sh bo'lmasligi kerak")
    private String nameRu;
    @NotBlank(message = "Kategoriya nomi bo'sh bo'lmasligi kerak")
    private String nameEn;
    private String icon;
    private String color;
    private Integer orderNum;
    private String slug;
    private String parentId;      // Agar sub-kategoriya qo'shilayotgan bo'lsa, asosiy kategoriyaning ID-si keladi
}