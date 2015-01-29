package com.example.testLib;

import android.app.Application;
import android.widget.Toast;
import com.firefly.walkietalkie.IErrorListener;
import com.firefly.walkietalkie.IPlayTalkieListener;
import com.firefly.walkietalkie.Walkietalkie;

/**
 * Created by XuanTrung on 8/21/2014.
 */
public class myapp extends Application {
    public static Walkietalkie walkietalkie;

    @Override
    public void onCreate() {
        super.onCreate();
        //Set listener error walkietalkie
        walkietalkie.setListenerError(new IErrorListener() {
            @Override
            public void onError(String error) {
                Toast.makeText(getBaseContext(), "Lỗi walkietalkie:" + error, Toast.LENGTH_LONG).show();
            }
        });
        //Set listener play walkietalkie
        walkietalkie.setListenerPlayTalkie(new IPlayTalkieListener() {
            @Override
            public void onPlayTalkie(String staff_id) {
                Toast.makeText(getBaseContext(), "Play from staffid:" + staff_id, Toast.LENGTH_LONG).show();
            }
        });
    }
}
