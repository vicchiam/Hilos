package org.example.hilos.servicios;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
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
 * Created by vicch on 04/03/2018.
 */

public class PrimoBroadCastNotiService extends Service {

    public static final String PRIMO_BROADCAST = "PRIMO_BROADCAST";
    public static final String RESULT = "primo";
    public static final String HANDLED = "intent_handled";

    public static final String TAG="PrimoBroadNotiService";

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    //
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 4;
    private static final int MAX_QUEUE_SIZE = 16;

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE);

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r,"Sha1HashBroadcastService #" + mCount.getAndIncrement());
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    };

    private ThreadPoolExecutor mExecutor;

    public class LocalBinder extends Binder {
        PrimoBroadCastNotiService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PrimoBroadCastNotiService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private void broadcastResult(final String text,final String result) {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler =  new Handler(mainLooper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PRIMO_BROADCAST);
                intent.putExtra(RESULT, result);
                LocalBroadcastManager.getInstance(PrimoBroadCastNotiService.this).
                        sendBroadcastSync(intent);
                boolean handled = intent.getBooleanExtra(HANDLED, false);
                if(!handled){
                    notifyUser(text, result);
                }
            }
        });
    }

    private void notifyUser(final String text,final String digest) {
        String msg = String.format(
                "El numero %s es primo? %s", text,digest);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Es Primo")
                .setContentText(msg);
        // Gets an instance of the NotificationManager service
        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Sets an unique ID for this notification
        nm.notify(text.hashCode(), builder.build());
    }

    void getPrimo(final Long num) {
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
                    broadcastResult(num+"", primo+"");
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
