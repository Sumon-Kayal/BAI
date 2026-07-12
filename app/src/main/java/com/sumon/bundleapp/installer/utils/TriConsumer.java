package com.sumon.bundleapp.installer.utils;

@FunctionalInterface
public interface TriConsumer<A, T, U> {
    void accept(A a, T t, U u);
}
