package ir.mab.radioamin.entity;

import ir.mab.radioamin.constraint.ValidPassword;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
@ValidPassword
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Email
    @Column(unique = true)
    @NotBlank
    String email;

    @NotBlank
    String password;

    Boolean active = false;

    @ManyToMany
    @JoinTable(
            name = "userRoles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    Set<Role> userRoles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "activationCodeId", referencedColumnName = "id")
    ActivationCode activationCode;

}
