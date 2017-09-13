package com.droid.manasshrestha.contentportalhome.itemtouchhelper;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(RecyclerView.ViewHolder viewHolder, int direction);
}