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

    @JsonIgnoreProperties({"albums"})
    Singer singer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false)

    @JsonIgnoreProperties({"album"})
    Cover cover;

    @OneToMany(mappedBy = "album")

    @JsonIgnoreProperties({"album"})
    @JsonIgnore
    Set<Music> musics;
}
