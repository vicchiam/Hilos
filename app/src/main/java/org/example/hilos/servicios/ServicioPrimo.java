package org.example.hilos.servicios;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
 * Created by vicch on 03/03/2018.
 */

public class ServicioPrimo extends Fragment implements ResultCallback<Boolean>{

    PrimoService mService;
    boolean mBound = false;

    private EditText inputField, resultField;
    private Button primecheckbutton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflador, @Nullable ViewGroup contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_primos, contenedor, false);

        TextView title = (TextView) vista.findViewById(R.id.primos_title);
        title.setText("Primos Service");

        inputField = (EditText) vista.findViewById(R.id.inputField);
        inputField.setText("100069");
        resultField = (EditText) vista.findViewById(R.id.resultField);
        primecheckbutton = (Button) vista.findViewById(R.id.primecheckbutton);
        primecheckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mService != null) {
                    String num=inputField.getText().toString();
                    long l=Long.parseLong(num);
                    mService.getPrimo(l,ServicioPrimo.this);
                }
                else{
                    Log.e("Primo","Servicio nulo");
                }
            }
        });

        return vista;
    }

    @Override
    public void onResult(Boolean data) {
        resultField.setText(data.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), PrimoService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.e("PrimoService","Start");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PrimoService.LocalBinder binder = (PrimoService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mService= null;
        }
    };

}
