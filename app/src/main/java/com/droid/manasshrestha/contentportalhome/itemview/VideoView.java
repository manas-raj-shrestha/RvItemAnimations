package com.droid.manasshrestha.contentportalhome.itemview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.droid.manasshrestha.contentportalhome.DisplayUtils;
import com.droid.manasshrestha.contentportalhome.MultiSelectListener;
import com.droid.manasshrestha.contentportalhome.R;
import com.droid.manasshrestha.contentportalhome.VideoItemModel;
import com.droid.manasshrestha.contentportalhome.VideoViewAdapter;
import com.droid.manasshrestha.contentportalhome.itemtouchhelper.OnStartDragListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;

public class VideoView extends FrameLayout implements ViewStateListener {

    private static final ValueAnimator parentHeightAnim = ValueAnimator.ofFloat(DisplayUtils.convertDpToPixel(112), DisplayUtils.convertDpToPixel(96));
    private static final ValueAnimator textSizeAnim = ValueAnimator.ofFloat(14f, 13f);
    private static final ValueAnimator reverseParentHeightAnim = ValueAnimator.ofFloat(DisplayUtils.convertDpToPixel(96), DisplayUtils.convertDpToPixel(112));
    private static final ValueAnimator reverseTextSizeAnim = ValueAnimator.ofFloat(13f, 14f);

    private final static int ANIMATION_DURATION = 400;
    private final int editAnimDuration = 100;

    private static int screenWidth;

    @BindView(R.id.iv_thumbnail)
    ImageView ivThumbnail;

    @BindView(R.id.rl_container)
    RelativeLayout rvContainer;

    @BindView(R.id.rl_thumbnail_container)
    RelativeLayout rlThumbnailContainer;

    @BindView(R.id.iv_downloaded)
    ImageView ivDownloaded;

    @BindView(R.id.rl_underlying_container)
    RelativeLayout rlUnderlyingContainer;

    @BindView(R.id.tv_video_name)
    TextView tvVideoName;

    @BindView(R.id.iv_check)
    ImageView ivCheck;

    @BindView(R.id.iv_favorite)
    ImageView ivFavorite;

    @BindView(R.id.tv_download)
    TextView tvDownload;

    @BindView(R.id.tv_favorite)
    TextView tvFavorite;

    @BindView(R.id.iv_mover)
    ImageView ivMover;

    @BindView(R.id.fl_parent)
    FrameLayout flParent;

    private VideoItemModel videoModel;
    private int initialViewCount;
    private MultiSelectListener multiSelectListener;

