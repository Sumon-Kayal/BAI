package com.sumon.bundleapp.installer.platform;

import java.io.File;

/**
 * Generation-agnostic description of an internal (non-SAF) file/folder picker request.
 * <p>
 * Mirrors com.github.angads25.filepicker.model.DialogProperties field-for-field so
 * {@code PlatformFilePicker.createInternalPicker()} can translate it 1:1, without exposing
 * that vendored library's types to shared (src/main) code. Only Legacy's implementation ever
 * touches the real library — see BAI-ROADMAP-AND-TODO.md, FilePicker architecture.
 * <p>
 * {@link #offset}, {@link #errorDir} and {@link #extensions} default to {@code null}, meaning
 * "use the underlying picker's own default" rather than a value this class has to duplicate.
 */
public class InternalPickerRequest {

    public enum SelectionMode { SINGLE, MULTI }

    public enum SelectionType { FILE, DIRECTORY }

    // Mirrors com.github.angads25.filepicker.model.DialogConfigs.SORT_BY_*
    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_LAST_MODIFIED = 1;
    public static final int SORT_BY_SIZE = 2;

    // Mirrors com.github.angads25.filepicker.model.DialogConfigs.SORT_ORDER_*
    public static final int SORT_ORDER_NORMAL = 0;
    public static final int SORT_ORDER_REVERSE = 1;

    public final String tag;
    public final String title;
    public final SelectionMode selectionMode;
    public final SelectionType selectionType;
    public final File root;

    public File offset;
    public File errorDir;
    public String[] extensions;
    public int sortBy = SORT_BY_NAME;
    public int sortOrder = SORT_ORDER_NORMAL;

    public InternalPickerRequest(String tag, String title, SelectionMode selectionMode, SelectionType selectionType, File root) {
        this.tag = tag;
        this.title = title;
        this.selectionMode = selectionMode;
        this.selectionType = selectionType;
        this.root = root;
    }
}
