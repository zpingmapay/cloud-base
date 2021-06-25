package com.xyz.cloud.spel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SpelRelation {
    EQ("=="),
    NE("!="),
    GT(">"),
    GE(">="),
    LE("<="),
    LT("<"),
    IN("in"),
    NIN("not in"),
    HAS_ANY("has a"),
    HAS_NONE("has none"),
    HAS_ALL("has all");

    private String value;
}
