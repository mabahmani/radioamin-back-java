package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class MusicUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    @JoinColumn(name = "music_id")
    Music music;
}
