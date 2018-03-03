package org.example.hilos.asynctask;

/**
 * Created by vicch on 28/02/2018.
 */

public interface TaskListener {
    void onPreExecute();
    void onProgressUpdate(double progreso);
    void onPostExecute(boolean resultado);
    void onCancelled();
    //void lockScreenOrientation();
    //void unlockScreenOrientation();
}
