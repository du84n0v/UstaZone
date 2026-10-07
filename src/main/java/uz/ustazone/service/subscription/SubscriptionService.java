package uz.ustazone.service.subscription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.subscription.SubscriptionActivateDTO;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.entity.SubscriptionEntity;
import uz.ustazone.enums.TariffAudienceEnum;
import uz.ustazone.enums.TariffTierEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.repository.MasterProfileRepository;
import uz.ustazone.repository.SubscriptionRepository;
import uz.ustazone.util.SpringSecurityUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SubscriptionService {

    @Autowired
    private MasterProfileRepository masterProfileRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Transactional
    public String activate(SubscriptionActivateDTO dto) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();

        // 1. Ustani topamiz
        MasterProfileEntity master = masterProfileRepository.findByProfileId(masterId)
                .orElseThrow(() -> new AppBadRequestException("Usta topilmadi"));

        // 2. Tarif narxini belgilaymiz (Hozircha test uchun qattiq narxlar qo'yamiz, keyin DB dagi Tariff jadvalidan olinadi)
        BigDecimal price = BigDecimal.ZERO;
        if (dto.getTier() == TariffTierEnum.PRO) {
            price = new BigDecimal("50000"); // Pro tarif narxi 50 ming so'm
        } else if (dto.getTier() == TariffTierEnum.PREMIUM) {
            price = new BigDecimal("100000"); // Premium tarif 100 ming so'm
        }

        // 3. Balans yetarliligini tekshiramiz
        if (master.getBalance() == null || master.getBalance().compareTo(price) < 0) {
            throw new AppBadRequestException("Balansingizda mablag' yetarli emas. Iltimos hamyoningizni to'ldiring!");
        }

        // 4. Pulni yechib olamiz
        master.setBalance(master.getBalance().subtract(price));
        masterProfileRepository.save(master);

        // 5. Eski obunani o'chiramiz (agar bo'lsa) va yangisini ochamiz
        subscriptionRepository.findByUserId(masterId).ifPresent(subscriptionRepository::delete);

        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setUserId(masterId);
        subscription.setTier(dto.getTier());
        subscription.setAudience(TariffAudienceEnum.MASTER);
        subscription.setBillingPeriod(dto.getBillingPeriod());
        subscription.setIsTrial(false);
        
        // Obuna 30 kunga beriladi
        subscription.setExpiresDate(LocalDateTime.now().plusDays(30));
        
        subscriptionRepository.save(subscription);

        return "Obuna muvaffaqiyatli faollashtirildi! Tarif: " + dto.getTier().name();
    }
}