package com.msa.member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String attributeKey;
    private String email;
    private String name;
    private String profilePicUrl;
    private String provider;

    public static OAuth2Attribute of(String provider, String attributeKey, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" :
                yield ofGoogle(provider, attributeKey, attributes);
            default:
                throw new RuntimeException();
        };
    }

    private static OAuth2Attribute ofGoogle(String provider, String attributeKey, Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .email(String.valueOf(attributes.get("email")))
                .provider(provider)
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }
    public Map<String, Object> convert2Map() {
        return new HashMap<>(Map.of("id", attributeKey,
                "key", attributeKey,
                "email", email,
                "provider", provider));
    }

}
