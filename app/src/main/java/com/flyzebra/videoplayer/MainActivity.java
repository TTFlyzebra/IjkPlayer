package com.flyzebra.videoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.flyzebra.videoplayer.view.IFlyViewVideo;

public class MainActivity extends AppCompatActivity {
    public IFlyViewVideo flyViewVideo;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flyViewVideo = findViewById(R.id.ac_main_fp01);
        flyViewVideo.setPlayUrlArr(new String[]{
//                "/sdcard/video/tsy.mp4",
                "http://10.0.0.88/video/tsy.mp4",
                "http://10.0.0.88/video/test.mp4"
        });
        flyViewVideo.setVideoScreenMode(IFlyViewVideo.SHOWMODE_AUTOFULL);
        i++;
        ((View)flyViewVideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flyViewVideo.play();
            }
        });
        flyViewVideo.startPlay();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
