package com.xyz.function;

import java.util.function.Consumer;

@FunctionalInterface
public interface VoidConsumer extends Consumer<Void> {
    @Override
    default void accept(Void t) {
        accept();
    }

    void accept();
}
