package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ir.mab.radioamin.constraint.ValidPassword;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
@ValidPassword
@ToString(exclude = "musics")
@EqualsAndHashCode(exclude = "musics")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @Email
    @NotBlank
    @Column(unique = true)
    String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Boolean active = false;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "userRoles",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    Set<Role> userRoles;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "activationCodeId", referencedColumnName = "id")
    ActivationCode activationCode;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profileId", referencedColumnName = "id")
    @JsonIgnore
    Profile profile;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @OneToMany(mappedBy = "user")
    Set<Session> sessions;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    Set<Playlist> playlists;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    Set<LikeDislike> likeDislikes;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    Set<Download> downloads;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    Set<Activity> activities;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    Set<Follow> follows;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    Set<Music> musics;
}
