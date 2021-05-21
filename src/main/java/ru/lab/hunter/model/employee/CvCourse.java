package ru.lab.hunter.model.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="cv_courses")
@Getter
@Setter
public class CvCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    
    @Column(name="date_of_completion")
    private Date dateOfCompletion;

    private String organization;

    private String description;

    @Column(name="cv_id")
    @JsonIgnore
    private Long cvId;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cv_id", insertable = false, updatable = false)
    @JsonIgnore
    private Cv cv;

    @Override
    public String toString() {
        return "CvCourse{" +
                "id=" + id +
                ", dateOfCompletion=" + dateOfCompletion +
                ", organization='" + organization + '\'' +
                ", description='" + description + '\'' +
                ", cvId=" + cvId +
                '}';
    }
}
