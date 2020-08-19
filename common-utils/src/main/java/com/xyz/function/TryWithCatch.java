package com.xyz.function;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TryWithCatch {
    public static void run(VoidConsumer function) {
        try {
            function.accept();
        } catch (Exception e) {
            // keep silence
        }
    }

    public static <T> void run(Consumer<T> function, T t) {
        try {
            function.accept(t);
        } catch (Exception e) {
            // keep silence
        }
    }

    public static <T> T apply(Supplier<T> function, T defaultValue) {
        try {
            return function.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
