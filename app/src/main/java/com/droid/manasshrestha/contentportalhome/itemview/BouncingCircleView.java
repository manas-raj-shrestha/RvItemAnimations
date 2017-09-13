package com.droid.manasshrestha.contentportalhome.itemview;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.droid.manasshrestha.contentportalhome.R;
import com.droid.manasshrestha.contentportalhome.VideoItemModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BouncingCircleView extends View {

    private final static int START_ANGLE = 270;

    private int intermediateRadius;
    private int height;
    private int width;
    private int viewMargin = 8;
    private int strokeWidth = 5;
    private int shrinkPercentage = 10;
    private long expandDuration = 400;
    private long shrinkDuration = 50;
    private float sweepAngle = 0;

    private Paint paint;
    private ViewStateListener videoStateListener;

    public BouncingCircleView(Context context) {
        this(context, null, 0);
    }

    public BouncingCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BouncingCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int[] attrsArray = new int[]{
                android.R.attr.id, // 0
                android.R.attr.background, // 1
                android.R.attr.layout_width, // 2
                android.R.attr.layout_height // 3
        };

        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        width = ta.getDimensionPixelSize(2, ViewGroup.LayoutParams.MATCH_PARENT);
        height = ta.getDimensionPixelSize(3, ViewGroup.LayoutParams.MATCH_PARENT);
        ta.recycle();

        startExpandShrinkAnim();
        setUpPaint();

    }

    private void setUpPaint() {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.color_download_progress));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
        paint.setAlpha(190);
    }

    Disposable disposable1;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (disposable1 != null)
            disposable1.dispose();

    }

    private void startDummyEmission() {
        Observable observable = Observable.range(1, 100).flatMap(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                return Observable.just(integer * 2).delay(50 * integer, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                disposable1 = disposable;
            }
        });

        observable.subscribeWith(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                sweepAngle = (integer.floatValue() / 200f) * 360f;

                invalidate();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                if (videoStateListener != null)
                    videoStateListener.onVideoStateChanged(VideoItemModel.VideoState.DOWNLOADED);
            }
        });


    }

    private void startExpandShrinkAnim() {
        int finalRadius = (width - viewMargin) / 2;
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startDummyEmission();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animatorSet
                .play(getValueAnimator(finalRadius, (finalRadius - (finalRadius * shrinkPercentage / 100)), shrinkDuration))
                .after(getValueAnimator(0f, finalRadius, expandDuration));

        animatorSet.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(width / 2, height / 2, intermediateRadius, paint);

        paint.setStyle(Paint.Style.FILL);
        RectF rectF = new RectF(viewMargin, viewMargin , width - viewMargin , height - viewMargin );
        canvas.drawArc(rectF, START_ANGLE, sweepAngle, true, paint);
    }

    private ValueAnimator getValueAnimator(float startValue, float endValue, long duration) {
        ValueAnimator shrinkAnimator = ValueAnimator.ofFloat(startValue, endValue);
        shrinkAnimator.setDuration(duration);
        shrinkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                Float value = ((Float) (animation.getAnimatedValue())).floatValue();
                intermediateRadius = value.intValue();
                invalidate();
            }
        });

        return shrinkAnimator;
    }

    public void setVideoStateListener(VideoView videoStateListener) {
        this.videoStateListener = videoStateListener;
    }


}
