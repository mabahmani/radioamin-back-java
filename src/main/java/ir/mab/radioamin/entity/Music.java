package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import ir.mab.radioamin.model.Views;
import ir.mab.radioamin.model.enums.MusicType;
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
    @JsonView(Views.Summary.class)
    Long id;

    @NotBlank
    @JsonView(Views.Summary.class)
    String name;

    @NotNull
    @JsonView(Views.Summary.class)
    Boolean published;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Summary.class)
    MusicType musicType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @JsonIgnoreProperties({"musics"})
    User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "singer_id")
    @JsonView(Views.Summary.class)
    @JsonIgnoreProperties({"musics"})
    Singer singer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cover_id", referencedColumnName = "id")
    @JsonView(Views.Summary.class)
    @JsonIgnoreProperties({"music"})
    Cover cover;

    @ManyToMany
    @JoinTable(
            name = "music_genres",
            joinColumns = @JoinColumn(name = "music_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @JsonView(Views.Expand.class)
    @JsonIgnoreProperties({"musics"})
    Set<Genre> genres;

    @ManyToOne
    @JoinColumn(name = "language_id")
    @JsonView(Views.Expand.class)
    @JsonIgnoreProperties({"musics"})
    Language language;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "album_id")
    @JsonView(Views.Expand.class)
    @JsonIgnoreProperties({"musics"})
    Album album;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(Views.Expand.class)
    @JsonIgnoreProperties({"music"})
    Set<MusicUrl> musicUrls;

    @JsonIgnore
    @ManyToMany(mappedBy = "musics")
    @JsonIgnoreProperties({"musics"})
    Set<Playlist> playlists;

    @JsonIgnore
    @OneToMany(mappedBy = "music")
    @JsonIgnoreProperties({"music"})
    Set<LikeDislike> likeDislikes;

    @JsonIgnore
    @OneToMany(mappedBy = "music")
    @JsonIgnoreProperties({"music"})
    Set<Download> downloads;

    @JsonIgnore
    @OneToMany(mappedBy = "music")
    @JsonIgnoreProperties({"music"})
    Set<Activity> activities;
}
