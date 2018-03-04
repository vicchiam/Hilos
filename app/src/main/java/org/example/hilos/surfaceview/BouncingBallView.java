package org.example.hilos.surfaceview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import org.example.hilos.R;

/**
 * Created by vicch on 04/03/2018.
 */

public class BouncingBallView extends SurfaceView implements SurfaceHolder.Callback{

    private BouncingBallThread bbThread = null;
    public int toolbarH;

    public BouncingBallView(Context context) {
        super(context);
        this.toolbarH=getToolBarHeight();
        if(bbThread!=null) return;
        bbThread = new BouncingBallThread(this, this.toolbarH);
        getHolder().addCallback(this);
        setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(bbThread!=null) {
                    return bbThread.onTouch(event);
                }
                else
                    return false;
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (bbThread.getState() == Thread.State.NEW){
            bbThread.setRunning(true);
            bbThread.start();
        }
        else{
            bbThread=new BouncingBallThread(this, this.toolbarH);
            bbThread.setRunning(true);
            bbThread.start();
        }
        Log.e("Surface","Start");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
        bbThread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean reintentar = true;
        bbThread.setRunning(false);
        while (reintentar) {
            try {
                bbThread.join();
                reintentar = false;
                Log.e("Surface","Destroy");
            }
            catch (InterruptedException e) { }
        }
    }

    public int getToolBarHeight() {
        int[] attrs = new int[] {R.attr.actionBarSize};
        TypedArray ta = getContext().obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

}
