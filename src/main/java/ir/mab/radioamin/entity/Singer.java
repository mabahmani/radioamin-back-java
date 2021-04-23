package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import ir.mab.radioamin.model.Views;
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
    @JsonView(Views.Summary.class)
    Long id;

    @NotBlank
    @Column(unique = true)
    @JsonView(Views.Summary.class)
    String name;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "avatarId", referencedColumnName = "id")
    @JsonView(Views.Summary.class)
    @JsonIgnoreProperties({"singer"})
    Avatar avatar;

    @JsonView(Views.Expand.class)
    @OneToMany(mappedBy = "singer")
    @JsonIgnoreProperties({"singer"})
    Set<Album> albums;

    @JsonView(Views.Expand.class)
    @OneToMany(mappedBy = "singer")
    @JsonIgnoreProperties({"singer"})
    Set<Music> musics;

    @JsonView(Views.Expand.class)
    @OneToMany(mappedBy = "singer")
    @JsonIgnoreProperties({"singer"})
    Set<Follow> follows;
}
