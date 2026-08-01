package com.aefyr.flexfilter.applier;

import java.util.List;

/**
 * A single filter unit. Implementations override one of:
 *  - filterSimple(item): return true to EXCLUDE the item (predicate-style)
 *  - filterComplex(list): return a transformed list (used for sorting)
 * Defaults are no-ops so anonymous implementations only need to override
 * whichever one they actually use, matching the original library's API.
 */
public interface CustomFilter<T> {
    default boolean filterSimple(T item) {
        return false;
    }

    default List<T> filterComplex(List<T> list) {
        return list;
    }
}
