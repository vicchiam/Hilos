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
import android.widget.TextView;

import org.example.hilos.R;

/**
 * Created by vicch on 03/03/2018.
 */

public class CalculadoraSHA1 extends Fragment implements ResultCallback<String>{

    Sha1HashService mService;
    boolean mBound = false;

    EditText et;
    TextView te;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.calculadora_service, container,false);

        Button queryButton = (Button) vista.findViewById(R.id.hashIt);
        et = (EditText) vista.findViewById(R.id.text);
        te = (TextView) vista.findViewById(R.id.hashResult);

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    Log.e("calculadora",et.getText().toString());
                    mService.getSha1Digest(et.getText().toString(),CalculadoraSHA1.this);
                }
                else{
                    Log.e("Calculadora","Servicio nulo");
                }
            }
        });

        return vista;
    }

    @Override
    public void onResult(String data) {
        Log.e("CalculadoraRES",data);
        te.setText(data);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), Sha1HashService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.e("Calculadora","Start");
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
            Sha1HashService.LocalBinder binder = (Sha1HashService.LocalBinder) service;
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
