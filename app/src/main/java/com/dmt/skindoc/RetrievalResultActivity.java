package com.dmt.skindoc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.dmt.skindoc.httpTask.FileBinary;
import com.dmt.skindoc.httpTask.HttpListener;
import com.dmt.skindoc.httpTask.JsonObjectRequest;
import com.dmt.skindoc.httpTask.Request;
import com.dmt.skindoc.httpTask.RequestExecutor;
import com.dmt.skindoc.httpTask.RequestMethod;
import com.dmt.skindoc.httpTask.Response;
import com.dmt.skindoc.httpTask.error.ParseError;
import com.dmt.skindoc.httpTask.error.TimeOutError;
import com.dmt.skindoc.httpTask.error.URLError;
import com.dmt.skindoc.httpTask.error.UnKnownHostError;
import com.dmt.skindoc.utils.Constants;
import com.dmt.skindoc.utils.Logger;

import java.io.File;
import java.util.List;



public class RetrievalResultActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView ivResult1;
    private ImageView ivResult2;
    private ImageView ivResult3;
    private TextView tvName1;
    private TextView tvName2;
    private TextView tvName3;
    private TextView tvDescription1;
    private TextView tvDescription2;
    private TextView tvDescription3;
    private Button btnSave,btnStart;

    private String filePath;
    private List<String> results;
    private File uploadFile;

    //异步加载结果
    @SuppressLint("HandlerLeak")
    private  Handler resultsHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //如果检索成功，则加载结果

            if (msg.getData().getBoolean("RESULT_OK"))
            {
                //TODO:加载结果
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrieval_results);
        ivResult1=findViewById(R.id.iv_retrieval_result1);
        ivResult2=findViewById(R.id.iv_retrieval_result2);
        ivResult3=findViewById(R.id.iv_retrieval_result3);

        tvName1=findViewById(R.id.tv_retrieval_result_name1);
        tvName2=findViewById(R.id.tv_retrieval_result_name2);
        tvName3=findViewById(R.id.tv_retrieval_result_name3);

        tvDescription1=findViewById(R.id.tv_retrieval_result_description1);
        tvDescription2=findViewById(R.id.tv_retrieval_result_description2);
        tvDescription3=findViewById(R.id.tv_retrieval_result_description3);

        btnSave=findViewById(R.id.btn_retrieval_result_save);
        btnStart=findViewById(R.id.btn_retrieval_start);


        Intent intent=getIntent();
        filePath=intent.getStringExtra("filePath");
        uploadFile=new File(filePath);




    }
    public void retrieval()
    {
        Logger.d("-------startRetrieval-------");
        Request<JSONObject> request=new JsonObjectRequest(Constants.URL_POST, RequestMethod.POST);
        //Authoriazation
        //request.setHead("Authorization","Bearer 98e7e3a59cd997e78c996ef569e0434a");

        //添加key-value
        request.add("image", new FileBinary(uploadFile));

        //开启表单
        request.formData(true);

        RequestExecutor.INSTANCE.execute(request, new HttpListener<JSONObject>() {
            @Override
            public void onSucceed(Response<JSONObject> response) {
                //成功的回调
                Logger.d("responseCode="+response.getResponseCode());
                JSONObject object=response.get();
                String str=object.toString();
                Logger.d("result:="+str);
                //TODO:存储返回的检索结果，异步加载

                Message msg=new Message();
                msg.getData().putBoolean("RESULT_OK",true);
                resultsHandler.sendMessage(msg);

            }

            @Override
            public void onFailed(Exception e) {
                Logger.i(e.getMessage());
                if (e instanceof ParseError)
                {
                    Logger.d("解析错误");

                }else if (e instanceof URLError)
                {
                    Logger.d("URL错误");

                }else if (e instanceof TimeOutError)
                {
                    Logger.d("超时错误");

                }else if (e instanceof UnKnownHostError)
                {
                    Logger.d("未知服务器错误");
                }

            }
        });


    }
    void onSave()
    {
        //TODO:保存结果
    }
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.btn_retrieval_start:
            {
                retrieval();
                break;
            }
            case R.id.btn_retrieval_result_save:
            {
                onSave();
                break;
            }
            default:
                break;
        }


    }
}
