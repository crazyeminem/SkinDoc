package com.dmt.skindoc;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * @Author: Zora
 * @CreateTime: 2016/5/20 14:33
 * @Description: 携带指示器的banner控件
 */
public class BannerWithIndicatorView extends RelativeLayout {

    private BannerView mBanner;
    private RadioGroup mIndicator;
    private Context mContext;
    private View mRootView;
    private BannerPagerAdapter mAdapter;
    private int mIndicatorDrawable = 0;
    private final int CENTER_HORIZONTAL = 0, RIGHT = 1, LEFT = 2;
    private CountDownTimer mTimer;
    private int mInteval = 5 * 1000;
    private boolean couldAuto = false;
    private boolean isAuto = false;

    public BannerWithIndicatorView(Context context) {
        super(context);
        init(context,null,0);
    }

    public BannerWithIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public BannerWithIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (couldAuto) {
                    isAuto = false;
                    mTimer.cancel();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (couldAuto) {
                    isAuto = true;
                    mTimer.start();
                }
                break;
        }
        return false;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        LayoutInflater inflator = LayoutInflater.from(mContext);
        mRootView = inflator.inflate(R.layout.view_banner_with_indicator,null);
        mBanner = (BannerView) mRootView.findViewById(R.id.banner);
        mIndicator = (RadioGroup) mRootView.findViewById(R.id.rg_indicator);
        if (attrs != null){
            TypedArray array = mContext.obtainStyledAttributes(attrs,R.styleable.BannerWithIndicatorView);
            mIndicatorDrawable = array.getResourceId(R.styleable.BannerWithIndicatorView_indicatorDrawable,R.drawable.bg_indicator_banner);
            LayoutParams params = (LayoutParams) mIndicator.getLayoutParams();
            int gravity = array.getInteger(R.styleable.BannerWithIndicatorView_indicatorGravity,0);
            switch (gravity) {
                case CENTER_HORIZONTAL:
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    break;
                case RIGHT:
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    break;
                case LEFT:
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    break;
                default:
                    break;
            }
            params.bottomMargin = array.getResourceId(R.styleable.BannerWithIndicatorView_indicatorMarginBottom,20);
            params.rightMargin = array.getResourceId(R.styleable.BannerWithIndicatorView_indicatorMarginRight,20);
            array.recycle();
        }
        mBanner.addPageIndicator(new BannerView.OnIndicatorChangedListener() {
            @Override
            public void indicateCurrentItem(int mPageIndicator) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mIndicator.getChildCount() > position) {
                    mIndicator.check(mIndicator.getChildAt(position).getId());
                }
            }
        });
        this.addView(mRootView);
    }

    public void setAdapter(BannerPagerAdapter adapter){
        mAdapter = adapter;
        setIndicator();
        mBanner.setAdapter(mAdapter);
        mTimer = new CountDownTimer(Long.MAX_VALUE, mInteval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isAuto && mAdapter != null && mAdapter.getBannerCount() > 1){
                    int position = mBanner.getRealCurrentItem();
                    mBanner.setRealCurrentItem(position + 1,true);
                }
            }

            @Override
            public void onFinish() {
                isAuto = false;
            }
        };
    }

    public synchronized void autoScrollPage(int inteval){
        this.couldAuto = true;
        this.isAuto = true;
        this.mInteval = inteval;
        // 延时开启自动轮播
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mTimer.start();
            }
        },mInteval);
    }

    public synchronized void stopAutoScrollPag(){
        this.couldAuto = false;
        this.mTimer.cancel();
        this.mTimer.onFinish();
    }

    public void notifyDataSetChanged(){
        mIndicator.removeAllViews();
        setIndicator();
    }

    private void setIndicator() {
        int count = mAdapter.getBannerCount();
        RadioButton radioButton = null;
        for (int i = 0; i < count; i++) {
            radioButton = new RadioButton(mContext);
            radioButton.setClickable(false);
            radioButton.setChecked(false);
            radioButton.setButtonDrawable(mIndicatorDrawable);
            mIndicator.addView(radioButton);
        }
    }

    @CallSuper
    public void refreshData(List<Object> datas, Object... obj){
        mAdapter.refreshData(datas,obj);
        notifyDataSetChanged();
    }
}
