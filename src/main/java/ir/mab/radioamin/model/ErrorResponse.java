package ir.mab.radioamin.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ErrorResponse {

    List<Error> errors;

    public ErrorResponse(List<Error> errors) {
        this.errors = errors;
    }

    public ErrorResponse(Error error) {
        this.errors = Collections.singletonList(error);
    }
}
