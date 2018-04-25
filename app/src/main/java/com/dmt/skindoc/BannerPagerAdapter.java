package com.dmt.skindoc;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @Author: Zora
 * @CreateTime: 2016/5/19 17:58
 * @Description: banner的adapter
 * @param <E> : data数据
 */
public abstract class BannerPagerAdapter<E> extends PagerAdapter {

    public List<E> mDatas;

    public BannerPagerAdapter(List<E> datas, Object... objectses) {
        this.mDatas = datas;
    }

    @Override
    public int getCount() {
        if (mDatas == null || mDatas.size() == 0) {
            return 0;
        }
        // 如果banner数据大于1
        else if (mDatas.size() > 1) {
            // view的总数加2，最前面和最后面，分别对应最后一个banner的展示和第一个banner的显示，用来遮盖最后一个banner和第一个banner的连贯切换做预处理
            return mDatas.size() + 2;
        } else {
            return 1;
        }
    }

    public void refreshData(List<E> datas, Object... objectses){
        this.mDatas = datas;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView = null;
        // 当图片数量大于1
        if (getCount() > 1) {
            // 当处于viewpager的第0个位置的时候即展示banner的最后一张
            if (position == 0) {
                convertView = getItemView(container, getBannerCount() - 1);
            }
            // 当处于viewpager的第getCount - 1(getBannerCount() + 1)个位置的时候即展示banner的第一张
            else if (position == getBannerCount() + 1) {
                convertView = getItemView(container, 0);
            }
            // 因为头部添加了一个view用于展示最后一个banner，所以正确的banner坐标应-1
            else {
                convertView = getItemView(container, position - 1);
            }
        }
        // 当图片数量为1的时候
        else {
            convertView = getItemView(container, position);
        }
        container.addView(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 获取banner总数
     * @return  banner数量
     */
    public int getBannerCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public abstract View getItemView(ViewGroup container, int position);
}