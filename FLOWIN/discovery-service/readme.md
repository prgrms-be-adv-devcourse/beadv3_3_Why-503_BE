server:
    port:   //이건 깃헙 secret에 있음

eureka:
    client:
        register-with-eureka: false //자기 자신을 등록하지 않음
        fetch-registry: false // 다른 eureka 서버에서 읽어오지 않음
    instance:
    prefer-ip-address: true //hostname 말고 IP를 저장

spring:
    application:
        name: discovery-server