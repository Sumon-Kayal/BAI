package com.sumon.bundleapp.installer.analytics;

public interface AnalyticsProvider {

    boolean supportsDataCollection();

    boolean isDataCollectionEnabled();

    void setDataCollectionEnabled(boolean enabled);

}
