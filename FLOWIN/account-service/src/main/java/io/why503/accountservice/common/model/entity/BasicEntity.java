package io.why503.accountservice.common.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
/*
account 와 company에서 사용하는 기본 엔티티
공통되는 부분을 만들어둠
 */
@Getter
@MappedSuperclass
public class BasicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    protected Long sq;

    @Setter
    @Column(name = "name")
    protected String name;

    @Setter
    @Column(name = "phone")
    protected String phone;

    @Setter
    @Column(name = "email")
    protected String email;

    @Setter
    @Column(name = "basic_addr")
    protected String basicAddr;

    @Setter
    @Column(name = "detail_addr")
    protected String detailAddr;

    @Setter
    @Column(name = "post")
    protected String post;
}
