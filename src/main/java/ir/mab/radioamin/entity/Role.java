package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.mab.radioamin.model.enums.RoleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude = "users")
@ToString(exclude = "users")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    @NotNull
    RoleEnum role;

    @JsonIgnore
    @ManyToMany(mappedBy = "userRoles", fetch = FetchType.EAGER)
    Set<User> users;

    public Role() {
    }

    public Role(@NotNull RoleEnum role) {
        this.role = role;
    }
}
