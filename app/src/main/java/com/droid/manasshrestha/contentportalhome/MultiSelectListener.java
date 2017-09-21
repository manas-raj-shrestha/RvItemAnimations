package com.droid.manasshrestha.contentportalhome;

public interface MultiSelectListener {
    void onSelectChange(boolean checked, int id);

    boolean isSelectedVideo(String videoId);
}
