package com.gli.floatingvideo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by german on 8/22/15.
 *
 */
public class VideoViewService extends Service {

    private WindowManager wm;
    private LinearLayout ll;
    private Button stop;
    private VideoView video;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        ll = new LinearLayout(getApplicationContext());
        stop = new Button(getApplicationContext());
        video = new VideoView(getApplicationContext());

        ViewGroup.LayoutParams btnParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams videoParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        video.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
        video.setBackgroundColor(Color.argb(66, 0, 0, 255));
        video.setLayoutParams(videoParams);
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                video.start();
            }
        });
        video.start();

        stop.setText("Stop");
        stop.setLayoutParams(btnParams);
        stop.setGravity(Gravity.CENTER);

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.argb(66, 255, 0, 0));
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        final WindowManager.LayoutParams wlp = new WindowManager.LayoutParams(200, 320, WindowManager.LayoutParams.TYPE_PHONE, WindowManager
                .LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        wlp.x = 0;
        wlp.y = 0;
        wlp.gravity = Gravity.CENTER;

        ll.addView(video);

        ll.addView(stop);

        wm.addView(ll, wlp);
        ll.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams updatedParams = wlp;
            int x, y;
            float touchx, touchy;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParams.x;
                        y = updatedParams.y;
                        touchx = motionEvent.getRawX();
                        touchy = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updatedParams.x = (int) (x + (motionEvent.getRawX() - touchx));
                        updatedParams.y = (int) (y + (motionEvent.getRawY() - touchy));
                        wm.updateViewLayout(ll, updatedParams);
                        break;
                    default:
                        break;

                }

                return false;
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeViewImmediate(ll);
                stopSelf();
            }
        });
        video.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (video.isPlaying()) {
                    video.pause();
                } else {
                    video.start();
                }
                return false;
            }
        });
    }
}
