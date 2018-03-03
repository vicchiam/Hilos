package org.example.hilos.servicios;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.example.hilos.MainActivity;
import org.example.hilos.R;

import java.lang.ref.WeakReference;

/**
 * Created by vicch on 03/03/2018.
 */

public class ServicioMessenger extends Fragment {

    private Messenger mBoundServiceMessenger;
    private boolean mServiceConnected = false;
    private TextView mTimestampText;
    private final Messenger mActivityMessenger = new Messenger( new ActivityHandler(this));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.local_service, container, false);
        mTimestampText = (TextView) vista.findViewById(R.id.timestamptext);
        Button printTimestampButton =(Button) vista.findViewById(R.id.btnPrintTimeStamp);
        Button stopServiceButon = (Button) vista.findViewById(R.id.btnStopService);
        printTimestampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceConnected) {
                    try {
                        Message msg = Message.obtain(null, MessengerService.MSG_GET_TIMESTAMP, 0, 0);
                        msg.replyTo = mActivityMessenger;
                        mBoundServiceMessenger.send(msg);
                    }
                    catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        stopServiceButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceConnected) {
                    getActivity().unbindService(mServiceConnection);
                    mServiceConnected = false;
                }
                Intent intent = new Intent(getActivity(), MessengerService.class);
                getActivity().stopService(intent);
            }
        });

        return vista;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), MessengerService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mServiceConnected) {
            getActivity().unbindService(mServiceConnection);
            mServiceConnected = false;
        }
    }

    private ServiceConnection mServiceConnection =
            new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundServiceMessenger = null;
            mServiceConnected = false;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundServiceMessenger = new Messenger(service);
            mServiceConnected = true;
        }
    };

    static class ActivityHandler extends Handler {
        private final WeakReference<ServicioMessenger> mFragment;

        public ActivityHandler(ServicioMessenger servicioMessenger) {
            mFragment = new WeakReference<ServicioMessenger>(servicioMessenger);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessengerService.MSG_GET_TIMESTAMP: {
                    mFragment.get().mTimestampText.setText(msg.getData().getString("timestamp"));
                }
            }
        }
    }

}
