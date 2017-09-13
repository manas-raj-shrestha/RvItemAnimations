package com.droid.manasshrestha.contentportalhome;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.droid.manasshrestha.contentportalhome.itemmovehelper.ItemMoveAdapter;
import com.droid.manasshrestha.contentportalhome.itemmovehelper.SimpleItemMoveHelper;
import com.droid.manasshrestha.contentportalhome.itemtouchhelper.OnStartDragListener;
import com.droid.manasshrestha.contentportalhome.itemtouchhelper.SimpleItemTouchHelper;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observables.ConnectableObservable;

public class HomeActivity extends AppCompatActivity implements OnStartDragListener, EditListener {

    @BindView(R.id.rv_video_list)
    RecyclerView rvVideoList;

    @BindView(R.id.rv_item_mover)
    RecyclerView rvItemMover;

    VideoViewAdapter demoAdapter;
    public static ArrayList<VideoItemModel> videoItemModels;
    ItemTouchHelper touchHelper;
    boolean editModeOn;
    ObservableEmitter<Boolean> e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        videoItemModels = getTempDatas();

        ConnectableObservable<Boolean> observable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                HomeActivity.this.e = e;
            }
        }).publish();

        demoAdapter = new VideoViewAdapter(videoItemModels, this, observable);
        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelper(this, demoAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvVideoList);
        rvVideoList.setAdapter(demoAdapter);
        rvVideoList.addItemDecoration(new SpacesItemDecoration(32));
        rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        ((SimpleItemAnimator) rvVideoList.getItemAnimator()).setSupportsChangeAnimations(false);
        rvVideoList.setItemAnimator(new CustomItemAnimator());

        setItemMover();
    }


//    private ConnectableObservable<Boolean> setEditObservable() {
//
//
//
//        return observable;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            editModeOn =!editModeOn;
            e.onNext(editModeOn);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setItemMover() {

        ItemMoveAdapter itemMoveAdapter = new ItemMoveAdapter(demoAdapter, videoItemModels);

        ItemTouchHelper.Callback callback =
                new SimpleItemMoveHelper(this, itemMoveAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvItemMover);

        rvItemMover.setLayoutManager(new LinearLayoutManager(this));
        rvItemMover.setAdapter(itemMoveAdapter);


    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    @Override
    public boolean getEditStatus() {
        return editModeOn;
    }

    public class CustomItemAnimator extends DefaultItemAnimator {
        @Override
        public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
            if (oldHolder == newHolder) {
                if (oldHolder != null) {
                    //if the two holders are equal, call dispatch change only once
                    dispatchChangeFinished(oldHolder, /*ignored*/true);
                }
            } else {
                //else call dispatch change once for every non-null holder
                if (oldHolder != null) {
                    dispatchChangeFinished(oldHolder, true);
                }
                if (newHolder != null) {
                    dispatchChangeFinished(newHolder, false);
                }
            }

            return false;
        }
    }

    public static ArrayList<VideoItemModel> getTempDatas() {
        ArrayList<VideoItemModel> videoItemModels = new ArrayList<>();

        VideoItemModel videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("World of Autism");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DOWNLOADING);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.world_of_autism);
        videoItemModels.add(videoItemModel2);

        VideoItemModel videoItemModel3 = new VideoItemModel();
        videoItemModel3.setFavorite(false);
        videoItemModel3.setTitle("OK Go - Upside Down & Inside Out");
        videoItemModel3.setVideoState(VideoItemModel.VideoState.ERROR);
        videoItemModel3.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel3.setThumbnailId(R.drawable.upside_down);
        videoItemModels.add(videoItemModel3);

        videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("Steven Wilson - Pariah");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DEFAULT);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.pariah);
        videoItemModels.add(videoItemModel2);


        videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("Happiness is a choice");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DEFAULT);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.pepsi);
        videoItemModels.add(videoItemModel2);

        videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("Taylor Swift - Style");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DEFAULT);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.style);
        videoItemModels.add(videoItemModel2);

        videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("Uptown Funk");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DEFAULT);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.uptown);
        videoItemModels.add(videoItemModel2);

        videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("Share a coke this holiday");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DEFAULT);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.coke);
        videoItemModels.add(videoItemModel2);

        videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("Design on the outside. Fun on the inside");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DEFAULT);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.vw);
        videoItemModels.add(videoItemModel2);

        videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("Despacito");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DEFAULT);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.despacito);
        videoItemModels.add(videoItemModel2);

        videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("The Barber of Seville");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DEFAULT);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.opera);
        videoItemModels.add(videoItemModel2);

        videoItemModel2 = new VideoItemModel();
        videoItemModel2.setFavorite(false);
        videoItemModel2.setTitle("Sky Dive Dubai");
        videoItemModel2.setVideoState(VideoItemModel.VideoState.DEFAULT);
        videoItemModel2.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel2.setThumbnailId(R.drawable.sky_dive);
        videoItemModels.add(videoItemModel2);

        VideoItemModel videoItemModel = new VideoItemModel();
        videoItemModel.setFavorite(false);
        videoItemModel.setTitle("Travel Nepal - Pokhara");
        videoItemModel.setVideoState(VideoItemModel.VideoState.DOWNLOADED);
        videoItemModel.setVideoType(VideoItemModel.VideoType.NORMAL);
        videoItemModel.setThumbnailId(R.drawable.travel_nepal);
        videoItemModels.add(videoItemModel);

        return videoItemModels;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }
}
