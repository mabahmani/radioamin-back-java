package ir.mab.radioamin.model;

import lombok.Data;

@Data
public class SuccessResponse<T> {
    String message;
    T Data;

    public SuccessResponse(String message, T data) {
        this.message = message;
        Data = data;
    }
}
