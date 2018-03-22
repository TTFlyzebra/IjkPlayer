package com.flyzebra.videoplayer.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.flyzebra.videoplayer.utils.FlyLog;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Author: FlyZebra
 * Time: 18-1-28 下午3:18.
 * Discription: This is FlyPlayer
 */

public class FlyTextureViewVideo extends TextureView implements IFlyViewVideo, TextureView.SurfaceTextureListener {
    private int videoWidth, videoHeight;
    private int viewWidth, viewHeight;
    private String urlArr[];
    private String adUrlArr[];
    private IMediaPlayer iMediaPlayer;
    private int currentPlayAdItem = 0;
    private int currentPlayItem = 0;
    private boolean isLoop = true;
    private int playstate = PLAY_STATUS_NO;
    private int showMode = SHOWMODE_AUTOFULL;


    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            FlyLog.d();
            return false;
        }
    };
    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            FlyLog.d("i=%d,i1=%d,i2=%d,i3=%d", i, i1, i2, i3);
            videoWidth = i;
            videoHeight = i1;
            setVideoScreenMode();
        }
    };

    private void setVideoScreenMode() {
        if (viewWidth == 0 || viewHeight == 0 || videoWidth == 0 || videoHeight == 0) {
            return;
        }
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        int showWidth = videoWidth;
        int showHeight = videoHeight;
        switch (showMode) {
            case SHOWMODE_AUTOFULL:
                float scale = (float) videoHeight / (float) videoWidth > (float) viewHeight / (float) viewWidth
                        ? (float) viewHeight / (float) videoHeight : (float) viewWidth / (float) videoWidth;
                showWidth = (int) (videoWidth * scale);
                showHeight = (int) (videoHeight * scale);
                left = (viewWidth - showWidth) / 2;
                top = (viewHeight - showHeight) / 2;
                right = showWidth + left;
                bottom = showHeight + top;
                FlyLog.d("AUTOFULL:layout left=%d,top=%d,right=%d,bottom=%d", left, top, right, bottom);
                layout(left, top, right, bottom);
                break;
            case SHOWMODE_AUTOSIZE:
                if (videoWidth > viewWidth) {
                    showHeight = (int) ((float) videoHeight / ((float) videoWidth / (float) viewWidth));
                    showWidth = viewWidth;
                }
                if (videoHeight > viewWidth) {
                    showWidth = (int) ((float) videoWidth / ((float) videoHeight / (float) viewHeight));
                    showHeight = viewHeight;
                }
                left = (viewWidth - showWidth) / 2;
                top = (viewHeight - showHeight) / 2;
                right = showWidth + left;
                bottom = showHeight + top;
                FlyLog.d("AUTOSIZE:layout left=%d,top=%d,right=%d,bottom=%d", left, top, right, bottom);
                layout(left, top, right, bottom);
                break;
            case SHOWMODE_ALLFULL:
            default:
                layout(0, 0, viewWidth, viewHeight);
                break;
        }
    }

    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            FlyLog.d();
            if (!isLoop) {
                return;
            }
            switch (playstate) {
                case PLAY_STATUS_AD:
                    currentPlayItem++;
                    playVideo();
                    break;
                case PLAY_STATUS_NO:
                case PLAY_STATUS_VIDEO:
                    currentPlayAdItem++;
                    playAdVideo();
                    break;
                default:
                    break;

            }
        }
    };
    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            FlyLog.d(iMediaPlayer + "");
            iMediaPlayer.start();
        }
    };
    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            FlyLog.d("i=%d,i1=%d", i, i1);
            return false;
        }
    };
    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
//            FlyLog.d();
        }
    };
    private Surface mSurface = null;

    public FlyTextureViewVideo(@NonNull Context context) {
        this(context, null);
    }

    public FlyTextureViewVideo(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlyTextureViewVideo(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FlyTextureViewVideo(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setSurfaceTextureListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public FlyTextureViewVideo setPlayUrlArr(String[] urlArr) {
        this.urlArr = urlArr;
        return this;
    }

    @Override
    public FlyTextureViewVideo setPlayAdUrlArr(String[] adUrlArr) {
        this.adUrlArr = adUrlArr;
        return this;
    }

    @Override
    public IFlyViewVideo setLoop(boolean isLoop) {
        this.isLoop = isLoop;
        return this;
    }

    @Override
    public void startPlay() {
        playVideo();
    }

    @Override
    public void setVideoScreenMode(int mode) {
        showMode = mode;
        setVideoScreenMode();
    }

    @Override
    public void play() {
        if (iMediaPlayer != null) {
            iMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (iMediaPlayer != null) {
            iMediaPlayer.pause();
        }
    }

    private void playVideo() {
        if (urlArr == null || urlArr.length == 0) {
            return;
        }
        try {
            currentPlayItem = currentPlayItem % urlArr.length;
            if (iMediaPlayer == null) {
                initMediaPlayer();
            } else {
                iMediaPlayer.reset();
                iMediaPlayer.setSurface(mSurface);
            }
            iMediaPlayer.setDataSource(urlArr[currentPlayItem]);
            iMediaPlayer.prepareAsync();
            playstate = PLAY_STATUS_VIDEO;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playAdVideo() {
        if (adUrlArr == null || adUrlArr.length == 0) {
            currentPlayItem++;
            playVideo();
            return;
        }
        try {
            currentPlayAdItem = currentPlayAdItem % adUrlArr.length;
            if (iMediaPlayer == null) {
                initMediaPlayer();
            } else {
                iMediaPlayer.reset();
            }
            iMediaPlayer.setDataSource(adUrlArr[currentPlayAdItem]);
            iMediaPlayer.prepareAsync();
            playstate = PLAY_STATUS_AD;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        FlyLog.d("width=%d,heigh=%d", width, height);
        if (iMediaPlayer == null) {
            initMediaPlayer();
        }
        mSurface = new Surface(surface);
        iMediaPlayer.setSurface(mSurface);
        if(iMediaPlayer.getDuration()!=0){
            iMediaPlayer.start();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        FlyLog.d("width=%d,heigh=%d", width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        FlyLog.d();
        if(iMediaPlayer.isPlaying()){
            iMediaPlayer.pause();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//        FlyLog.d();
    }

    private void initMediaPlayer() {
        iMediaPlayer = new IjkMediaPlayer();
        iMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        iMediaPlayer.setScreenOnWhilePlaying(true);
        iMediaPlayer.setOnPreparedListener(onPreparedListener);
        iMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        iMediaPlayer.setOnCompletionListener(onCompletionListener);
        iMediaPlayer.setOnErrorListener(onErrorListener);
        iMediaPlayer.setOnInfoListener(onInfoListener);
        iMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(iMediaPlayer!=null) {
            iMediaPlayer.release();
            iMediaPlayer = null;
        }
    }
}
