package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(unique = true)
    @NotNull
    @NotBlank
    String role;

    @ManyToMany(mappedBy = "userRoles")
    Set<User> users;
}
