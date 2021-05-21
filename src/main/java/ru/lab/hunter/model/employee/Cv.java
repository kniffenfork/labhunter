package ru.lab.hunter.model.employee;

import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.lab.hunter.model.User;
import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name="cvs")
@Getter
@Setter
public class Cv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="employee_id", nullable = false)
    private Long employeeId;

    private String description;

    private String schedule;

    @NotEmpty
    private String experience;

    @Column(name="archived", insertable = false, nullable = false)
    private Boolean archived = false;

    @NotEmpty
    private String name;

    private Integer salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy="cv", fetch = FetchType.LAZY)
    private Set<CvKeySkill> keySkills;

    @OneToMany(mappedBy="cv", fetch = FetchType.LAZY)
    private Set<CvCourse> cvCourses;
}
