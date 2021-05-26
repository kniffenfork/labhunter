package ru.lab.hunter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.lab.hunter.model.employee.Cv;
import ru.lab.hunter.model.employer.Vacancy;
import ru.lab.hunter.security.Role;
import ru.lab.hunter.security.Status;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotNull
    private String first_name;

    @NotEmpty
    @NotNull
    private String last_name;

    private String phone_number;

    @NotEmpty
    @NotNull
    private String email;

    @NotNull
    @JsonIgnore
    private String password;

    @Transient
    @JsonIgnore
    private String confirm_password;

    @Column(name="accepted_mailing", insertable = false, nullable = false)
    private Boolean accepted_mailing;

   @Column(name="is_verificated", insertable = false, nullable = false)
    private Boolean is_verificated;

    private String vk;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Cv> cvs;

    @OneToMany(mappedBy = "employer")
    @JsonIgnore
    private Set<Vacancy> vacancies;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirm_password='" + confirm_password + '\'' +
                ", accepted_mailing=" + accepted_mailing +
                ", is_verificated=" + is_verificated +
                ", vk='" + vk + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", cvs=" + cvs +
                ", vacancies=" + vacancies +
                '}';
    }
}
