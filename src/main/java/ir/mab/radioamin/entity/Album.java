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
@EqualsAndHashCode(exclude = {"singer","musics"})
@ToString(exclude = {"singer","musics"})
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Summary.class)
    Long id;

    @NotBlank
    @JsonView(Views.Summary.class)
    String name;

    @JsonView(Views.Expand.class)
    Long releaseDate;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "singer_id", nullable = false)
    @JsonView(Views.Expand.class)
    @JsonIgnoreProperties({"albums"})
    Singer singer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JsonView(Views.Expand.class)
    @JsonIgnoreProperties({"album"})
    Cover cover;

    @OneToMany(mappedBy = "album")
    @JsonView(Views.Expand.class)
    @JsonIgnoreProperties({"album"})
    Set<Music> musics;
}
