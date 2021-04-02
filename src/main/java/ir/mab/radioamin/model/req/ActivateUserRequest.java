package ir.mab.radioamin.model.req;

import lombok.Data;

@Data
public class ActivateUserRequest {
    String email;
    String activationCode;
}
