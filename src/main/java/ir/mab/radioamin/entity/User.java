package ir.mab.radioamin.entity;

import ir.mab.radioamin.annotaion.ValidPassword;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Email
    @Column(unique = true)
    @NotNull
    @NotBlank
    String email;

    @ValidPassword
    @NotNull
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
