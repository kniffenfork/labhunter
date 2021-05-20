package ru.lab.hunter.model.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;


@Entity
@Table(name = "cv_key_skills")
@Getter
@Setter
public class CvKeySkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String name;

    @Column(name = "CV_id")
    @JsonIgnore
    private Long cvId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CV_id", insertable = false, updatable = false)
    @JsonIgnore
    private Cv cv;

    @Override
    public String toString() {
        return "CvKeySkill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cvId=" + cvId +
                ", cv=" + cv +
                '}';
    }
}
