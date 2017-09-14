package com.droid.manasshrestha.contentportalhome.itemtouchhelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.droid.manasshrestha.contentportalhome.EditListener;
import com.droid.manasshrestha.contentportalhome.HomeActivity;
import com.droid.manasshrestha.contentportalhome.R;
import com.droid.manasshrestha.contentportalhome.VideoItemModel;
import com.droid.manasshrestha.contentportalhome.itemview.VideoView;

public class SimpleItemTouchHelper extends ItemTouchHelper.Callback {

    private final static int SWIPE_VELOCITY_DISABLE = 0;
    private int swipeDirection;
    private static final float SWIPE_THRESHOLD = 0.186f;
    private ItemTouchHelperAdapter itemTouchHelperAdapter;
    private View underLyingView;
    private Context context;
    private EditListener editListener;
    private VideoItemModel videoItemModel;

    public SimpleItemTouchHelper(Context context, ItemTouchHelperAdapter itemTouchHelperAdapter) {
        this.itemTouchHelperAdapter = itemTouchHelperAdapter;
        underLyingView = LayoutInflater.from(context).inflate(R.layout.layout_underlying_actions, null, false);
        this.context = context;
        this.editListener = (EditListener) context;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        videoItemModel = ((VideoView) viewHolder.itemView).getVideoModel();

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

        if (HomeActivity.videoItemModels.get(viewHolder.getAdapterPosition()).getVideoState().equals(VideoItemModel.VideoState.DOWNLOADING) || editListener.getEditStatus()) {
            return makeMovementFlags(dragFlags, 0);
        }

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {

        if (videoItemModel.isDownloaded() && swipeDirection == ItemTouchHelper.END)
            return 1000;
        else if (videoItemModel.isFavorite() && swipeDirection == ItemTouchHelper.START)
            return 1000;
        else
            return SWIPE_THRESHOLD;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        if (videoItemModel.isDownloaded() && swipeDirection == ItemTouchHelper.END)
            return 0;
        else if (videoItemModel.isFavorite() && swipeDirection == ItemTouchHelper.START)
            return 0;
        else
            return super.getSwipeVelocityThreshold(defaultValue);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        itemTouchHelperAdapter.onItemDismiss(viewHolder, direction);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View view = viewHolder.itemView;
        if (dX != 0) {
            RectF rectF = new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            drawUnderLyingView(c, rectF, dX);
        }

        if (dX > 0) {
            swipeDirection = ItemTouchHelper.END;
        } else {
            swipeDirection = ItemTouchHelper.START;
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void drawUnderLyingView(Canvas canvas, RectF rectF, float dx) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec((int) rectF.width(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec((int) rectF.height(), View.MeasureSpec.EXACTLY);

        if (dx >= rectF.width() * SWIPE_THRESHOLD) {
            underLyingView.findViewById(R.id.tv_favorite).setVisibility(View.GONE);
            underLyingView.findViewById(R.id.tv_download).setVisibility(View.VISIBLE);
            underLyingView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_download_background));
        } else if (dx <= rectF.width() * SWIPE_THRESHOLD * -1) {
            underLyingView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_favorite_background));
            underLyingView.findViewById(R.id.tv_download).setVisibility(View.GONE);
            underLyingView.findViewById(R.id.tv_favorite).setVisibility(View.VISIBLE);
        } else {
            underLyingView.findViewById(R.id.tv_favorite).setVisibility(View.VISIBLE);
            underLyingView.findViewById(R.id.tv_download).setVisibility(View.VISIBLE);
            underLyingView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_default_background));
        }

        TextView tvDownload = ((TextView) underLyingView.findViewById(R.id.tv_download));
        if (videoItemModel.isDownloaded()) {
            tvDownload.setText("Downloaded");
            tvDownload.setTextSize(6);
        } else {
            ((TextView) underLyingView.findViewById(R.id.tv_download)).setText("Download");
            tvDownload.setTextSize(8);
        }

        canvas.save();
        underLyingView.measure(widthSpec, heightSpec);
        underLyingView.layout(0, 0, (int) rectF.width(), (int) rectF.height());
        canvas.translate(rectF.left, rectF.top);
        underLyingView.draw(canvas);
        canvas.restore();
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }

}
