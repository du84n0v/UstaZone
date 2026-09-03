package uz.ustazone.service.profile.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.admin.AdminComplaintActionDTO;
import uz.ustazone.dto.profile.admin.AdminComplaintResponseDTO;
import uz.ustazone.dto.profile.admin.AdminComplaintViewDTO;
import uz.ustazone.entity.ComplaintEntity;
import uz.ustazone.enums.ComplaintStatusEnum;
import uz.ustazone.enums.ComplaintTabEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.mapper.admin.AdminComplainViewMapper;
import uz.ustazone.repository.ComplaintAttachRepository;
import uz.ustazone.repository.ComplaintRepository;
import uz.ustazone.service.AttachService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class AdminComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;
    @Autowired
    private ComplaintAttachRepository complaintAttachRepository;
    @Autowired
    private AttachService attachService;

    @Transactional(readOnly = true)
    public Page<AdminComplaintViewDTO> getComplaints(ComplaintTabEnum tab, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<ComplaintStatusEnum> statuses = switch (tab) {
            case OPEN -> List.of(ComplaintStatusEnum.OPEN);
            case CLOSED -> List.of(ComplaintStatusEnum.RESOLVED, ComplaintStatusEnum.REJECTED);
            case ALL -> List.of(ComplaintStatusEnum.OPEN, ComplaintStatusEnum.RESOLVED, ComplaintStatusEnum.REJECTED);
        };

        Page<AdminComplainViewMapper> pages = complaintRepository.getComplainsByTab(statuses, pageable);
        return pages.map(this::viewMapperToDto);
    }

    @Transactional(readOnly = true)
    public AdminComplaintResponseDTO getById(String id) {
        AdminComplainViewMapper mapper = complaintRepository.getMapperById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        List<String> ids = complaintAttachRepository.findAttachIdsByComplaintId(id);
        List<String> urls = (ids != null)
                ? ids.stream().map(attachService::openURL).toList()
                : Collections.emptyList();

        return new AdminComplaintResponseDTO(viewMapperToDto(mapper), urls);
    }

    @Transactional
    public String resolve(String id, AdminComplaintActionDTO dto) {
        ComplaintEntity complain = getEntity(id);

        if (!ComplaintStatusEnum.OPEN.equals(complain.getStatus())) {
            throw new AppBadRequestException("Faqat ochiq holatdagi shikoyatni hal qilish mumkin!");
        }

        complain.setAdminNote(dto != null ? dto.getAdminNote() : null);
        complain.setStatus(ComplaintStatusEnum.RESOLVED);
        complain.setResolvedDate(LocalDateTime.now());

        complaintRepository.save(complain);

        return "Successfully resolved";
    }

    @Transactional
    public String reject(String id, AdminComplaintActionDTO dto) {
        ComplaintEntity complain = getEntity(id);

        if (!ComplaintStatusEnum.OPEN.equals(complain.getStatus())) {
            throw new AppBadRequestException("Faqat ochiq holatdagi shikoyatni rad etish mumkin!");
        }

        complain.setAdminNote(dto != null ? dto.getAdminNote() : null);
        complain.setStatus(ComplaintStatusEnum.REJECTED);
        complain.setResolvedDate(LocalDateTime.now());

        complaintRepository.save(complain);

        return "Successfully rejected";
    }

    private ComplaintEntity getEntity(String id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

    }

    private AdminComplaintViewDTO viewMapperToDto(AdminComplainViewMapper mapper){
        return new AdminComplaintViewDTO(
                mapper.getId(),
                mapper.getCategory(),
                mapper.getDescription(),
                mapper.getStatus(),
                mapper.getAdminNote(),
                mapper.getOrderId(),
                mapper.getReporterId(),
                mapper.getReporterFullName(),
                mapper.getReporterPhone(),
                mapper.getTargetId(),
                mapper.getTargetFullName(),
                mapper.getTargetPhone(),
                mapper.getCreatedDate(),
                mapper.getResolvedDate()
        );
    }
}
