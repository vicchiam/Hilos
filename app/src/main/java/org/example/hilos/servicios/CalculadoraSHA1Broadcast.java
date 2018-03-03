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
import android.widget.TextView;

import org.example.hilos.R;

/**
 * Created by vicch on 03/03/2018.
 */

public class CalculadoraSHA1Broadcast extends Fragment {

    Sha1HashBroadcastService mService;
    boolean mBound = false;

    private DigestReceiver mReceiver = new DigestReceiver();

    private static class DigestReceiver extends BroadcastReceiver {

        private TextView view;

        @Override
        public void onReceive(Context context, Intent intent) {

            if ( view != null ) {
                String result = intent.getStringExtra(Sha1HashBroadcastService.RESULT);
                view.setText(result);
            } else {
                Log.i("Sha1HashService", " ignoring - we're detached");
            }
        }

        public void attach(TextView view) {
            this.view = view;
        }
        public void detach() {
            this.view = null;
        }
    };

    EditText et;
    TextView textResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.calculadora_service, container,false);

        textResult = (TextView) vista.findViewById(R.id.hashResult);
        et = (EditText) vista.findViewById(R.id.text);

        Button queryButton = (Button) vista.findViewById(R.id.hashIt);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mService != null ) {
                    mService.getSha1Digest(et.getText().toString());
                }
            }
        });

        return vista;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(getActivity(), Sha1HashBroadcastService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mReceiver.attach(textResult);
        IntentFilter filter = new IntentFilter(
                Sha1HashBroadcastService.SHA1_BROADCAST);
        LocalBroadcastManager.getInstance(getActivity()).
                registerReceiver(mReceiver, filter);
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
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Sha1HashBroadcastService.LocalBinder binder = (Sha1HashBroadcastService.LocalBinder) service;
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
