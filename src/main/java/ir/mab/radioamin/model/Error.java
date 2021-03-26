package ir.mab.radioamin.model;

import ir.mab.radioamin.constant.ErrorType;
import lombok.Data;

@Data
public class Error{
    ErrorType errorType;
    String errorOn;
    String message;
    String help;

    public Error(ErrorType errorType, String message, String help) {
        this.errorType = errorType;
        this.message = message;
        this.help = help;
    }

    public Error(ErrorType errorType, String errorOn, String message, String help) {
        this.errorType = errorType;
        this.errorOn = errorOn;
        this.message = message;
        this.help = help;
    }
}
