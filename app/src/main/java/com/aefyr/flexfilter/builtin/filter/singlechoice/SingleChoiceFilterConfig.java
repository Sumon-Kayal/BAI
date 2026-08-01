package com.aefyr.flexfilter.builtin.filter.singlechoice;

import java.util.ArrayList;
import java.util.List;

import com.aefyr.flexfilter.config.core.FilterConfig;

public class SingleChoiceFilterConfig implements FilterConfig {
    private final String id;
    private final CharSequence name;
    private final List<SingleChoiceFilterConfigOption> optionList = new ArrayList<>();

    public SingleChoiceFilterConfig(String id, CharSequence name) {
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

    public SingleChoiceFilterConfig addOption(String optionId, CharSequence label) {
        optionList.add(new SingleChoiceFilterConfigOption(optionId, label));
        return this;
    }

    public List<SingleChoiceFilterConfigOption> options() {
        return optionList;
    }

    /** Clears selection on all options - needed before selecting a new one interactively. */
    public void clearSelection() {
        for (SingleChoiceFilterConfigOption option : optionList) {
            option.setUnselected();
        }
    }

    public SingleChoiceFilterConfigOption getSelectedOption() {
        for (SingleChoiceFilterConfigOption option : optionList) {
            if (option.isSelected())
                return option;
        }
        return optionList.get(0);
    }
}
