package com.droid.manasshrestha.contentportalhome;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droid.manasshrestha.contentportalhome.itemtouchhelper.ItemTouchHelperAdapter;
import com.droid.manasshrestha.contentportalhome.itemview.VideoView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    ArrayList<VideoItemModel> videoItemModels;
    ConnectableObservable<Boolean> editObservable;

    public VideoViewAdapter(ArrayList<VideoItemModel> videoItemModels, HomeActivity homeActivity, ConnectableObservable<Boolean> booleanObservable) {
        this.videoItemModels = videoItemModels;
        this.editObservable = booleanObservable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VideoView videoView = new VideoView(parent.getContext());
        videoView.setEditObservable(editObservable);
        return new VideoViewAdapter.ViewHolder(videoView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoView videoView = (VideoView) holder.itemView;
        ((VideoView) holder.itemView).setVideoModel(videoItemModels.get(position), holder);
        videoView.setEditMode(((EditListener)(holder.itemView.getContext())).getEditStatus(), false);
    }

    @Override
    public int getItemCount() {
        return videoItemModels.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(videoItemModels, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(videoItemModels, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.END) {
            //download
            videoItemModels.get(viewHolder.getAdapterPosition()).setVideoState(VideoItemModel.VideoState.DOWNLOADING);
            HomeActivity.videoItemModels.get(viewHolder.getAdapterPosition()).setVideoState(VideoItemModel.VideoState.DOWNLOADING);
            notifyItemChanged(viewHolder.getAdapterPosition());
        } else {
            //favorite
            if (!videoItemModels.get(viewHolder.getAdapterPosition()).isFavorite()) {
                HomeActivity.videoItemModels.get(viewHolder.getAdapterPosition()).setFavorite(true);
                videoItemModels.get(viewHolder.getAdapterPosition()).setVideoState(VideoItemModel.VideoState.FAVORITE_TRANSITION);
                videoItemModels.get(viewHolder.getAdapterPosition()).setFavorite(true);
            }else {
                HomeActivity.videoItemModels.get(viewHolder.getAdapterPosition()).setFavorite(false);
                videoItemModels.get(viewHolder.getAdapterPosition()).setVideoState(VideoItemModel.VideoState.UNFAVORITE_TRANSITION);
                videoItemModels.get(viewHolder.getAdapterPosition()).setFavorite(
                        false
                );
            }
            notifyItemChanged(viewHolder.getAdapterPosition());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_video_name)
        TextView tvVideoName;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