    public VideoView(@NonNull Context context) {
        this(context, null, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    public VideoItemModel getVideoModel() {
        return videoModel;
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.video_view, this);
        ButterKnife.bind(this);
        initialViewCount = rvContainer.getChildCount();

        ivCheck.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ivCheck.getTag() == null) {
                    //add to list
                    ivCheck.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_checked));
                    ivCheck.setTag(".");
                    multiSelectListener.onSelectChange(true, String.valueOf(videoModel.getVideoId()));
                } else {
                    //remove from list
                    ivCheck.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_check_unchecked));
                    ivCheck.setTag(null);
                    multiSelectListener.onSelectChange(false, String.valueOf(videoModel.getVideoId()));
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        screenWidth = getMeasuredWidth();
    }

    @Override
    public void onVideoStateChanged(VideoItemModel.VideoState state, boolean animate) {
        switch (state) {
            case ERROR:
                activateErrorState();
                break;
            case DEFAULT:
                if (videoModel.isFavorite())
                    ivFavorite.setVisibility(VISIBLE);
                else
                    ivFavorite.setVisibility(GONE);

                if (videoModel.isDownloaded())
                    ivDownloaded.setVisibility(VISIBLE);
                else
                    ivDownloaded.setVisibility(GONE);

                break;
            case DOWNLOADED:
                activateDownloadedState(true);
                break;
            case DOWNLOAD_FAILED:
                activateErrorState();
                break;
            case DOWNLOADING:
                activateDownloadingState();
                break;
            case FAVORITE_TRANSITION:
                addToFavorite();
                break;
            case UNFAVORITE_TRANSITION:
                unfavoriteVideos();
                break;
        }
    }

    private void unfavoriteVideos() {
        videoModel.setVideoState(VideoItemModel.VideoState.DEFAULT);
        onVideoStateChanged(VideoItemModel.VideoState.DEFAULT, true);
        tvDownload.setVisibility(GONE);

        ObjectAnimator downloadIconAnimation = ObjectAnimator.ofFloat(tvFavorite, "alpha", 1f, 0f);
        downloadIconAnimation.setDuration(200);
        downloadIconAnimation.start();

        rlUnderlyingContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_favorite_background));
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rlThumbnailContainer, "alpha", 0f, 1f);
        alphaAnimator.setDuration(2 * ANIMATION_DURATION);

        Animation anim = new ScaleAnimation(
                1f, 1.5f, // Start and end values for the X axis scaling
                1f, 1.5f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setStartOffset(ANIMATION_DURATION);
        anim.setDuration(250);
        ivFavorite.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ivFavorite.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation bounceAnim = new ScaleAnimation(
                        1.5f, 0f, // Start and end values for the X axis scaling
                        1.5f, 0f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                bounceAnim.setDuration(250);
                bounceAnim.setFillAfter(true);
                ivFavorite.startAnimation(bounceAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        alphaAnimator.start();
    }

    private void activateErrorState() {

        if (rvContainer.getChildCount() > initialViewCount)
            rvContainer.removeViewAt(initialViewCount);

        if (rvContainer.getChildCount() <= initialViewCount) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_error, null);
            rvContainer.addView(view);
        }

    }

    private void activateDownloadedState(boolean animate) {
        videoModel.setDownloaded(true);
        videoModel.setVideoState(VideoItemModel.VideoState.DEFAULT);
        if (rvContainer.getChildCount() > initialViewCount) {
            View view = rvContainer.getChildAt(initialViewCount);

            ObjectAnimator alphaAnimatorDownloadingView = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            alphaAnimatorDownloadingView.setDuration(ANIMATION_DURATION);
            alphaAnimatorDownloadingView.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (rvContainer.getChildCount() > initialViewCount) {
                        rvContainer.removeViewAt(initialViewCount);
                        ivDownloaded.setVisibility(VISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            alphaAnimatorDownloadingView.start();

            ObjectAnimator videoNameAnimator = ObjectAnimator.ofFloat(tvVideoName, "alpha", 0f, 1f);
            videoNameAnimator.setDuration(2 * ANIMATION_DURATION);
            videoNameAnimator.start();

            ObjectAnimator downloadedIconAnimator = ObjectAnimator.ofFloat(ivDownloaded, "alpha", 0f, 1f);
            downloadedIconAnimator.setDuration(2 * ANIMATION_DURATION);
            downloadedIconAnimator.start();
        }

    }

    private void activateDownloadingState() {
        View view = null;

        ObjectAnimator downloadIconAnimation = ObjectAnimator.ofFloat(tvDownload, "alpha", 1f, 0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rlThumbnailContainer, "alpha", 0f, 1f);
        ObjectAnimator videoNameAnimator = ObjectAnimator.ofFloat(tvVideoName, "alpha", 1f, 0f);

        if (rvContainer.getChildCount() <= initialViewCount) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.view_downloading, null);
            rvContainer.addView(view);
        }

        rlUnderlyingContainer.setBackgroundColor(Color.parseColor("#4a90e2"));
        rlUnderlyingContainer.findViewById(R.id.tv_favorite).setVisibility(GONE);
        rlThumbnailContainer.setAlpha(0f);


        downloadIconAnimation.setDuration(200);
        downloadIconAnimation.start();


        if (view != null) {
            ObjectAnimator alphaAnimatorDownloadingView = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            alphaAnimatorDownloadingView.setDuration(2 * ANIMATION_DURATION);
            alphaAnimatorDownloadingView.start();
        }
        alphaAnimator.setDuration(2 * ANIMATION_DURATION);

        final View finalView = view;
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (finalView != null) {
                    BouncingCircleView bouncingCircleView = (BouncingCircleView) finalView.findViewById(R.id.bouncing_circle_view);
                    bouncingCircleView.setVideoStateListener(VideoView.this);
                    bouncingCircleView.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        alphaAnimator.start();


        videoNameAnimator.setDuration(2 * ANIMATION_DURATION);
        videoNameAnimator.start();
    }

    public void setVideoModel(VideoItemModel videoModel, final VideoViewAdapter.ViewHolder holder) {
        this.videoModel = videoModel;

        tvVideoName.setText(videoModel.getTitle());
        if (videoModel.isFavorite())
            ivFavorite.setVisibility(VISIBLE);
        else
            ivFavorite.setVisibility(GONE);

        onVideoStateChanged(videoModel.getVideoState(), true);
        Glide.with(getContext()).load(videoModel.getThumbnailId()).into(ivThumbnail);

        // Start a drag whenever the handle view it touched
        ivMover.setVisibility(GONE);
        ivMover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    ((OnStartDragListener) getContext()).onStartDrag(holder);
                }
                return false;
            }
        });
    }

    public void addToFavorite() {
        videoModel.setVideoState(VideoItemModel.VideoState.DEFAULT);
        onVideoStateChanged(VideoItemModel.VideoState.DEFAULT, true);
        tvDownload.setVisibility(GONE);

        ObjectAnimator downloadIconAnimation = ObjectAnimator.ofFloat(tvFavorite, "alpha", 1f, 0f);
        downloadIconAnimation.setDuration(200);
        downloadIconAnimation.start();

        rlUnderlyingContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_favorite_background));
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rlThumbnailContainer, "alpha", 0f, 1f);
        alphaAnimator.setDuration(2 * ANIMATION_DURATION);

        Animation anim = new ScaleAnimation(
                0f, 1.5f, // Start and end values for the X axis scaling
                0f, 1.5f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setStartOffset(ANIMATION_DURATION);
        anim.setDuration(250);
        ivFavorite.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ivFavorite.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation bounceAnim = new ScaleAnimation(
                        1.5f, 1f, // Start and end values for the X axis scaling
                        1.5f, 1f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                bounceAnim.setDuration(250);
                ivFavorite.startAnimation(bounceAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        alphaAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (rvContainer.getChildCount() > initialViewCount)
            rvContainer.removeViewAt(initialViewCount);
        super.onDetachedFromWindow();

    }

    public void setEditObservable(ConnectableObservable<Boolean> editObservable) {
        editObservable.subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                setEditMode(aBoolean, true);
            }
        });
        editObservable.connect();
    }

    /**
     * handles edit mode animations
     *
     * @param editStatus
     * @param animate
     */
    public void setEditMode(boolean editStatus, boolean animate) {
        if (!animate && editStatus) {
            flParent.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) DisplayUtils.convertDpToPixel(96)));
            ivMover.setVisibility(VISIBLE);
        } else if (editStatus && animate) {
            ivCheck.setVisibility(VISIBLE);
            ivMover.setVisibility(VISIBLE);
            TranslateAnimation dragArrowAnim = new TranslateAnimation(screenWidth - ivMover.getX(), 0, 0, 0);
            dragArrowAnim.setDuration(editAnimDuration);
            dragArrowAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            ivMover.setAnimation(dragArrowAnim);
            ivMover.animate();

            parentHeightAnim.setDuration(editAnimDuration);
            parentHeightAnim.setInterpolator(new DecelerateInterpolator(1.5f));

            parentHeightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    flParent.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (float) animation.getAnimatedValue()));
                }
            });

            ValueAnimator containerWidthAnim = ValueAnimator.ofFloat(rvContainer.getWidth(), DisplayUtils.convertDpToPixel(296));
            containerWidthAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            containerWidthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    rvContainer.setLayoutParams(new RelativeLayout.LayoutParams((int) (float) animation.getAnimatedValue(), ViewGroup.LayoutParams.MATCH_PARENT));
                }
            });

            textSizeAnim.setDuration(editAnimDuration);
            textSizeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    tvVideoName.setTextSize((float) animation.getAnimatedValue());
                }
            });

            parentHeightAnim.start();
            containerWidthAnim.setDuration(editAnimDuration);
            containerWidthAnim.start();
            textSizeAnim.start();

        } else if (!editStatus && animate) {
            TranslateAnimation reverseDragArrowAnim = new TranslateAnimation(0, screenWidth - ivMover.getX(), 0, 0);
            reverseDragArrowAnim.setDuration(editAnimDuration);
            reverseDragArrowAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            ivMover.setAnimation(reverseDragArrowAnim);
            ivMover.animate();

            reverseParentHeightAnim.setDuration(editAnimDuration);
            reverseParentHeightAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            reverseParentHeightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    flParent.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (float) animation.getAnimatedValue()));
                }
            });

            ValueAnimator reverseContainerWidthAnim = ValueAnimator.ofFloat(DisplayUtils.convertDpToPixel(296), screenWidth);
            reverseContainerWidthAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            reverseContainerWidthAnim.setDuration(editAnimDuration);
            reverseContainerWidthAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    rvContainer.setLayoutParams(new RelativeLayout.LayoutParams((int) (float) animation.getAnimatedValue(), ViewGroup.LayoutParams.MATCH_PARENT));
                }
            });

            reverseTextSizeAnim.setDuration(editAnimDuration);
            reverseTextSizeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    tvVideoName.setTextSize((float) animation.getAnimatedValue());
                }
            });

            reverseParentHeightAnim.start();
            reverseContainerWidthAnim.start();
            reverseTextSizeAnim.start();
            ivCheck.setVisibility(GONE);

        } else {
            flParent.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) DisplayUtils.convertDpToPixel(112)));
            rvContainer.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ivMover.setVisibility(GONE);
            ivCheck.setVisibility(GONE);
        }

    }


    public void startFavoriteAnimation() {
        addToFavorite();
    }

    public void setMultiSelectListener(MultiSelectListener multiSelectListener) {
        this.multiSelectListener = multiSelectListener;
    }
}
