package com.aefyr.flexfilter.builtin.filter.sort;

import java.util.ArrayList;
import java.util.List;

import com.aefyr.flexfilter.config.core.FilterConfig;

public class SortFilterConfig implements FilterConfig {
    private final String id;
    private final CharSequence name;
    private final List<SortFilterConfigOption> optionList = new ArrayList<>();

    public SortFilterConfig(String id, CharSequence name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public CharSequence name() {
        return name;
    }

    public SortFilterConfig addOption(String optionId, CharSequence label) {
        optionList.add(new SortFilterConfigOption(optionId, label));
        return this;
    }

    public List<SortFilterConfigOption> options() {
        return optionList;
    }

    /** Clears selection on all options - needed before selecting a new one interactively. */
    public void clearSelection() {
        for (SortFilterConfigOption option : optionList) {
            option.setUnselected();
        }
    }

    public SortFilterConfigOption getSelectedOption() {
        for (SortFilterConfigOption option : optionList) {
            if (option.isSelected())
                return option;
        }
        return optionList.get(0);
    }
}
