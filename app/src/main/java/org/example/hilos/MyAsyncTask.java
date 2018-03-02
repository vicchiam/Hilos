package org.example.hilos;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by vicch on 28/02/2018.
 */

public class MyAsyncTask extends AsyncTask<Long, Double, Boolean> {

    private static final String TAG = MyAsyncTask.class.getName();
    private final TaskListener listener;

    public MyAsyncTask(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onPreExecute()");
        listener.onPreExecute();
        //listener.lockScreenOrientation();
    }

    @Override
    protected Boolean doInBackground(Long... n) {
        Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": Comienza doInBackground()");
        long numComprobar = n[0];
        if (numComprobar < 2 || numComprobar % 2 == 0) return false;
        double limite = Math.sqrt(numComprobar) + 0.0001;
        double progreso = 0;
        for (long factor = 3; factor < limite && !isCancelled(); factor += 2) {
            if (numComprobar % factor == 0) return false;
            if (factor > limite * progreso / 100) {
                publishProgress(progreso / 100);
                progreso += 5;
            }
        }

        Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": Finaliza doInBackground()");
        return true;
    }

    @Override
    protected void onProgressUpdate(Double... progress) {
        Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onProgressUpdate()");
        listener.onProgressUpdate(progress[0]);
    }

    @Override
    protected void onPostExecute(Boolean isPrime) {
        //listener.unlockScreenOrientation();
        Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onPostExecute()");
        listener.onPostExecute(isPrime);
    }

    @Override
    protected void onCancelled() {
        //listener.unlockScreenOrientation();
        Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onCancelled");
        listener.onCancelled();
        super.onCancelled();
    }
}
