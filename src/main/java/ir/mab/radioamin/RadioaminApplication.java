package ir.mab.radioamin;

import ir.mab.radioamin.entity.Role;
import ir.mab.radioamin.entity.User;
import ir.mab.radioamin.model.enums.RoleEnum;
import ir.mab.radioamin.repository.RoleRepository;
import ir.mab.radioamin.repository.UserRepository;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

import static org.passay.RepeatCharactersRule.ERROR_CODE;

@SpringBootApplication
public class RadioaminApplication {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(RadioaminApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createSuperUser() {
        User user = userRepository.findUserByEmail("superuser@radioamin.app")
                .orElseGet(() -> {

                    Role developer = roleRepository.findRoleByRole(RoleEnum.DEVELOPER).orElseGet(() -> {
                        Role role = new Role();
                        role.setRole(RoleEnum.DEVELOPER);
                        return roleRepository.save(role);
                    });

                    Role admin = roleRepository.findRoleByRole(RoleEnum.ADMIN).orElseGet(() -> {
                        Role role = new Role();
                        role.setRole(RoleEnum.ADMIN);
                        return roleRepository.save(role);
                    });

                    Role consumer = roleRepository.findRoleByRole(RoleEnum.CONSUMER).orElseGet(() -> {
                        Role role = new Role();
                        role.setRole(RoleEnum.CONSUMER);
                        return roleRepository.save(role);
                    });

                    Set<Role> superRoles = new HashSet<>();
                    superRoles.add(developer);
                    superRoles.add(admin);
                    superRoles.add(consumer);

                    User dUser = new User();
                    dUser.setActive(true);
                    dUser.setUserRoles(superRoles);
                    dUser.setCreatedAt(System.currentTimeMillis());
                    dUser.setEmail("superuser@radioamin.app");
                    dUser.setPassword(passwordEncoder.encode("9rr#4X^.AgV8KX@"));

                    return userRepository.save(dUser);
                });

        String password = generatePassayPassword();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        System.out.println("SECRET_PASS: " + password);
    }

    public String generatePassayPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        return gen.generatePassword(32, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
    }

}
