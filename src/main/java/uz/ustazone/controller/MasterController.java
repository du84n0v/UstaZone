package uz.ustazone.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.EmailChangeRequestDTO;
import uz.ustazone.dto.profile.EmailVerifyRequestDTO;
import uz.ustazone.dto.profile.PasswordUpdateDTO;
import uz.ustazone.dto.profile.master.MasterCabinetResponseDTO;
import uz.ustazone.dto.profile.master.MasterProfileUpdateDTO;
import uz.ustazone.dto.profile.master.MasterScheduleUpdateDTO;
import uz.ustazone.dto.profile.master.MasterServicePriceUpdateDTO;
import uz.ustazone.service.MasterProfileService;
import uz.ustazone.util.SpringSecurityUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master")
@PreAuthorize("hasRole('MASTER')")
public class MasterController {

    @Autowired
    private MasterProfileService masterService;

    @GetMapping("/cabinet")
    public ResponseEntity<MasterCabinetResponseDTO> getCabinetData() {
        String masterId = SpringSecurityUtil.getCurrentProfileId();

        MasterCabinetResponseDTO response = masterService.getMasterCabinetInfo(masterId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/detail")
    public ResponseEntity<String> updateDetail(@Valid @RequestBody MasterProfileUpdateDTO dto){
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updateMasterDetail(masterId, dto));
    }

    @PostMapping("/update/password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updatePassword(masterId, dto));
    }

    @PostMapping("/settings/change-email-request")
    public ResponseEntity<String> changeEmailRequest(@Valid @RequestBody EmailChangeRequestDTO dto) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updateEmail(masterId, dto.getNewEmail()));
    }

    @PostMapping("/verify-change-email")
    public ResponseEntity<String> verifyChangeEmail(@Valid @RequestBody EmailVerifyRequestDTO dto) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.verifyAndChangeEmail(masterId, dto));
    }

    @PutMapping("/services/prices")
    public ResponseEntity<String> updateServicePrices(@RequestBody List<MasterServicePriceUpdateDTO> dtoList) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updatePrices(masterId, dtoList));
    }

    @PutMapping("/schedule")
    public ResponseEntity<String> updateSchedule(@RequestBody MasterScheduleUpdateDTO dto) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updateSchedule(masterId, dto));
    }
}