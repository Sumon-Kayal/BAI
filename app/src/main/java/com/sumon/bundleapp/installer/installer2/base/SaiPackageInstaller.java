package com.sumon.bundleapp.installer.installer2.base;

import com.sumon.bundleapp.installer.installer2.base.model.SaiPiSessionParams;
import com.sumon.bundleapp.installer.installer2.base.model.SaiPiSessionState;

import java.util.List;

public interface SaiPackageInstaller {

    String createSession(SaiPiSessionParams params);

    void enqueueSession(String sessionId);

    void registerSessionObserver(SaiPiSessionObserver observer);

    void unregisterSessionObserver(SaiPiSessionObserver observer);

    List<SaiPiSessionState> getSessions();

}
