package org.example.hilos.asynctask;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vicch on 03/03/2018.
 */

public class ImageSave {

    private static ImageSave INSTANCE = null;

    private List<Bitmap> list;

    private ImageSave(){
        list=new ArrayList<>();
    }

    private synchronized static void createInstance(){
        if(INSTANCE==null){
            INSTANCE=new ImageSave();
        }
    }

    public static ImageSave getInstance(){
        if(INSTANCE==null) createInstance();
        return INSTANCE;
    }

    public List<Bitmap> getList(){
        return list;
    }

    public void add(Bitmap bitmap){
        list.add(bitmap);
    }

    public void clear(){
        list.clear();
    }

}
