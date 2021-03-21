package ir.mab.radioamin.annotaion;

import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {

    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8),
                new CharacterRule(EnglishCharacterData.UpperCase,1),
                new CharacterRule(EnglishCharacterData.LowerCase,1),
                new CharacterRule(EnglishCharacterData.Special,1),
                new CharacterRule(EnglishCharacterData.Digit,1),
                new UsernameRule(),
                new WhitespaceRule()));


        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        String messages = String.join(", ", validator.getMessages(result));
        context.buildConstraintViolationWithTemplate(messages).addConstraintViolation();
        return false;
    }
}
