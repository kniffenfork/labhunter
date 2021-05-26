package ru.lab.hunter.model.employer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.lab.hunter.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    private String description;

    private String website;

    @OneToMany(mappedBy="company")
    @JsonIgnore
    private Set<Vacancy> vacancies;

    @ManyToMany(cascade=CascadeType.PERSIST)
    @JsonIgnore
    @JoinTable(
            name = "employers_companies",
            joinColumns = @JoinColumn(name = "company_id"), inverseJoinColumns = @JoinColumn(name = "employer_id")
    )
    private Set<User> employers;

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", website='" + website + '\'' +
                ", vacancies=" + vacancies +
                ", employers=" + employers +
                '}';
    }
}
