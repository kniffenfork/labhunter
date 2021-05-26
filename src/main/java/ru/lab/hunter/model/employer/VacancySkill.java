package ru.lab.hunter.model.employer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "vacancies_skills")
@Getter
@Setter
public class VacancySkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @Column(name = "vacancy_id")
    @JsonIgnore
    private Long vacancyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="vacancy_id", insertable = false, updatable = false)
    @JsonIgnore
    private Vacancy vacancy;

    @Override
    public String toString() {
        return "VacancySkill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vacancyId=" + vacancyId +
                ", vacancy=" + vacancy +
                '}';
    }
}
