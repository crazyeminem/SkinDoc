package com.dmt.skindoc;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Zora
 * @CreateTime: 2016/5/19 17:58
 * @Description:
 */
public class BannerView extends ViewPager {

    private BannerPagerAdapter mAdapter;
    /** banner指示器（不是view的position指示器）*/
    private int mPageIndicator;
    private OnPageChangeListener mPageChangeListener;
    private List<OnIndicatorChangedListener> mIndicatorChangeListeners;
    private static final String TAG = "BannerView";

    public BannerView(Context context) {
        super(context);
        init();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @deprecated
     * this method is useless for rewrite. One can use{@link #addPageIndicator(OnIndicatorChangedListener)} instead of it.
     * @param listener
     */
    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        synchronized (this) {
            if (mPageChangeListener == null) {
                mPageChangeListener = listener;
                super.addOnPageChangeListener(listener);
            }
        }
    }

    /**
     * @deprecated
     * this method is useless,please use{@link #getRealCurrentItem()} instead;
     * @return
     */
    @Override
    public int getCurrentItem() {
        return super.getCurrentItem();
    }

    /**
     * 获取视图展示的banner所对应的position
     * @return
     */
    public int getRealCurrentItem(){
        return mPageIndicator;
    }

    /**
     * @deprecated
     * this method is useless, please use {@link #setRealCurrentItem(int)} instead.
     * @param item
     */
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

    /**
     * @deprecated
     * this method is useless, please use {@link #setRealCurrentItem(int,boolean)} instead.
     * @param item
     */
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    /**
     * 显示指定视图
     * @param item  第item个banner
     */
    public void setRealCurrentItem(int item){
        super.setCurrentItem(item + 1);
    }

    /**
     * 显示指定视图
     * @param item  第item个banner
     */
    public void setRealCurrentItem(int item, boolean smothScroll){
        super.setCurrentItem(item + 1,smothScroll);
    }

    /**
     * this method is useless for rewrite. One can use{@link #addPageIndicator(OnIndicatorChangedListener)} instead of it.
     * @param listener
     */
    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        super.setOnPageChangeListener(listener);
    }

    private void init(){
        mIndicatorChangeListeners = new ArrayList<OnIndicatorChangedListener>();
       this.addOnPageChangeListener(new OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               Log.d(TAG, "onPageScrolled() called with: " + "mPageIndicator = [" + mPageIndicator + "], positionOffset = [" + positionOffset + "], positionOffsetPixels = [" + positionOffsetPixels + "]");
               for (OnIndicatorChangedListener listener : mIndicatorChangeListeners){
                   listener.onPageScrolled(mPageIndicator,positionOffset,positionOffsetPixels);
               }
           }

           @Override
           public void onPageSelected(int position) {
               Log.d(TAG, "onPageSelected() called with: " + "mPageIndicator = [" + mPageIndicator + "]");
               for (OnIndicatorChangedListener listener : mIndicatorChangeListeners){
                   // 当展示第0个view的时候即展示最后一个banner，所以指示器对应显示最后一个位置，去banner的最后一个坐标
                   if (position == 0){
                       position = mAdapter.getBannerCount() - 1;
                   }
                   // 因为先执行onPageSelected(int)，后执行onPageScrollStateChanged(int)的SCROLL_STATE_IDLE状态，因此position比当前的banner坐标永远超前一位，不能用mPageIndicator作为指示器
                   else{
                       position -= 1;
                   }
                   listener.onPageSelected(position);
               }
           }

           @Override
           public void onPageScrollStateChanged(int state) {
               // 当滑动停止且有数据的时候
                if (state == SCROLL_STATE_IDLE ) {
                    // 获取当前位置
                    int position = getCurrentItem();
                    if (mAdapter != null && mAdapter.getBannerCount() > 1) {
                        // 如果当前的位置是viewpager的第0个位置时
                        if (position == 0) {
                            // 无动画切换到相同内容的倒数第二个view的位置，变换位置后使当前view左右都存在item,因此实现循环滑动效果
                            mPageIndicator = mAdapter.getBannerCount() - 1;
                            setCurrentItem(mPageIndicator + 1, false);
                        }
                        // 如果当前的位置是最后一个view的时候
                        else if (position == mAdapter.getBannerCount() + 1) {
                            // 无动画切换到相同内容的第二个view的位置，变换位置后使当前view左右都存在item,因此实现循环滑动效果
                            mPageIndicator = 0;
                            setCurrentItem(mPageIndicator + 1, false);
                        } else {
                            mPageIndicator = position - 1;
                        }
                    }else{
                        mPageIndicator = position;
                    }
                    for (OnIndicatorChangedListener listener : mIndicatorChangeListeners){
                        listener.indicateCurrentItem(mPageIndicator);
                    }
                }
            Log.d(TAG, "onPageScrollStateChanged() called with: " + "state = [" + state + "]");
           }
       });
   }

    /**
     * 设置页面指示器
     * @param listener 指示器
     */
    public void addPageIndicator(OnIndicatorChangedListener listener){
        this.mIndicatorChangeListeners.add(listener);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        this.mAdapter = (BannerPagerAdapter) adapter;
        super.setAdapter(adapter);
        // 设置初始显示banner的第1张，view的第2个（position = 1）
        this.setCurrentItem(1,false);
        mPageIndicator = 0;
    }

    public interface OnIndicatorChangedListener{

        void indicateCurrentItem(int mPageIndicator);

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

    }

}
