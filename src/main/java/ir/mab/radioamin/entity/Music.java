package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ir.mab.radioamin.model.enums.MusicType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude = {"singer", "album", "genres"})
@ToString(exclude = {"singer", "album", "genres"})
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotBlank
    String name;

    @NotNull
    Boolean published;

    @NotNull
    @Enumerated(EnumType.STRING)
    MusicType musicType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "singer_id")
    @JsonIgnoreProperties({"musics"})
    Singer singer;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cover_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"music"})
    Cover cover;

    @ManyToMany
    @JoinTable(
            name = "music_genres",
            joinColumns = @JoinColumn(name = "music_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @JsonIgnoreProperties({"musics"})
    Set<Genre> genres;

    @ManyToOne
    @JoinColumn(name = "language_id")
    
    @JsonIgnoreProperties({"musics"})
    Language language;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "album_id")
    @JsonIgnoreProperties({"musics"})
    Album album;

    
    String lyric;

    @OneToMany(mappedBy = "music", cascade = CascadeType.ALL)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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

    @JsonIgnore
    Long playCount = 0L;
}
