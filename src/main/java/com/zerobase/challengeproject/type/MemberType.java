package com.zerobase.challengeproject.type;

import lombok.Getter;

@Getter
public enum MemberType {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String authority;

    MemberType(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }
}
