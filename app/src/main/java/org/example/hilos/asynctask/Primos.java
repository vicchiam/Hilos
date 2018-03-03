package org.example.hilos.asynctask;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.example.hilos.R;

/**
 * Created by vicch on 27/02/2018.
 */

public class Primos extends Fragment {

    private static final String TAG = Fragment.class.getName();

    private Activity actividad;

    private EditText inputField, resultField;
    private Button primecheckbutton;
    private ProgressBar progressBar;

    private MyAsyncTask mAsyncTask;

    @Override
    public void onAttach(Activity actividad) {
        super.onAttach(actividad);
        this.actividad = actividad;
    }

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_primos, contenedor, false);

        TextView title = (TextView) vista.findViewById(R.id.primos_title);
        title.setText("Primos");

        inputField = (EditText) vista.findViewById(R.id.inputField);
        resultField = (EditText) vista.findViewById(R.id.resultField);
        primecheckbutton = (Button) vista.findViewById(R.id.primecheckbutton);
        primecheckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerPrimecheck();
            }
        });

        progressBar = (ProgressBar) vista.findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        return vista;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "Pausando test " + Thread.currentThread().getId());
        if(mAsyncTask!=null){
            mAsyncTask.cancel(true);
        }
    }

    public void triggerPrimecheck(){
        if(mAsyncTask==null || mAsyncTask.getStatus() != AsyncTask.Status.RUNNING){
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": triggerPrimecheck() comienza");
            long parameter = Long.parseLong(inputField.getText().toString());
            mAsyncTask = new MyAsyncTask();
            mAsyncTask.execute(parameter);
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": triggerPrimecheck() termina");
        }
        else {
            Log.v(TAG, "Cancelando test " + Thread.currentThread().getId());
            mAsyncTask.cancel(true);
        }
    }

    private void lockScreenOrientation() {
        int currentOrientation= getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    private class MyAsyncTask extends AsyncTask<Long, Double, Boolean> {

        @Override
        protected void onPreExecute() {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onPreExecute()");
            resultField.setText("");
            primecheckbutton.setText("CANCELAR");
            lockScreenOrientation();
        }

        @Override
        protected Boolean doInBackground(Long... n) {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": Comienza doInBackground()");
            long numComprobar = n[0];
            if (numComprobar < 2 || numComprobar % 2 == 0)
                return false;
            double limite = Math.sqrt(numComprobar) + 0.0001;
            double progreso = 0;
            for (long factor = 3; factor < limite && !isCancelled(); factor += 2) {
                if (numComprobar % factor == 0)
                    return false;
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
            Double d=progress[0]*100;
            resultField.setText(String.format("%.1f%% completed",d));
            progressBar.setProgress(d.intValue());
        }

        @Override
        protected void onPostExecute(Boolean isPrime) {
            unlockScreenOrientation();
            progressBar.setProgress(progressBar.getMax());
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onPostExecute()");
            resultField.setText(isPrime + "");
            primecheckbutton.setText("¿ES PRIMO?");
        }

        @Override
        protected void onCancelled() {
            if(getActivity()!=null)
                unlockScreenOrientation();
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onCancelled");
            super.onCancelled();
            resultField.setText("Proceso cancelado");
            primecheckbutton.setText("¿ES PRIMO?");
        }



    }

}
