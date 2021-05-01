package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    @Column(unique = true)
    String name;

//    @JsonIgnore
//    @OneToMany(mappedBy = "language")
//    Set<Music> musics;
}
