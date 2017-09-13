package com.droid.manasshrestha.contentportalhome.itemmovehelper;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.droid.manasshrestha.contentportalhome.DisplayUtils;
import com.droid.manasshrestha.contentportalhome.R;
import com.droid.manasshrestha.contentportalhome.VideoItemModel;
import com.droid.manasshrestha.contentportalhome.itemtouchhelper.ItemTouchHelperAdapter;

import java.util.ArrayList;


public class ItemMoveAdapter extends RecyclerView.Adapter<ItemMoveAdapter.ItemMoveHolder> implements ItemMoveHelper {

    ItemTouchHelperAdapter itemTouchHelperAdapter;
    ArrayList<VideoItemModel> videoItemModels;

    public ItemMoveAdapter(ItemTouchHelperAdapter itemTouchHelperAdapter, ArrayList<VideoItemModel> videoItemModels) {
        this.itemTouchHelperAdapter = itemTouchHelperAdapter;
        this.videoItemModels = videoItemModels;
    }

    @Override
    public ItemMoveHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) DisplayUtils.convertDpToPixel(119)));
        imageView.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.ic_file_download_white_24dp));
        imageView.setBackgroundColor(Color.BLACK);
        return new ItemMoveHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ItemMoveHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return videoItemModels.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        itemTouchHelperAdapter.onItemMove(fromPosition, toPosition);
    }

    public class ItemMoveHolder extends RecyclerView.ViewHolder {
        public ItemMoveHolder(View itemView) {
            super(itemView);
        }
    }
}
