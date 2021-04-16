package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class LikeDislike {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    Boolean isLike;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "music_id", nullable = false)
    Music music;
}
