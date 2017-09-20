package com.droid.manasshrestha.contentportalhome;

public interface MultiSelectListener {
    void onSelectChange(boolean checked, String id);

    boolean isSelectedVideo(String videoId);
}
