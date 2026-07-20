package uz.ustazone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.general.EmailChangeRequestDTO;
import uz.ustazone.dto.profile.general.EmailVerifyRequestDTO;
import uz.ustazone.dto.profile.general.PasswordUpdateDTO;
import uz.ustazone.dto.profile.master.MasterCabinetResponseDTO;
import uz.ustazone.dto.profile.master.MasterProfileUpdateDTO;
import uz.ustazone.dto.profile.master.MasterScheduleUpdateDTO;
import uz.ustazone.dto.profile.master.MasterServicePriceUpdateDTO;
import uz.ustazone.service.profile.master.MasterProfileService;
import uz.ustazone.util.SpringSecurityUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master")
@PreAuthorize("hasRole('MASTER')")
@Tag(name = "Master API", description = "API list for Master")
public class MasterController {

    @Autowired
    private MasterProfileService masterService;

    @GetMapping("/cabinet")
    @Operation(summary = "Cabinet", description = "API used for Getting Master own Cabinet")
    public ResponseEntity<MasterCabinetResponseDTO> getCabinetData() {
        String masterId = SpringSecurityUtil.getCurrentProfileId();

        MasterCabinetResponseDTO response = masterService.getMasterCabinetInfo(masterId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/detail")
    @Operation(summary = "Update", description = "API used for updating master detail info")
    public ResponseEntity<String> updateDetail(@Valid @RequestBody MasterProfileUpdateDTO dto){
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updateMasterDetail(masterId, dto));
    }

    @PostMapping("/update/password")
    @Operation(summary = "Update password", description = "API used for update master password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updatePassword(masterId, dto));
    }

    @PostMapping("/settings/change-email-request")
    @Operation(summary = "Update email", description = "API used for register only Master")
    public ResponseEntity<String> changeEmailRequest(@Valid @RequestBody EmailChangeRequestDTO dto) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updateEmail(masterId, dto.getNewEmail()));
    }

    @PostMapping("/verify-change-email")
    @Operation(summary = "Verify", description = "API used for Verify email when changing email")
    public ResponseEntity<String> verifyChangeEmail(@Valid @RequestBody EmailVerifyRequestDTO dto) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.verifyAndChangeEmail(masterId, dto));
    }

    @PutMapping("/services/prices")
    @Operation(summary = "Changing price", description = "API used for changing service prices")
    public ResponseEntity<String> updateServicePrices(@RequestBody List<MasterServicePriceUpdateDTO> dtoList) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updatePrices(masterId, dtoList));
    }

    @PutMapping("/schedule")
    @Operation(summary = "Reschedule", description = "API used for reschedule work days")
    public ResponseEntity<String> updateSchedule(@RequestBody MasterScheduleUpdateDTO dto) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(masterService.updateSchedule(masterId, dto));
    }
}