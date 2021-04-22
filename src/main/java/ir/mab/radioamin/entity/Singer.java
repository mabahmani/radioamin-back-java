package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude = {"albums","musics"})
@ToString(exclude = {"albums","musics"})
public class Singer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    @Column(unique = true)
    String name;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatarId", referencedColumnName = "id")
    Avatar avatar;


    @JsonIgnore
    @OneToMany(mappedBy = "singer")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Set<Album> albums;

    @OneToMany(mappedBy = "singer")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Set<Music> musics;

    @OneToMany(mappedBy = "singer")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Set<Follow> follows;
}
