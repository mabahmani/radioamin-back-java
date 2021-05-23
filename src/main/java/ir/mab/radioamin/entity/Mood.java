package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Mood {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    @Column(unique = true)
    String name;

    @ManyToMany
    @JoinTable(
            name = "mood_geners",
            joinColumns = @JoinColumn(name = "mood_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    Set<Genre> genres = new HashSet<>();
}
