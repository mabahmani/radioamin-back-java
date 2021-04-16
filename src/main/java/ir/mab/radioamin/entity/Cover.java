package ir.mab.radioamin.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Cover {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @OneToOne(mappedBy = "cover")
    Music music;

}
