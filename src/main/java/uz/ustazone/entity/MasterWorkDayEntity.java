package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.WeekDayEnum;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "master_work_day")
public class MasterWorkDayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "master_profile_id")
    private String masterProfileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_profile_id", nullable = false, insertable = false, updatable = false)
    private MasterProfileEntity masterProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private WeekDayEnum dayOfWeek;

    @Column(name = "work_start")
    private LocalTime workStart;

    @Column(name = "work_end")
    private LocalTime workEnd;
}