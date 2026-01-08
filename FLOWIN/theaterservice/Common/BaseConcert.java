package io.why503.theaterservice.Common;


import jakarta.persistence.*;

@MappedSuperclass
@Entity
public abstract class baseConcert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public baseConcert() {

    }
}
