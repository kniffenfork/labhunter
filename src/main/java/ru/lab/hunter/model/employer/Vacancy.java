package ru.lab.hunter.model.employer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.lab.hunter.model.User;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="vacancies")
@Getter
@Setter
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String schedule;

    private String experience;

    @Column(nullable = false)
    private String name;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "employer_id")
    private Long employerId;

    private Boolean archived;

    @OneToMany(mappedBy="vacancy", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Set<VacancySkill> vacancySkills;

    @OneToOne(mappedBy="vacancy", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private VacancyAddress vacancyAddress;

    @OneToOne(mappedBy="vacancy", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private VacancySalary vacancySalary;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="company_id", insertable = false, updatable = false)
    @JsonIgnore
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employer_id", insertable = false, updatable = false)
    @JsonIgnore
    private User employer;

    @Override
    public String toString() {
        return "Vacancy{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", schedule='" + schedule + '\'' +
                ", experience='" + experience + '\'' +
                ", name='" + name + '\'' +
                ", companyId=" + companyId +
                ", employerId=" + employerId +
                ", archived=" + archived +
                ", vacancySkills=" + vacancySkills +
                ", vacancyAddress=" + vacancyAddress +
                ", vacancySalary=" + vacancySalary +
                ", company=" + company +
                ", employer=" + employer +
                '}';
    }
}
