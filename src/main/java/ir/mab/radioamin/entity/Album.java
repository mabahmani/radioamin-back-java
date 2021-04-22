package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
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
    Singer singer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "cover_id", referencedColumnName = "id")
    Cover cover;

    @JsonIgnore
    @OneToMany(mappedBy = "album")
    Set<Music> musics;
}
