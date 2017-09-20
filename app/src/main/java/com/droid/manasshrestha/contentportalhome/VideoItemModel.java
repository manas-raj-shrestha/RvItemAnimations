package com.droid.manasshrestha.contentportalhome;

public class VideoItemModel {

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    int videoId;
    String title;
    VideoType videoType;
    boolean isDownloaded;
    int thumbnailId;
    boolean favorite;
    VideoState videoState;

    public enum VideoType {
        NORMAL,
        VR,
        V360
    }

    public enum VideoState {
        DEFAULT,
        DOWNLOADING,
        DOWNLOADED,
        DOWNLOAD_FAILED,
        ERROR,
        UNFAVORITE_TRANSITION, FAVORITE_TRANSITION
    }

    public String getTitle() {
        return title;
    }

    public int getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(int thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public VideoType getVideoType() {
        return videoType;
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public VideoState getVideoState() {
        return videoState;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public void setVideoState(VideoState videoState) {
        this.videoState = videoState;
    }
}
