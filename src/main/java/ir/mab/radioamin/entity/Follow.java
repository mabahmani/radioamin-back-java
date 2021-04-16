package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "singer_id", nullable = false)
    Singer singer;
}
