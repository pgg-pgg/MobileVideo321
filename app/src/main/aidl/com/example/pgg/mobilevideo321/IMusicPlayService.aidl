// IMusicPlayService.aidl
package com.example.pgg.mobilevideo321;

// Declare any non-default types here with import statements

interface IMusicPlayService {

        /**
         * 根据位置打开音频文件
         * @param position
         */
        void openAudio(int position);

        /**
         * 播放音乐
         */
        void start();

        /**
         * 暂停
         */
        void pause();

        /**
         * 停止
         */
        void stop();

        /**
         * 获取当前播放进度
         * @return
         */
        int getCurrentPosition();

        int getDuration();

        /**
         * 获取演唱者名称
         * @return
         */
        String getArtist();

        /**
         * 获取歌曲名称
         * @return
         */
        String getName();

        /**
         * 获取歌曲路径
         * @return
         */
        String getAudioPath();

        /**
         * 播放下一个音乐
         */
        void next();

        void pre();

        /**
         * 设置播放模式
         */
        void setPlayMode(int playMode);

        /**
         * 得到播放模式
         * @return
         */
        int getPlayMode();

        /**
         *是否在播放
         */
        boolean isPlaying();

        void seekTo(int position);


        int getAudioSessionId();
}
