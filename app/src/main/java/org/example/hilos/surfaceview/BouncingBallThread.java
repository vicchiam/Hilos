package org.example.hilos.surfaceview;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import org.example.hilos.R;

/**
 * Created by vicch on 04/03/2018.
 */

public class BouncingBallThread extends Thread {

    static final long FPS = 10;
    private SurfaceView superfView;
    private int width, height;
    private boolean running = false;
    private int pos_x = -1;
    private int pos_y = -1;
    private int xVelocidad = 10;
    private int yVelocidad = 5;
    private BitmapDrawable pelota;

    private int toolbarHeigth;

    public int touched_x, touched_y;
    public boolean touched;

    public BouncingBallThread(SurfaceView view, int toolbarHeight) {
        this.superfView = view;
        this.toolbarHeigth=toolbarHeight;
        // Coloca una imagen de tu elección
        pelota = (BitmapDrawable) view.getContext(). getResources().getDrawable(R.drawable.pelota);
    }

    public void setRunning(boolean run) { running = run; }

    @Override public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas canvas = null;
            startTime = System.currentTimeMillis();
            try {
                // Bloqueamos el canvas de la superficie para dibujarlo
                canvas = superfView.getHolder().lockCanvas();
                // Sincronizamos el método draw() de la superficie para
                // que se ejecute como un bloque
                synchronized (superfView.getHolder()) {
                    if (canvas != null)
                        doDraw(canvas);
                }
            }
            finally {
                // Liberamos el canvas de la superficie desbloqueándolo
                if (canvas != null) {
                    superfView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            // Tiempo que debemos parar la ejecución del hilo
            sleepTime = ticksPS - System.currentTimeMillis() - startTime;
            // Paramos la ejecución del hilo
            try {
                if (sleepTime > 0) sleep(sleepTime);
                else sleep(10);
            }
            catch (Exception e) { }
        }
    }

    protected void doDraw(Canvas canvas) {
        //int diameter=pelota.getBitmap().getWidth();
        int radio=40;
        if (pos_x < 0 && pos_y < 0) {
            pos_x = this.width / 2;
            pos_y = this.height / 2;
        }
        else {
            pos_x += xVelocidad;
            pos_y += yVelocidad;
            if (touched && touched_x > (pos_x - radio)
                    && touched_x < (pos_x + radio)
                    && touched_y > (pos_y - radio)
                    && touched_y < (pos_y + radio)) {
                touched = false;
                xVelocidad = xVelocidad * -1;
                yVelocidad = yVelocidad * -1;
            }
            else if(touched){
                if(xVelocidad!=0){
                    xVelocidad=0;
                    yVelocidad=0;
                }
                else{
                    xVelocidad=10*((touched_x-pos_x>=0)?1:-1);
                    yVelocidad=5*((touched_y-pos_y>=0)?1:-1);
                }
            }
            if ((pos_x > this.width - radio) || (pos_x- (radio) < 0)) {
                xVelocidad = xVelocidad * -1;
            }
            if ((pos_y > this.height - radio) || (pos_y -(radio) < 0)) {
                yVelocidad = yVelocidad * -1;
            }
        }

        canvas.drawColor(Color.LTGRAY);
        //canvas.drawBitmap(pelota.getBitmap(), pos_x, pos_y, null);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        canvas.drawCircle(pos_x, pos_y,radio,paint);
    }

    public void setSurfaceSize(int width, int height) {
        // Sincronizamos superficie para que ningún proceso pueda acceder
        synchronized (superfView) {
            this.width = width;
            this.height = height-toolbarHeigth;
        }
    }

    public boolean onTouch(MotionEvent event) {
        touched_x = (int) event.getX();
        touched_y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touched = true;
                break;
            case MotionEvent.ACTION_MOVE:
                touched = false;
                break;
            case MotionEvent.ACTION_UP:
                touched = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                touched = false;
                Log.e("TouchEven ACTION_CANCEL", " ");
                break;
            case MotionEvent.ACTION_OUTSIDE:
                touched = false;
                break;
                default:
        }
        return true;
    }

}
