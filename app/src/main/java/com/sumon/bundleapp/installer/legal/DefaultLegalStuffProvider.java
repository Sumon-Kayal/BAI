package com.sumon.bundleapp.installer.legal;

public class DefaultLegalStuffProvider implements LegalStuffProvider {

    private static DefaultLegalStuffProvider sInstance;

    public static synchronized DefaultLegalStuffProvider getInstance() {
        return sInstance != null ? sInstance : new DefaultLegalStuffProvider();
    }

    private DefaultLegalStuffProvider() {
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
        return null;
    }
}
