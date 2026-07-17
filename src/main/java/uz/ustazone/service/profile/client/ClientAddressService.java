package uz.ustazone.service.profile.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.ustazone.repository.ClientAddressRepository;

@Service
public class ClientAddressService {

    @Autowired
    private ClientAddressRepository clientAddressRepository;
}
