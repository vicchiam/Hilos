package org.example.hilos;

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
import android.widget.TextView;

/**
 * Created by vicch on 27/02/2018.
 */

public class PrimosInterface extends Fragment implements TaskListener{

    private static final String TAG = Fragment.class.getName();

    private Activity actividad;

    private EditText inputField, resultField;
    private Button primecheckbutton;
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
        title.setText("Primos Interface");

        inputField = (EditText) vista.findViewById(R.id.inputField);
        resultField = (EditText) vista.findViewById(R.id.resultField);
        primecheckbutton = (Button) vista.findViewById(R.id.primecheckbutton);
        primecheckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerPrimecheck();
            }
        });

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
            mAsyncTask = new MyAsyncTask(this);
            mAsyncTask.execute(parameter);
            Log.v(TAG, "Thread " + Thread.currentThread().getId() + ": triggerPrimecheck() termina");
        }
        else {
            Log.v(TAG, "Cancelando test " + Thread.currentThread().getId());
            mAsyncTask.cancel(true);
        }
    }

    @Override
    public void onPreExecute() {
        resultField.setText("");
        primecheckbutton.setText("CANCELAR");
    }

    @Override
    public void onProgressUpdate(double progress) {
        resultField.setText(String.format("%.1f%% completado", progress*100));
    }

    @Override
    public void onPostExecute(boolean resultado) {
        resultField.setText(resultado + "");
        primecheckbutton.setText("¿ES PRIMO?");
    }

    @Override
    public void onCancelled() {
        resultField.setText("Proceso cancelado");
        primecheckbutton.setText("¿ES PRIMO?");
    }

    @Override
    public void lockScreenOrientation() {
        int currentOrientation= getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void unlockScreenOrientation() {
        if(getActivity()!=null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

}
