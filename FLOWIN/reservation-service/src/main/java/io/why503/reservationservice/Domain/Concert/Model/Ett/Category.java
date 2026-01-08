package io.why503.reservationservice.Domain.Concert.Model.Ett;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_sq")
    private Long id;

    @Column(name = "category_no")
    private Integer number;

    @Column(name = "category_name", nullable = false)
    private String name;
}