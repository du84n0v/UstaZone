package uz.ustazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.ustazone.entity.MasterCategoryEntity;
import uz.ustazone.repository.MasterCategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MasterCategoryService {

    @Autowired
    private MasterCategoryRepository masterCategoryRepository;

    public void merge(String profileId, List<String> categoryIds) {
        deleteCategoryByProfileId(profileId);
        List<MasterCategoryEntity> ls = new ArrayList<>();
        for (String categoryId : categoryIds) {
            MasterCategoryEntity entity = new MasterCategoryEntity();
            entity.setMasterProfileId(profileId);
            entity.setCategoryId(categoryId);

            ls.add(entity);
        }

        masterCategoryRepository.saveAll(ls);
    }

    public void deleteCategoryByProfileId(String profileId){
        masterCategoryRepository.deleteByMasterProfileId(profileId);
    }
}
