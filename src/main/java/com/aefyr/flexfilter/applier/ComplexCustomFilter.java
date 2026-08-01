package com.aefyr.flexfilter.applier;

import java.util.ArrayList;
import java.util.List;

import com.aefyr.flexfilter.builtin.DefaultCustomFilterFactory;
import com.aefyr.flexfilter.config.core.ComplexFilterConfig;
import com.aefyr.flexfilter.config.core.FilterConfig;
import com.aefyr.flexfilter.builtin.filter.singlechoice.SingleChoiceFilterConfig;
import com.aefyr.flexfilter.builtin.filter.sort.SortFilterConfig;

public class ComplexCustomFilter<T> {
    private final List<CustomFilter<T>> filters;

    private ComplexCustomFilter(List<CustomFilter<T>> filters) {
        this.filters = filters;
    }

    public List<T> apply(List<T> items) {
        List<T> result = new ArrayList<>();
        outer:
        for (T item : items) {
            for (CustomFilter<T> filter : filters) {
                if (filter.filterSimple(item))
                    continue outer;
            }
            result.add(item);
        }

        for (CustomFilter<T> filter : filters) {
            result = new ArrayList<>(filter.filterComplex(result));
        }

        return result;
    }

    public static class Builder<T> {
        private final List<CustomFilter<T>> filters = new ArrayList<>();

        public Builder<T> with(ComplexFilterConfig config, DefaultCustomFilterFactory<T> factory) {
            for (FilterConfig filterConfig : config.filters()) {
                if (filterConfig instanceof SingleChoiceFilterConfig) {
                    filters.add(factory.createCustomSingleChoiceFilter((SingleChoiceFilterConfig) filterConfig));
                } else if (filterConfig instanceof SortFilterConfig) {
                    filters.add(factory.createCustomSortFilter((SortFilterConfig) filterConfig));
                }
            }
            return this;
        }

        public Builder<T> add(CustomFilter<T> filter) {
            filters.add(filter);
            return this;
        }

        public ComplexCustomFilter<T> build() {
            return new ComplexCustomFilter<>(filters);
        }
    }
}
