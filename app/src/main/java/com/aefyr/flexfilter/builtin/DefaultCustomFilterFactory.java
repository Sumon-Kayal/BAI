package com.aefyr.flexfilter.builtin;

import com.aefyr.flexfilter.applier.CustomFilter;
import com.aefyr.flexfilter.builtin.filter.singlechoice.SingleChoiceFilterConfig;
import com.aefyr.flexfilter.builtin.filter.sort.SortFilterConfig;

public interface DefaultCustomFilterFactory<T> {
    CustomFilter<T> createCustomSingleChoiceFilter(SingleChoiceFilterConfig config);

    CustomFilter<T> createCustomSortFilter(SortFilterConfig config);
}
