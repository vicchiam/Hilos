package org.example.hilos.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.service.carrier.CarrierMessagingService;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vicch on 03/03/2018.
 */

public class Sha1HashService extends Service{

    private final IBinder mBinder = new LocalBinder();
    private final String TAG="Sha1HashService";
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 4;
    private static final int MAX_QUEUE_SIZE = 16;
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE);
    private static final ThreadFactory sThreadFactory=new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "SHA1HashService #" + mCount.getAndIncrement());
            t.setPriority(Thread.MIN_PRIORITY); return t;
        }
    };
    private ThreadPoolExecutor mExecutor;

    public class LocalBinder extends Binder {
        Sha1HashService getService() {
            return Sha1HashService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void postResultOnUI(final String result, final WeakReference<ResultCallback<String>> callback) {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = new Handler(mainLooper);
        handler.post(new Runnable() {
            @Override public void run() {
                if (callback.get() != null) {
                    callback.get().onResult(result);
                }
            }
        });
    }

    void getSha1Digest(final String text, ResultCallback<String> callback) {
        final WeakReference<ResultCallback<String>> ref = new WeakReference<ResultCallback<String>>(callback);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Hashing text " + text + " on Thread " + Thread.currentThread().getName());
                try { // Execute the Long Running Computation
                    final String result = SHA1(text);
                    Log.i(TAG, "Hash result for " + text + " is " + result);
                    // Execute the Runnable on UI Thread
                    postResultOnUI(result, ref);
                }
                catch (Exception e) {
                    Log.e(TAG, "Hash failed", e);
                }
            }
        }; // Submit the Runnable on the ThreadPool
        mExecutor.execute(runnable);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Starting Hashing Service");
        super.onCreate();
        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE, 5, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
        mExecutor.prestartAllCoreThreads();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping Hashing Service");
        super.onDestroy();
        mExecutor.shutdown();
    }

    public String SHA1(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            }
            while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
