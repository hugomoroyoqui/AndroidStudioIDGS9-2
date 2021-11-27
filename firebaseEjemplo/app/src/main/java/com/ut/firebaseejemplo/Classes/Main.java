package com.ut.firebaseejemplo.Classes;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try{

            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", "hugo-moroyoqui");
            MediaManager.init(this, config);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
