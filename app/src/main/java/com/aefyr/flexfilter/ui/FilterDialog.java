package com.aefyr.flexfilter.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aefyr.flexfilter.config.core.ComplexFilterConfig;
import com.aefyr.flexfilter.config.core.FilterConfig;
import com.aefyr.flexfilter.builtin.filter.singlechoice.SingleChoiceFilterConfig;
import com.aefyr.flexfilter.builtin.filter.singlechoice.SingleChoiceFilterConfigOption;
import com.aefyr.flexfilter.builtin.filter.sort.SortFilterConfig;
import com.aefyr.flexfilter.builtin.filter.sort.SortFilterConfigOption;
import com.sumon.bundleapp.installer.R;

/**
 * Vendored replacement for com.aefyr.flexfilter.ui.FilterDialog. Renders
 * each filter as a titled RadioGroup (plus an ascending/descending toggle
 * for sort filters) built directly in code, rather than a generic
 * RecyclerView + view-holder-factory architecture - this only needs to
 * support the two filter types this app actually uses.
 */
public class FilterDialog extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_CONFIG = "config";

    private ComplexFilterConfig mConfig;
    private OnApplyConfigListener mListener;

    public interface OnApplyConfigListener {
        void onApplyConfig(ComplexFilterConfig config);
    }

    /**
     * @param viewHolderFactory unused - kept for call-site compatibility with
     *                          the original library's API.
     */
    public static FilterDialog newInstance(CharSequence title, ComplexFilterConfig config, Class<?> viewHolderFactory) {
        return newInstance(title, config);
    }

    public static FilterDialog newInstance(CharSequence title, ComplexFilterConfig config) {
        FilterDialog dialog = new FilterDialog();
        Bundle args = new Bundle();
        args.putCharSequence(ARG_TITLE, title);
        args.putSerializable(ARG_CONFIG, config);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnApplyConfigListener) {
            mListener = (OnApplyConfigListener) getParentFragment();
        } else if (context instanceof OnApplyConfigListener) {
            mListener = (OnApplyConfigListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfig = (ComplexFilterConfig) requireArguments().getSerializable(ARG_CONFIG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_filter, container, false);

        TextView title = root.findViewById(R.id.text_filter_dialog_title);
        title.setText(requireArguments().getCharSequence(ARG_TITLE));

        LinearLayout sectionsContainer = root.findViewById(R.id.container_filter_sections);
        List<FilterConfig> filters = mConfig.filters();
        for (FilterConfig filterConfig : filters) {
            if (filterConfig instanceof SingleChoiceFilterConfig) {
                sectionsContainer.addView(buildSingleChoiceSection((SingleChoiceFilterConfig) filterConfig));
            } else if (filterConfig instanceof SortFilterConfig) {
                sectionsContainer.addView(buildSortSection((SortFilterConfig) filterConfig));
            }
        }

        root.findViewById(R.id.button_filter_dialog_apply).setOnClickListener(v -> {
            if (mListener != null)
                mListener.onApplyConfig(mConfig);
            dismiss();
        });

        return root;
    }

    private View buildSingleChoiceSection(SingleChoiceFilterConfig config) {
        Context context = requireContext();
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        TextView label = new TextView(context);
        label.setText(config.name());
        label.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        section.addView(label);

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        List<SingleChoiceFilterConfigOption> options = config.options();
        for (int i = 0; i < options.size(); i++) {
            SingleChoiceFilterConfigOption option = options.get(i);
            RadioButton button = new RadioButton(context);
            button.setText(option.label());
            button.setId(View.generateViewId());
            button.setChecked(option.isSelected());
            radioGroup.addView(button);

            int index = i;
            button.setOnClickListener(v -> {
                config.clearSelection();
                options.get(index).setSelected();
            });
        }

        section.addView(radioGroup);
        return section;
    }

    private View buildSortSection(SortFilterConfig config) {
        Context context = requireContext();
        LinearLayout section = new LinearLayout(context);
        section.setOrientation(LinearLayout.VERTICAL);

        TextView label = new TextView(context);
        label.setText(config.name());
        label.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        section.addView(label);

        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        List<SortFilterConfigOption> options = config.options();
        List<CheckBox> ascendingBoxes = new ArrayList<>();

        for (int i = 0; i < options.size(); i++) {
            SortFilterConfigOption option = options.get(i);

            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);

            RadioButton button = new RadioButton(context);
            button.setText(option.label());
            button.setId(View.generateViewId());
            button.setChecked(option.isSelected());
            button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            radioGroup.addView(button);

            CheckBox ascendingBox = new CheckBox(context);
            ascendingBox.setText("Ascending");
            ascendingBox.setChecked(option.ascending());
            ascendingBoxes.add(ascendingBox);

            row.addView(button);
            row.addView(ascendingBox);

            int index = i;
            button.setOnClickListener(v -> {
                config.clearSelection();
                options.get(index).setSelected();
            });
            ascendingBox.setOnCheckedChangeListener((buttonView, isChecked) -> option.setAscending(isChecked));

            section.addView(row);
        }

        return section;
    }
}
