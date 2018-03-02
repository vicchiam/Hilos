package org.example.hilos;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by vicch on 27/02/2018.
 */

public class DownloadImages extends Fragment{

    private Activity actividad;

    private ProgressBar progressBar;
    private LinearLayout layout;

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
        View vista = inflador.inflate(R.layout.download_layout, contenedor, false);

        TextView title = (TextView) vista.findViewById(R.id.primos_title);
        title.setText("Descarga de imagenes");

        layout=(LinearLayout) vista.findViewById(R.id.contenedor_img);

        progressBar = (ProgressBar) vista.findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(4);

        if(savedInstanceState==null) {
            init();
        }

        return vista;
    }

    private void init(){
        Log.e("INI","Iniciando Descarag Imagenes");

        String url1="https://www.bioparcvalencia.es/wp-content/uploads/2017/06/interiores-animal-bioparc-valencia-alcaravan-del-cabo-01.jpg";
        String url2="https://www.bioparcvalencia.es/wp-content/uploads/2017/06/interiores-animal-bioparc-valencia-dril-01.jpg";
        String url3="https://www.bioparcvalencia.es/wp-content/uploads/2017/06/interiores-animal-bioparc-valencia-jirafa-01.jpg";
        String url4="https://www.bioparcvalencia.es/wp-content/uploads/2017/06/interiores-animal-bioparc-valencia-pelicano-rosado-01-min.jpg";

        DownloadImageTask d1=new DownloadImageTask();
        d1.execute(url1);
        DownloadImageTask d2=new DownloadImageTask();
        d2.execute(url2);
        DownloadImageTask d3=new DownloadImageTask();
        d3.execute(url3);
        DownloadImageTask d4=new DownloadImageTask();
        d4.execute(url4);


    }

    @Override
    public void onDestroy() {
        /*
        if(mAsyncTask!=null)
            mAsyncTask.cancel(true);
            */
        super.onDestroy();
    }

    public void setBitmap(Bitmap bitmap){
        progressBar.setProgress(progressBar.getProgress()+1);


        ImageView imageView = new ImageView(this.getContext());
        imageView.setImageBitmap(bitmap);

        layout.addView(imageView);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url=strings[0];

            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            setBitmap(bitmap);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
