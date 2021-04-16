package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToMany
    @JoinTable(
            name = "music_genres",
            joinColumns = @JoinColumn(name = "music_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    Set<Music> musics;

    @ManyToMany
    @JoinTable(
            name = "mood_geners",
            joinColumns = @JoinColumn(name = "mood_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    Set<Mood> moods;
}
