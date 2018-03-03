package org.example.hilos.servicios;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vicch on 03/03/2018.
 */

public class PrimoServiceBroadcast extends Service {

    private final String TAG="PrimoBroadcastService";

    public static final String PRIMO_BROADCAST = "PRIMO_BROADCAST";
    public static final String RESULT = "primo";

    // Binder given to clients
    private final IBinder mBinder = new PrimoServiceBroadcast.LocalBinder();
    //
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 4;
    private static final int MAX_QUEUE_SIZE = 16;

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r,"PrimoBroadcastService #" + mCount.getAndIncrement());
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    };

    private ThreadPoolExecutor mExecutor;

    public class LocalBinder extends Binder {
        PrimoServiceBroadcast getService() {
            return PrimoServiceBroadcast.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void broadcastResult(boolean result) {
        Intent intent = new Intent(PRIMO_BROADCAST);
        intent.putExtra(RESULT, result+"");
        LocalBroadcastManager.getInstance(this).
                sendBroadcast(intent);
        Log.e("PrimoBroad", result+"");
    }

    void getPrimo(final Long num, ResultCallback<Boolean> callback) {
        final WeakReference<ResultCallback<Boolean>> ref = new WeakReference<ResultCallback<Boolean>>(callback);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Hashing text " + num + " on Thread " + Thread.currentThread().getName());
                try { // Execute the Long Running Computation
                    boolean primo=true;
                    long numComprobar = num;
                    if (numComprobar < 2 || numComprobar % 2 == 0){
                        primo=false;
                    }
                    else{
                        double limite = Math.sqrt(numComprobar) + 0.0001;
                        double progreso = 0;
                        for (long factor = 3; factor < limite; factor += 2) {
                            if (numComprobar % factor == 0){
                                primo=false;
                                break;
                            }
                        }
                    }
                    Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": Finaliza doInBackground()");
                    broadcastResult(primo);
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

}
