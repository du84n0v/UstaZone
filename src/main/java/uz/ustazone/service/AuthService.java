package uz.ustazone.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uz.ustazone.config.security.CustomUserDetails;
import uz.ustazone.dto.auth.*;
import uz.ustazone.dto.profile.general.CurrentProfileDTO;
import uz.ustazone.dto.profile.master.MasterResponseDTO;
import uz.ustazone.dto.security.JwtDTO;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.entity.VerificationAttemptEntity;
import uz.ustazone.enums.GeneralStatusEnum;
import uz.ustazone.enums.ProfileRoleEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.ProfileRepository;
import uz.ustazone.repository.VerificationAttemptRepository;
import uz.ustazone.service.profile.master.MasterProfileService;
import uz.ustazone.util.JwtUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private AttachService attachService;
    @Autowired
    private MasterProfileService masterProfileService;
    @Autowired
    private MailSenderService mailSenderService;
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private VerificationAttemptRepository attemptRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    public String registerMaster(MasterRegisterDTO dto) {

        userExists(dto.getEmail(), dto.getPhone());

        ProfileEntity profile = dtoToEntity(dto);

        if (dto.getProfilePicId() != null) profile.setAvatarAttachId(dto.getProfilePicId());

        profileRepository.save(profile);

        masterProfileService.save(profile.getId(), dto);

        sendCode(dto.getEmail());

        return "Emailga kod yuborildi";
    }

    public String registerClient(ClientRegisterDTO dto) {
        userExists(dto.getEmail(), dto.getPhone());

        ProfileEntity profile = new ProfileEntity();
        profile.setFullName(dto.getFullName());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setPassword(passwordEncoder.encode(dto.getPassword()));
        profile.setRole(ProfileRoleEnum.ROLE_CLIENT);
        profile.setStatus(GeneralStatusEnum.IN_REGISTRATION);

        profileRepository.save(profile);

        sendCode(dto.getEmail());

        return "Emailga kod yuborildi";
    }

    private void userExists(String email, String phone) {
        Optional<ProfileEntity> optional = profileRepository.findByEmailAndPhoneAndVisible(email, phone, true);
        if (optional.isPresent()) {
            if (optional.get().getStatus().equals(GeneralStatusEnum.IN_REGISTRATION)) {
                profileRepository.delete(optional.get());
            } else {
                throw new AppBadRequestException("User already exists");
            }
        }
    }

    private void sendCode(String email) {
        mailSenderService.verificationCode(email);

        VerificationAttemptEntity attempt = attemptRepository.findByEmail(email);
        if (attempt == null) attempt = new VerificationAttemptEntity();
        attempt.setEmail(email);
        attempt.setAttemptCount(0);
        attempt.setLastAttempt(LocalDateTime.now());
        attempt.setLastResendTime(LocalDateTime.now());
        attemptRepository.save(attempt);
    }

    private ProfileEntity dtoToEntity(MasterRegisterDTO dto) {
        ProfileEntity profile = new ProfileEntity();
        profile.setFullName(dto.getFullName());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setPassword(passwordEncoder.encode(dto.getPassword()));
        profile.setRole(ProfileRoleEnum.ROLE_MASTER);
        profile.setRegion(dto.getRegion());
        profile.setCity(dto.getCity());
        profile.setLatitude(dto.getLatitude());
        profile.setLongitude(dto.getLongitude());
        profile.setStatus(GeneralStatusEnum.IN_REGISTRATION);

        return profile;
    }

    @Transactional
    public String verify(VerificationDTO dto) {
        verificationService.checkVerificationCode(dto.getEmail(), dto.getCode());

        ProfileEntity profile = profileRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ItemNotFoundException("User not found"));

        profile.setStatus(GeneralStatusEnum.ACTIVE);
        profileRepository.save(profile);

        return "Successfully activated. You can login by now";
    }

    @Transactional
    public String resendCode(ResendCodeDTO dto) {
        String email = dto.getEmail();
        LocalDateTime now = LocalDateTime.now();
        VerificationAttemptEntity attempt = attemptRepository.findByEmail(email);

        if (attempt != null && attempt.getLastResendTime() != null) {
            LocalDateTime limit = attempt.getLastResendTime().plusMinutes(1);
            if (limit.isAfter(now)) {
                long remain = Duration.between(now, limit).toSeconds();
                throw new AppBadRequestException("Wait " + remain + "-second to resend code");
            }
        }

        mailSenderService.verificationCode(email);

        if (attempt == null) {
            attempt = new VerificationAttemptEntity();
            attempt.setEmail(email);
            attempt.setResendCount(1);
        } else {
            attempt.setResendCount((attempt.getResendCount() == null ? 0 : attempt.getResendCount()) + 1);
        }

        attempt.setLastResendTime(now);
        attempt.setAttemptCount(0);
        attemptRepository.save(attempt);

        return "Code sent to your email. Please check email";
    }

    public MasterResponseDTO login(LoginRequestDTO login) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword())
            );

            if (authentication.isAuthenticated()) {

                if (authentication.getPrincipal() instanceof CustomUserDetails user) {
                    JwtDTO jwt = new JwtDTO(
                            user.getId(),
                            user.getEmail(),
                            user.getRole().name());

                    CurrentProfileDTO current = new CurrentProfileDTO();
                    current.setId(user.getId());
                    current.setFullName(user.getFullName());
                    current.setEmail(user.getEmail());
                    current.setRole(user.getRole().name());
                    String avatarId = profileRepository.findAvatarAttachIdById(user.getId());
                    String avatarUrl = (avatarId != null ? attachService.openURL(avatarId) : null);

                    MasterResponseDTO response = new MasterResponseDTO();
                    response.setProfile(current);
                    response.setAvatarUrl(avatarUrl);
                    response.setAccessToken(JwtUtil.getToken(jwt, Boolean.TRUE));
                    response.setRefreshToken(JwtUtil.getToken(jwt, Boolean.FALSE));

                    return response;
                }
            }
        } catch (BadCredentialsException e) {
//            log.error("Username or password is wrong {} {}", login.getEmail(), login.getPassword());
            throw new AppBadRequestException("Username or password wrong");
        } catch (DisabledException e) {
//            log.error("This user not active {} ", login.getEmail());
            throw new AppBadRequestException("This user is not active");
        }
        throw new AppBadRequestException("Username or password wrong");
    }
}
