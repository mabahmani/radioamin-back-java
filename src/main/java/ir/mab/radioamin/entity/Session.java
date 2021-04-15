package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    @JsonIgnore
    @NotBlank
    @Column(nullable = false)
    String refreshToken;

    @NotBlank
    @Column(nullable = false)
    String userAgent;

    @NotBlank
    @Column(nullable = false)
    String ip;

    @Column(nullable = false)
    Long lastUpdate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
