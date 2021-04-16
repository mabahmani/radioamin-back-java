package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Mood {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToMany(mappedBy = "moods")
    Set<Genre> genres;
}
