package org.example.hilos.servicios;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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

public class ServicioPrimoBroadcastNotif extends Fragment{

    PrimoBroadCastNotiService mService;
    boolean mBound = false;

    private ServicioPrimoBroadcastNotif.DigestReceiver mReceiver = new ServicioPrimoBroadcastNotif.DigestReceiver();

    private static class DigestReceiver extends BroadcastReceiver {

        private TextView view;

        @Override
        public void onReceive(Context context, Intent intent) {

            if ( view != null ) {
                String result = intent.getStringExtra(PrimoBroadCastNotiService.RESULT);
                intent.putExtra(PrimoBroadCastNotiService.HANDLED, true);
                view.setText(result);
            } else {
                Log.i("PrimoServiceNotif", " ignoring - we're detached");
            }
        }

        public void attach(TextView view) {
            this.view = view;
        }
        public void detach() {
            this.view = null;
        }
    };

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
        title.setText("Primos broadcast notificacion");

        ProgressBar progressBar = (ProgressBar) vista.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        inputField = (EditText) vista.findViewById(R.id.inputField);
        resultField = (EditText) vista.findViewById(R.id.resultField);
        primecheckbutton = (Button) vista.findViewById(R.id.primecheckbutton);
        primecheckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mService != null) {
                    String num=inputField.getText().toString();
                    long l=Long.parseLong(num);
                    mService.getPrimo(l);
                }
                else{
                    Log.e("Primo","Servicio nulo");
                }
            }
        });

        return vista;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), PrimoBroadCastNotiService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mReceiver.attach(resultField);
        IntentFilter filter = new IntentFilter(
                PrimoBroadCastNotiService.PRIMO_BROADCAST);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
        Log.e("PrimoService","Start");
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        mReceiver.detach();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PrimoBroadCastNotiService.LocalBinder binder = (PrimoBroadCastNotiService.LocalBinder) service;
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
