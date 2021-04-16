package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Singer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @OneToMany(mappedBy = "singer")
    Set<Music> musics;

    @OneToMany(mappedBy = "singer")
    Set<Follow> follows;
}
