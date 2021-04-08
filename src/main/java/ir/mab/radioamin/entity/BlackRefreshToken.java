package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class BlackRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String refreshToken;
    Long expiredAt;
}
