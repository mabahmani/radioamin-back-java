package ir.mab.radioamin.model.req;

import ir.mab.radioamin.entity.User;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
public class ChangeUserPasswordRequest {

    @Valid
    User user;

    @NotBlank
    String activationCode;
}
