package io.why503.aiservice.model.vo;

import java.util.Optional;

public enum Category {
    CONCERT, MUSICAL, PLAY, CLASSIC;


    public static Optional<Category> fromString(String value) {
        try {
            return Optional.of(valueOf(value.toUpperCase()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
