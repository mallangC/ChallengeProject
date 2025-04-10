package com.zerobase.challengeproject.type;

import lombok.Getter;

@Getter
public enum CategoryType {

  COTE("COTE"),
  WATER("WATER"),
  DIET("DIET");

  private final String description;

  CategoryType(String description) {
    this.description = description;
  }
}
