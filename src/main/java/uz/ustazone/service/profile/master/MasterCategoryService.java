package uz.ustazone.service.profile.master;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.ustazone.dto.profile.master.MasterServicePriceUpdateDTO;
import uz.ustazone.entity.MasterCategoryEntity;
import uz.ustazone.enums.PriceTypeEnum;
import uz.ustazone.exception.AppBadRequestException;
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

    public List<MasterCategoryEntity> getMasterCategories(String masterId) {
        return masterCategoryRepository.findAllByMasterProfileIdAndCategoryParentIsNotNull(masterId);
    }

    @Transactional
    public void updateCategoryPrice(List<MasterServicePriceUpdateDTO> dtoList) {
        for (MasterServicePriceUpdateDTO dto : dtoList) {
            int effRow;
            if(dto.getPriceType().equals(PriceTypeEnum.FROM)) {
                effRow = masterCategoryRepository.updateCategoryPrice(dto.getId(), dto.getPriceType(), dto.getMinPrice(), dto.getMaxPrice());
            }
            else {
                effRow = masterCategoryRepository.updateCategoryPrice(dto.getId(), dto.getPriceType(), dto.getMinPrice(), null);
            }
            if(effRow == 0){
                throw new AppBadRequestException("Something went wrong");
            }
        }
    }
}
