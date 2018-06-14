package com.example.pgg.mobilevideo321.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pgg on 18-6-14.
 */

public class NetVideoItem implements Serializable{

    private List<TrailersBean> trailers;

    public List<TrailersBean> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<TrailersBean> trailers) {
        this.trailers = trailers;
    }

    public static class TrailersBean {
        /**
         * id : 70833
         * movieName : 高斯林《登月第一人》预告
         * coverImg : http://img5.mtime.cn/mg/2018/06/09/093500.53794377_120X90X4.jpg
         * movieId : 229976
         * url : http://vfx.mtime.cn/Video/2018/06/09/mp4/180609183835866592.mp4
         * hightUrl : http://vfx.mtime.cn/Video/2018/06/09/mp4/180609183835866592.mp4
         * videoTitle : 登月第一人 预告片
         * videoLength : 152
         * rating : 0
         * type : ["传记","剧情","历史"]
         * summary : 宇航员阿姆斯特朗的十年奋斗
         */

        private int id;
        private String movieName;
        private String coverImg;
        private int movieId;
        private String url;
        private String hightUrl;
        private String videoTitle;
        private int videoLength;
        private float rating;
        private String summary;
        private List<String> type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMovieName() {
            return movieName;
        }

        public void setMovieName(String movieName) {
            this.movieName = movieName;
        }

        public String getCoverImg() {
            return coverImg;
        }

        public void setCoverImg(String coverImg) {
            this.coverImg = coverImg;
        }

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHightUrl() {
            return hightUrl;
        }

        public void setHightUrl(String hightUrl) {
            this.hightUrl = hightUrl;
        }

        public String getVideoTitle() {
            return videoTitle;
        }

        public void setVideoTitle(String videoTitle) {
            this.videoTitle = videoTitle;
        }

        public int getVideoLength() {
            return videoLength;
        }

        public void setVideoLength(int videoLength) {
            this.videoLength = videoLength;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<String> getType() {
            return type;
        }

        public void setType(List<String> type) {
            this.type = type;
        }
    }
}
