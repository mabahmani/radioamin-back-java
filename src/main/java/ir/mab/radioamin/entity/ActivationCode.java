package ir.mab.radioamin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
public class ActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    Long id;

    @NotNull
    @NotBlank
    @Size(min = 6, max = 6)
    @Column(length = 6)
    String code;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull
    Long createdTime;

    @JsonIgnore
    @OneToOne(mappedBy = "activationCode")
    User user;
}
