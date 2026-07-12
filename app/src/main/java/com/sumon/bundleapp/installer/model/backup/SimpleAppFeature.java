package com.sumon.bundleapp.installer.model.backup;

import com.sumon.bundleapp.installer.model.common.AppFeature;

public class SimpleAppFeature implements AppFeature {

    private String mText;

    public SimpleAppFeature(String text) {
        mText = text;
    }

    @Override
    public CharSequence toText() {
        return mText;
    }
}
