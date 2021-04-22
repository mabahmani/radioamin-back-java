package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.mab.radioamin.model.MusicUrlType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class MusicUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotNull
    MusicUrlType musicUrlType;

    @NotNull
    String url;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "music_id")
    Music music;
}
