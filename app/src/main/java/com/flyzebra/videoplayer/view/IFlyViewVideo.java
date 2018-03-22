package com.flyzebra.videoplayer.view;

/**
 * Author: FlyZebra
 * Time: 18-1-28 下午3:18.
 * Discription: This is IFlyPlayer
 */

public interface IFlyViewVideo {

    /**
     * 播放器状态
     */
    int PLAY_STATUS_NO = 0;
    int PLAY_STATUS_AD = 1;
    int PLAY_STATUS_VIDEO = 2;

    /**
     *屏幕显示模式
     */
    int SHOWMODE_AUTOFULL = 0;
    int SHOWMODE_AUTOSIZE = 1;
    int SHOWMODE_ALLFULL = 2;
    /**
     * 设置播放地址：
     * @param urlArr
     */
    IFlyViewVideo setPlayUrlArr(String urlArr[]);

    /**
     * 设置播放广告地址：
     * @param adUrlArr
     */
    IFlyViewVideo setPlayAdUrlArr(String adUrlArr[]);

    /**
     * 设置是否循环播放
     * @param isLoop
     * @return
     */
    IFlyViewVideo setLoop(boolean isLoop);

    /**
     * 开始播放视频
     */
    void startPlay();

    /**
     * 设置视频显示模式(全屏，自适应，原始尺寸)
     */
    void setVideoScreenMode(int mode);

    /**
     * 恢复播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();
}
