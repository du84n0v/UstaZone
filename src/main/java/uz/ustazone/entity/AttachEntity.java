package uz.ustazone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "attach")
@Getter
@Setter
public class AttachEntity {

    @Id
    private String id;

    @Column(name = "origin_name")
    private String originName;

    @Column(name = "size")
    private Integer size;

    @Column(name = "type")
    private String type;

    @Column(name = "path")
    private String path;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
