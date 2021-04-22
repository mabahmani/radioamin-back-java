package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class Cover {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotNull
    String url;

    @JsonIgnore
    @OneToOne(mappedBy = "cover")
    Music music;

    @JsonIgnore
    @OneToOne(mappedBy = "cover")
    Album album;

    @JsonIgnore
    @OneToOne(mappedBy = "cover")
    Playlist playlist;

    public String getUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(url).toUriString();
    }
}
