package ir.mab.radioamin.entity;

import ir.mab.radioamin.annotaion.ValidPassword;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Email
    String email;

    @ValidPassword
    String password;
}
