package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.mab.radioamin.model.enums.MusicUrlType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@ToString(exclude = "music")
@EqualsAndHashCode(exclude = "music")
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

    public String getUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(url).toUriString();
    }

    @JsonIgnore
    public String getFilePath() {
        return url;
    }
}
