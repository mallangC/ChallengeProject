package com.zerobase.challengeproject.type;

import lombok.Getter;

@Getter
public enum CategoryType {

  COTE("COTE"),
  DRINKING("DRINKING"),
  DIET("DIET");

  private final String description;

  CategoryType(String description) {
    this.description = description;
  }
}
