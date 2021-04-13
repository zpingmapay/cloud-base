package com.xyz.cloud.spel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SpelRelation {
    EQ("=="), NE("!="), GT(">"), GE(">="), LE("<="), LT("<"), IN("in"), NIN("not in");

    private String value;
}
