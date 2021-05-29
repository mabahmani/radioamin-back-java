package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude = "moods")
@ToString(exclude = "moods")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    @Column(unique = true)
    String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "genres")
    @JsonIgnoreProperties({"genres"})
    Set<Music> musics;


    @JsonIgnore
    @ManyToMany(mappedBy = "genres")
    Set<Mood> moods = new HashSet<>();
}
