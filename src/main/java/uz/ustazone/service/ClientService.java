package uz.ustazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.ustazone.repository.ProfileRepository;

@Service
public class ClientService {

    @Autowired
    private ProfileRepository profileRepository;
}
