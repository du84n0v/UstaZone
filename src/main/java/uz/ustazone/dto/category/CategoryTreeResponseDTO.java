package uz.ustazone.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeResponseDTO {
    private String id;
    private String nameUz;
    private String nameRu;
    private String nameEn;
    private String icon;
    private String color;
    private Integer orderNum;
    private String slug;
    private Integer subCount;
    private List<CategoryTreeResponseDTO> subCategories;
}