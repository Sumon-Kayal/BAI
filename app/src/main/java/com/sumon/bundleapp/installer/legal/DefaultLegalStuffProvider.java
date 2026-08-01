package com.sumon.bundleapp.installer.legal;

import android.content.Context;

public class DefaultLegalStuffProvider implements LegalStuffProvider {

    private static DefaultLegalStuffProvider sInstance;

    private Context mContext;

    public static synchronized DefaultLegalStuffProvider getInstance(Context context) {
        return sInstance != null ? sInstance : new DefaultLegalStuffProvider(context);
    }

    private DefaultLegalStuffProvider(Context context) {
        mContext = context.getApplicationContext();

        sInstance = this;
    }

    @Override
    public boolean hasPrivacyPolicy() {
        return false;
    }

    @Override
    public String getPrivacyPolicyUrl() {
        return null;
    }

    @Override
    public boolean hasEula() {
        return true;
    }

    @Override
    public String getEulaUrl() {
        return "https://raw.githubusercontent.com/Sumon-Kayal/BAI/1ce832e49def6b67503400353f7ddb4554f2b8a8/EULA.md";
    }
}
