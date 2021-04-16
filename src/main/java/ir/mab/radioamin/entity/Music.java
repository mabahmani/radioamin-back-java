package ir.mab.radioamin.entity;

import ir.mab.radioamin.model.MusicType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    MusicType musicType;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "singer_id")
    Singer singer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "cover_id", referencedColumnName = "id")
    Cover cover;

    @NotNull
    @ManyToMany(mappedBy = "musics")
    Set<Genre> genres;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "language_id", nullable = false)
    Language language;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    Album album;

    @NotNull
    @OneToMany(mappedBy = "music")
    Set<MusicUrl> musicUrls;

    @ManyToMany(mappedBy = "musics")
    Set<Playlist> playlists;

    @OneToMany(mappedBy = "music")
    Set<LikeDislike> likeDislikes;

    @OneToMany(mappedBy = "music")
    Set<Download> downloads;

    @OneToMany(mappedBy = "music")
    Set<Activity> activities;
}
