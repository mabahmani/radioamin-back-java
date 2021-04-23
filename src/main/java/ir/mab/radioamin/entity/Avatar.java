package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@EqualsAndHashCode(exclude = {"profile","singer"})
@ToString(exclude = {"profile","singer"})
public class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @NotBlank
    String url;

    @JsonIgnore
    @OneToOne(mappedBy = "avatar")
    Profile profile;

    @JsonIgnore
    @OneToOne(mappedBy = "avatar")
    Singer singer;

    public String getUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(url).toUriString();
    }

    @JsonIgnore
    public String getFilePath() {
        return url;
    }
}
