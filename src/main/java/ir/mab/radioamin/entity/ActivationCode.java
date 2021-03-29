package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
public class ActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @NotNull
    @NotBlank
    @Size(min = 6, max = 6)
    @Column(length = 6)
    String code;

    @NotNull
    Long createdTime;

    @OneToOne(mappedBy = "activationCode")
    User user;
}
