package com.sumon.bundleapp.installer.legal;

public interface LegalStuffProvider {

    boolean hasPrivacyPolicy();

    String getPrivacyPolicyUrl();

    boolean hasEula();

    String getEulaUrl();

}
