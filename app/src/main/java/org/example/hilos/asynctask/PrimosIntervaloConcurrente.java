package org.example.hilos.asynctask;

import android.app.Activity;
import android.app.Fragment;
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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by vicch on 27/02/2018.
 */

public class PrimosIntervaloConcurrente extends Fragment{

    public static final String TAG = PrimosIntervaloConcurrente.class.getName();
    private Activity actividad;

    private EditText inputField, inputField2, resultField;
    private Button primecheckbutton;
    private ProgressBar progressBar;

    private List<MyAsyncTask> tasks;

    @Override
    public void onAttach(Activity actividad) {
        super.onAttach(actividad);
        this.actividad = actividad;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_primos, contenedor, false);

        TextView title = (TextView) vista.findViewById(R.id.primos_title);
        title.setText("Primos Intervalo Concurrente");

        inputField = (EditText) vista.findViewById(R.id.inputField);
        inputField2 = (EditText) vista.findViewById(R.id.inputField2);
        inputField2.setVisibility(View.VISIBLE);
        inputField.setText("3");
        inputField2.setText("100");

        resultField = (EditText) vista.findViewById(R.id.resultField);
        primecheckbutton = (Button) vista.findViewById(R.id.primecheckbutton);
        primecheckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerPrimecheck();
            }
        });

        if(tasks!=null) {
            for (MyAsyncTask task : tasks) {
                if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
                    primecheckbutton.setText("CANCELAR");
                }
            }
        }

        progressBar = (ProgressBar) vista.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        return vista;
    }

    @Override
    public void onDestroy() {
        if(tasks!=null){
            for (MyAsyncTask task : tasks){
                if(task!=null)
                    task.cancel(true);
            }
        }
        super.onDestroy();
    }

    public void triggerPrimecheck(){
        boolean running=false;
        if(tasks!=null) {
            for (MyAsyncTask task : tasks) {
                if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
                    running = true;
                }
            }
        }
        if(!running){
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": triggerPrimecheck() comienza");

            Long num1 = Long.parseLong(inputField.getText().toString());
            Long num2 = Long.parseLong(inputField2.getText().toString());
            int cores= this.getNumOfCores();
            long interval=(num2-num1)/cores;
            Log.e("Interval",interval+"");

            tasks=new ArrayList<>();
            if(interval==0){
                MyAsyncTask task=new PrimosIntervaloConcurrente.MyAsyncTask();
                Long[] params=new Long[2];
                params[0]=num1;
                params[1]=num2;
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
                tasks.add(task);
            }
            else {
                for (int i = 0; i < cores; i++) {
                    MyAsyncTask task = new PrimosIntervaloConcurrente.MyAsyncTask();
                    Long[] params = new Long[2];
                    params[0] = num1 + (interval * i);
                    params[1] = num1 + (interval * (i + 1)) -1;
                    if (i == (cores - 1)) {
                        params[1] = num2;
                    }
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
                    tasks.add(task);
                }
            }
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": triggerPrimecheck() termina");
        }
        else {
            Log.v(TAG, "Cancelando test " + Thread.currentThread().getId());
            for (MyAsyncTask task : tasks){
                if(task!=null)
                    task.cancel(true);
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<Long, Double, Boolean> {

        @Override
        protected void onPreExecute() {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onPreExecute()");
            resultField.setText("");
            primecheckbutton.setText("CANCELAR");
        }

        @Override
        protected Boolean doInBackground(Long... n) {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": Comienza doInBackground()");
            long ini=n[0];
            long fin=n[1];
            for(;ini<=fin;ini++) {
                boolean isPrimo=true;
                long numComprobar = ini;
                if (numComprobar < 2 || numComprobar % 2 == 0){
                    isPrimo=false;
                    continue;
                }
                double limite = Math.sqrt(numComprobar) + 0.0001;
                double progreso = 0;
                for (long factor = 3; factor < limite && !isCancelled(); factor += 2) {
                    if (numComprobar % factor == 0){
                        isPrimo=false;
                        break;
                    }
                    if (factor > limite * progreso / 100) {
                        //publishProgress(progreso / 100);
                        progreso += 5;
                    }
                }
                if(isPrimo){
                    publishProgress(numComprobar*1.0);
                }
            }

            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": Finaliza doInBackground()");
            return true;
        }

        @Override
        protected void onProgressUpdate(Double... progress) {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onProgressUpdate()");
            resultField.setText(resultField.getText()+" "+progress[0].intValue()+";");
        }

        @Override
        protected void onPostExecute(Boolean isPrime) {
            progressBar.setProgress(progressBar.getMax());
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onPostExecute()");
            //resultField.setText(resultField.getText()+" FIN");
            primecheckbutton.setText("¿ES PRIMO?");
        }

        @Override
        protected void onCancelled() {
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": onCancelled");
            super.onCancelled();
            resultField.setText("Proceso cancelado");
            primecheckbutton.setText("¿ES PRIMO?");
        }
    }

    private int getNumOfCores() {
        try {
            int i = new File("/sys/devices/system/cpu/")
                    .listFiles(new FileFilter(){
                        public boolean accept(File params){
                            return Pattern.matches("cpu[0-9]", params.getName());
                        }
                    }).length;
            Log.d("CORES",i+"");
            return i;
        }
        catch (Exception e){
            Log.e(TAG, "Error determinando el número de procesadores");
        }
        return 1;
    }

}
