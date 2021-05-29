package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @JsonIgnoreProperties({"singer"})
    @JsonIgnore
    Avatar avatar;


    @OneToMany(mappedBy = "singer")
    @JsonIgnoreProperties({"singer"})
    @JsonIgnore
    Set<Album> albums;


    @OneToMany(mappedBy = "singer")
    @JsonIgnoreProperties({"singer"})
    @JsonIgnore
    Set<Music> musics;


    @OneToMany(mappedBy = "singer")
    @JsonIgnoreProperties({"singer"})
    @JsonIgnore
    Set<Follow> follows;
}
