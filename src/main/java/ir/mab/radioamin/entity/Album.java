package ir.mab.radioamin.entity;

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
@EqualsAndHashCode(exclude = {"singer","musics"})
@ToString(exclude = {"singer","musics"})
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    String name;

    Long releaseDate;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "singer_id", nullable = false)
    @JsonIgnoreProperties({"albums","musics","follows"})
    Singer singer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "cover_id", referencedColumnName = "id")
    Cover cover;

    @OneToMany(mappedBy = "album")
    Set<Music> musics;
}
