package com.xyz.desensitize.strategy;

import com.github.houbb.sensitive.api.IContext;
import com.github.houbb.sensitive.api.IStrategy;

/**
 * @author dev-sxl
 * date 2020-09-16
 */
public class CustomPasswordStrategy implements IStrategy {

    @Override
    public Object des(Object original, IContext context) {
        return "******";
    }

}
