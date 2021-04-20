package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @NotBlank
    String name;

    @NotNull
    Boolean published;

    @NotNull
    @Enumerated(EnumType.STRING)
    MusicType musicType;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    User user;

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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Set<MusicUrl> musicUrls;

    @JsonIgnore
    @ManyToMany(mappedBy = "musics")
    Set<Playlist> playlists;

    @JsonIgnore
    @OneToMany(mappedBy = "music")
    Set<LikeDislike> likeDislikes;

    @JsonIgnore
    @OneToMany(mappedBy = "music")
    Set<Download> downloads;

    @JsonIgnore
    @OneToMany(mappedBy = "music")
    Set<Activity> activities;
}
