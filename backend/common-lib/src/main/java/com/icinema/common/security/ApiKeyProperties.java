package com.icinema.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.api")
public record ApiKeyProperties(
    boolean enabled,
    String header,
    String key
) {
}
