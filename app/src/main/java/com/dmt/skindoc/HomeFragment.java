package com.dmt.skindoc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ListView lv_home;
    private Button btn_wiki;
    private Button btn_onlineDoc;
    private Button btn_trace;
    private Button btn_train;
    private ArrayList<news> newsList;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_fragment,null);

        lv_home=view.findViewById(R.id.lv_home);
        btn_wiki=view.findViewById(R.id.btn_wiki);
        btn_onlineDoc=view.findViewById(R.id.btn_onlineDoc);
        btn_trace=view.findViewById(R.id.btn_trace);
        btn_train=view.findViewById(R.id.btn_train);

        //顶部滚动栏初始化
        List<Integer> images=new ArrayList<Integer>();
        images.add(R.mipmap.cat01);
        images.add(R.mipmap.cat02);
        images.add(R.mipmap.cat03);
        images.add(R.mipmap.cat04);
        images.add(R.mipmap.cat05);
        images.add(R.mipmap.cat06);
        images.add(R.mipmap.cat07);
        images.add(R.mipmap.cat08);
        images.add(R.mipmap.cat09);
        images.add(R.mipmap.cat10);
        BannerWithIndicatorView bvi_home=view.findViewById(R.id.bv_home);
        BannerAdapter bva_home=new BannerAdapter(images);
        bvi_home.setAdapter(bva_home);
        bvi_home.autoScrollPage(5000);

        //新闻栏初始化
        //获取新闻
        newsList=new ArrayList<news>();
        getNews();
        lv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(),"待开发",Toast.LENGTH_SHORT).show();
            }
        });
        lv_home.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return newsList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                View convertView;
                news theNews=newsList.get(i);
                if (view==null)
                    convertView=inflater.inflate(R.layout.home_news_listview,null);
                else
                    convertView=view;
                ImageView iv=convertView.findViewById(R.id.iv_home_news);
                TextView tv_head=convertView.findViewById(R.id.tv_home_news_head);
                TextView tv_body=convertView.findViewById(R.id.tv_home_news_body);
                iv.setImageBitmap(theNews.getImageUrl());
                tv_head.setText(theNews.getNewsHead());
                tv_body.setText(theNews.getNewsBody());
                return convertView;
            }
        });
        return view;
    }
    private class BannerAdapter<Integer> extends BannerPagerAdapter<Integer>{

        public BannerAdapter(List<Integer> datas) {
            super(datas);
        }

        @Override
        public View getItemView(ViewGroup container, int position) {
            ImageView imageView =new ImageView(getActivity());
            imageView.setImageResource((java.lang.Integer) mDatas.get(position));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            return imageView;
        }

    }
    //拉取新闻
    private void getNews()
    {
        Bitmap bitmap= BitmapFactory.decodeResource(getContext().getResources(),R.mipmap.cat01);//图片

        for (int i=0;i<5;i++){
            news news_debug=new news(bitmap,"新闻标题"+java.lang.Integer.toString(i),"新闻摘要");
            newsList.add(news_debug);
        }


    }
}
class news{
    news(Bitmap img,String head,String body)
    {
        image=img;
        newsHead=head;
        newsBody=body;
    }

    public Bitmap getImageUrl() {
        return image;
    }

    public String getNewsBody() {
        return newsBody;
    }

    public String getNewsHead() {
        return newsHead;
    }
    private Bitmap image;
    private String newsHead;
    private String newsBody;

}
