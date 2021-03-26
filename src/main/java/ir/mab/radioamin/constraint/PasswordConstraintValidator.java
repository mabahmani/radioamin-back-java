package ir.mab.radioamin.constraint;

import ir.mab.radioamin.entity.User;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, User> {
    @Override
    public void initialize(ValidPassword constraintAnnotation) {

    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {

        if (user.getPassword() == null){
            user.setPassword("");
        }

        if (user.getEmail() == null){
            user.setEmail("");
        }

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8,64),
                new CharacterRule(EnglishCharacterData.UpperCase,1),
                new CharacterRule(EnglishCharacterData.LowerCase,1),
                new CharacterRule(EnglishCharacterData.Special,1),
                new CharacterRule(EnglishCharacterData.Digit,1),
                new UsernameRule(),
                new WhitespaceRule()));


        RuleResult result = validator.validate(new PasswordData(user.getEmail(),user.getPassword()));

        if (result.isValid()) {
            return true;
        }


        String messageTemplate = String.join(", ", validator.getMessages(result));
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }

}
