package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(exclude = {"user","avatar"})
@ToString(exclude = {"user","avatar"})
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    String firstName;
    String lastName;
    String displayName;
    String bio;

    @JsonIgnore
    @OneToOne(mappedBy = "profile")
    User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatarId", referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Avatar avatar;
}
