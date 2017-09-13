package com.droid.manasshrestha.contentportalhome.itemmovehelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;


public class SimpleItemMoveHelper extends ItemTouchHelper.Callback {

    Context context;
    ItemMoveHelper itemMoveHelper;

    public SimpleItemMoveHelper(Context context, ItemMoveHelper itemMoveHelper) {
        this.itemMoveHelper = itemMoveHelper;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemMoveHelper.onItemMove(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        Log.e("onMove", "onMove");
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
}
