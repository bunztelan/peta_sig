package com.example.peta.helper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class UploadHelper {
    private static UploadHelper mInstance;
    private RequestQueue mrequestQueue;
    private static Context mcontext;


    private  UploadHelper(Context context){
        mcontext = context;
        mrequestQueue = getrequestqueue();
    }

    private RequestQueue getrequestqueue(){
        if(mrequestQueue == null){
            mrequestQueue = Volley.newRequestQueue(mcontext.getApplicationContext());
        }

        return mrequestQueue;
    }


    public static synchronized UploadHelper getmInstance(Context context){
        if(mInstance == null){
            mInstance = new UploadHelper(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getrequestqueue().add(request);
    }





}