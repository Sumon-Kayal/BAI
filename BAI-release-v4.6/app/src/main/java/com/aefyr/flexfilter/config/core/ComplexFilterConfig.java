package com.aefyr.flexfilter.config.core;

import java.util.List;

public class ComplexFilterConfig implements java.io.Serializable {
    private final List<FilterConfig> filterList;

    public ComplexFilterConfig(List<FilterConfig> filterList) {
        this.filterList = filterList;
    }

    public List<FilterConfig> filters() {
        return filterList;
    }
}
