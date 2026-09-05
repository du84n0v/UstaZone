package uz.ustazone.service.profile.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.address.ClientAddressRequestDTO;
import uz.ustazone.dto.address.ClientAddressResponseDTO;
import uz.ustazone.entity.ClientAddressEntity;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.ClientAddressRepository;

import java.util.List;

@Service
public class ClientAddressService {

    @Autowired
    private ClientAddressRepository clientAddressRepository;

    @Transactional
    public ClientAddressResponseDTO create(String clientId, ClientAddressRequestDTO dto) {
        ClientAddressEntity entity = ClientAddressEntity.builder()
                .clientId(clientId)
                .addressType(dto.getType())
                .addressLine(dto.getAddress().trim())
                .region((dto.getRegion() != null && !dto.getRegion().isBlank()) ? dto.getRegion().trim() : "Toshkent")
                .build();

        clientAddressRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<ClientAddressResponseDTO> getAll(String profileId) {
        return clientAddressRepository.findAllByClientIdAndVisibleTrueOrderByCreatedDateDesc(profileId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void delete(String clientId, String addressId) {
        ClientAddressEntity entity = clientAddressRepository.findByIdAndClientIdAndVisibleTrue(addressId, clientId)
                .orElseThrow(() -> new ItemNotFoundException("Manzil topilmadi"));

        entity.setVisible(false);
        clientAddressRepository.save(entity);
    }

    private ClientAddressResponseDTO toDto(ClientAddressEntity entity) {
        return ClientAddressResponseDTO.builder()
                .id(entity.getId())
                .type(entity.getAddressType())
                .addressLine(entity.getAddressLine())
                .region(entity.getRegion())
                .createdDate(entity.getCreatedDate())
                .build();
    }
}
