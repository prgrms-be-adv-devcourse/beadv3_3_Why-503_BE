package io.why503.aiservice.model.vo;

import java.util.List;

public record ResultRequest(
        List<Category> category,
        List<Category> attention
) {}
