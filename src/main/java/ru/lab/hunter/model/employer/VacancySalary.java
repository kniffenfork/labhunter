package ru.lab.hunter.model.employer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "vacancies_salaries")
@Getter
@Setter
public class VacancySalary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "vacancy_id")
    @JsonIgnore
    private Long vacancyId;

    @Column(name = "salary_to")
    private Long salaryTo;

    @Column(name = "salary_from")
    private Long salaryFrom;

    @Column(nullable = false)
    private Boolean gross;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="vacancy_id", insertable = false, updatable = false)
    @JsonIgnore
    private Vacancy vacancy;

    @Override
    public String toString() {
        return "VacancySalary{" +
                "id=" + id +
                ", vacancyId=" + vacancyId +
                ", salaryTo=" + salaryTo +
                ", salaryFrom=" + salaryFrom +
                ", gross=" + gross +
                ", vacancy=" + vacancy +
                '}';
    }
}
