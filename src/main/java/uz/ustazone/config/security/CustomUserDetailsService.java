package uz.ustazone.config.security;

import org.jspecify.annotations.NonNull;
import uz.ustazone.enums.GeneralStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.ProfileRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        System.out.println("loadUsername: " + username);
        Optional<ProfileEntity> optional = profileRepository.findByEmailAndStatusAndVisibleTrue(username, GeneralStatusEnum.ACTIVE, true);
        if(optional.isEmpty()){
            throw new ItemNotFoundException("User not found");
        }
        ProfileEntity profile = optional.get();
        return new CustomUserDetails(
                profile.getId(),
                profile.getEmail(),
                profile.getPassword(),
                profile.getFullName(),
                profile.getStatus(),
                profile.getRole()
        );
    }
}
