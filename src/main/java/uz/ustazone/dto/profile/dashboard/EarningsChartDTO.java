package uz.ustazone.dto.profile.dashboard;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EarningsChartDTO {
    
    @JsonFormat(pattern = "MM-dd")
    private LocalDate date;
    
    private Double amount;     
}