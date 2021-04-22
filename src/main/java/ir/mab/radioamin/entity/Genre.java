package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    @Column(unique = true)
    String name;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "music_genres",
            joinColumns = @JoinColumn(name = "music_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    Set<Music> musics;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "mood_geners",
            joinColumns = @JoinColumn(name = "mood_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    Set<Mood> moods;
}
