package com.sumon.bundleapp.installer.installerx.resolver.urimess;

import android.content.Context;

/**
 * Factory that creates a uri host, implementations must have an empty constructor
 */
public interface UriHostFactory {

    UriHost createUriHost(Context context);

}
