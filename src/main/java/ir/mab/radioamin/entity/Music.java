package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "singer_id")
    @JsonView(Views.Summary.class)
    Singer singer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "cover_id", referencedColumnName = "id")
    @JsonView(Views.Summary.class)
    Cover cover;

    @ManyToMany(mappedBy = "musics")
    @JsonView(Views.Expand.class)
    Set<Genre> genres;

    @ManyToOne(optional = false)
    @JoinColumn(name = "language_id", nullable = false)
    @JsonView(Views.Expand.class)
    Language language;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    @JsonView(Views.Expand.class)
    Album album;

    @OneToMany(mappedBy = "music")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(Views.Expand.class)
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
