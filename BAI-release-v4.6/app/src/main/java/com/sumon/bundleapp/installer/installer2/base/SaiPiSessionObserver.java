package com.sumon.bundleapp.installer.installer2.base;

import com.sumon.bundleapp.installer.installer2.base.model.SaiPiSessionState;

public interface SaiPiSessionObserver {

    void onSessionStateChanged(SaiPiSessionState state);

}
