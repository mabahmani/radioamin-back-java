package ir.mab.radioamin.model.res;

import ir.mab.radioamin.model.Error;
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
