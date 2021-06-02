package com.xyz.utils;

import org.hibernate.validator.constraints.Range;

public interface Randomable {
    interface ByPriority {
        int MAX_PRIORITY = 1;
        int MIN_PRIORITY = 10;

        @Range(min = MAX_PRIORITY, max = MIN_PRIORITY)
        int getPriority();
    }

    interface ByWeight {
        @Range(min = 1, max = 100)
        int getWeight();
    }
}
