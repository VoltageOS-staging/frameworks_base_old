package com.android.internal.gmscompat;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static android.app.compat.gms.GmsCompat.appContext;

// Unprivileged app that is performing a screen capture is required by the OS to run
// a foreground service with FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
public class GmcMediaProjectionService extends Service {
    private static final String TAG = "GmcMediaProjService";

    private static final ArrayMap<String, CountDownLatch> latches = new ArrayMap<>();

    public static void start() {
        if (Thread.currentThread() == appContext().getMainLooper().getThread()) {
            // otherwise, latch.await() below would deadlock
            throw new IllegalStateException("should never be called from the main thread");
        }

        String id = UUID.randomUUID().toString();
        var latch = new CountDownLatch(1);
        synchronized (latches) {
            latches.put(id, latch);
        }
        Intent intent = intent().setIdentifier(id);
        Log.d(TAG, "start " + id);
        appContext().startForegroundService(intent);
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void stop() {
        Log.d(TAG, "stop");
        appContext().stopService(intent());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand " + intent);

        // Ensure startForeground() is called as soon as possible
        Notification n;
        try {
            IGms2Gca gms2Gca = GmsCompatApp.iGms2Gca();
            if (gms2Gca != null) {
                n = gms2Gca.getMediaProjectionNotification();
            } else {
                Log.e(TAG, "iGms2Gca is null, unable to get media projection notification");
                stopSelf();
                return START_NOT_STICKY;
            }
        } catch (RemoteException e) {
            throw GmsCompatApp.callFailed(e);
        }

        startForeground(GmsCoreConst.NOTIF_ID_MEDIA_PROJECTION_SERVICE, n);

        String id = intent.getIdentifier();
        CountDownLatch latch;

        synchronized (latches) {
            latch = latches.remove(id);
        }
        if (latch != null) {
            latch.countDown();
        } else {
            Log.e(TAG, "missing latch");
        }

        return START_NOT_STICKY;
    }

    private static Intent intent() {
        return new Intent(appContext(), GmcMediaProjectionService.class);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
