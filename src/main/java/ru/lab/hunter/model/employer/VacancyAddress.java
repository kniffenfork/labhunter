package ru.lab.hunter.model.employer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "vacancies_addresses")
@Getter
@Setter
public class VacancyAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "vacancy_id")
    @JsonIgnore
    private Long vacancyId;

    @NotEmpty
    private String country;

    @NotEmpty
    private String city;

    @NotEmpty
    private String street;

    @NotEmpty
    private String building;

    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="vacancy_id", insertable = false, updatable = false)
    @JsonIgnore
    private Vacancy vacancy;

    @Override
    public String toString() {
        return "VacancyAddress{" +
                "id=" + id +
                ", vacancyId=" + vacancyId +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", building='" + building + '\'' +
                ", description='" + description + '\'' +
                ", vacancy=" + vacancy +
                '}';
    }
}
