package com.aefyr.flexfilter.builtin.filter.singlechoice;

import java.util.ArrayList;
import java.util.List;

import com.aefyr.flexfilter.config.core.FilterConfig;

public class SingleChoiceFilterConfigOption implements java.io.Serializable {
    private final String id;
    private final CharSequence label;
    private boolean selected;

    public SingleChoiceFilterConfigOption(String id, CharSequence label) {
        this.id = id;
        this.label = label;
    }

    public String id() {
        return id;
    }

    public CharSequence label() {
        return label;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected() {
        selected = true;
    }

    public void setUnselected() {
        selected = false;
    }
}
