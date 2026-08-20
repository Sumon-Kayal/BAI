package com.sumon.bundleapp.installer.ui.activities;

import com.sumon.bundleapp.installer.R;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sumon.bundleapp.installer.adapters.LicensesAdapter;
import com.sumon.bundleapp.installer.utils.InsetsUtils;
import com.sumon.bundleapp.installer.viewmodels.LicensesViewModel;

public class LicensesActivity extends ThemedActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);

        RecyclerView recyclerView = findViewById(R.id.rv_licenses);

        InsetsUtils.applySystemBarInsetsAsPadding(recyclerView);
        recyclerView.setClipToPadding(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 16);

        LicensesAdapter adapter = new LicensesAdapter(this);
        recyclerView.setAdapter(adapter);

        LicensesViewModel viewModel = new ViewModelProvider(this).get(LicensesViewModel.class);
        viewModel.getLicenses().observe(this, adapter::setLicenses);
    }
}
