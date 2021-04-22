package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Data
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    @Column(unique = true)
    String name;

    @JsonIgnore
    @OneToMany(mappedBy = "language")
    Set<Music> musics;
}
