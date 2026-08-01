package com.aefyr.flexfilter.builtin.filter.sort;

public class SortFilterConfigOption implements java.io.Serializable {
    private final String id;
    private final CharSequence label;
    private boolean selected;
    private boolean ascending = true;

    public SortFilterConfigOption(String id, CharSequence label) {
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

    public boolean ascending() {
        return ascending;
    }

    public void setAscending(boolean value) {
        ascending = value;
    }
}
