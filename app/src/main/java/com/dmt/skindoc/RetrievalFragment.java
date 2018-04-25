package com.dmt.skindoc;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class RetrievalFragment extends Fragment {
    private TextView tvFilename;
    private Button btnLocalPicture;
    private Button btnTakePicture;
    private ImageView ivCroppedImage;
    private Button btnRetrieval;


    private static final int MEDIA_TYPE_IMAGE=1;
    private static final int MEDIA_TYPE_VIDEO=2;
    private static final int REQUEST_EXTERNAL_STORAGE=1000;
    private static final int REQUEST_CONTENT_ACTIVITY=1001;
    private static final int REQUEST_CAMERA_ACTIVITY=1002;
    private static final int REQUEST_CAMERA=1003;
    private static String[] PERMISSION_STORAGE={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private String userAccount="visitor";//用户名，应该从登陆界面获得
    private Uri imageUri;//照相的uri
    private String picPath;
    private boolean hasPicture=false;

    private String uploadFilePath;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.retrieval_fragment,null);
        btnLocalPicture=view.findViewById(R.id.btn_retrieval_localpic);
        btnTakePicture=view.findViewById(R.id.btn_retrieval_takepic);
        tvFilename=view.findViewById(R.id.tv_retrieval_filepath);
        ivCroppedImage=view.findViewById(R.id.iv_retrieval_cropedimage);
        btnRetrieval=view.findViewById(R.id.btn_retrieval_upload);

        btnLocalPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(),PERMISSION_STORAGE,REQUEST_EXTERNAL_STORAGE);

                }
                Intent intent_fileResurce=new Intent(Intent.ACTION_GET_CONTENT);
                intent_fileResurce.setType("image/*");
                intent_fileResurce.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent_fileResurce,REQUEST_CONTENT_ACTIVITY);
            }
        });

        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check permission
                if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
                }
                Intent intent_Camera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri=getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent_Camera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                if(intent_Camera.resolveActivity(getActivity().getPackageManager())!=null)
                    startActivityForResult(intent_Camera,REQUEST_CAMERA_ACTIVITY);

            }
        });
        btnRetrieval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 上传文件到服务器
                Toast.makeText(getActivity(),"检索中",Toast.LENGTH_SHORT).show();
                Intent  retrievalIntent=new Intent(getActivity(),RetrievalResultActivity.class);
                retrievalIntent.putExtra("filePath",uploadFilePath);

                startActivity(retrievalIntent);


            }
        });
        return view;
    }
    private void initCropView()
    {
        CropImage.activity().setAspectRatio(4,3)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                ;

    }
    /** Create a file Uri for saving an image or video */
    private  Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String state= Environment.getExternalStorageState();
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SKinPic");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                //Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        picPath=mediaStorageDir.getPath();
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //Log.d(TAG, "timeStamp"+timeStamp);
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ userAccount+"_"+timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }
    //将裁剪图保存到SkinPic文件夹，命名：“CROP_username_timestamp.jpg”
    public void saveImage(Bitmap bitmap,String savePath){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File savedCropImage=new File(savePath+File.separator+"CROP_"+userAccount+"_"+ timeStamp+".jpg");

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CONTENT_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Toast.makeText(getContext(), "获取到文件路径", Toast.LENGTH_SHORT).show();
                String filepath = PathHelper.getRealFilePath(getActivity(), uri);
                tvFilename.setText(filepath);
                CropImage.activity(uri)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(4,3)
                        .start(getContext(),this);
            }
        }
        else if (requestCode==REQUEST_CAMERA_ACTIVITY)
        {
            String filepath=PathHelper.getRealFilePath(getActivity(),imageUri);
            CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setAspectRatio(4,3)
                    .start(getContext(),this);
            tvFilename.setText(filepath);
        }
        else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            if(resultCode== Activity.RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                String filepath = PathHelper.getRealFilePath(getActivity(), result.getUri());
                tvFilename.setText(filepath);
                uploadFilePath=filepath;
                Toast.makeText(getActivity(),PathHelper.getRealFilePath(getActivity(),result.getUri()),Toast.LENGTH_LONG).show();
                //ivCroppedImage.setImageURI(result.getUri());
                Bitmap crop= BitmapFactory.decodeFile(result.getUri().getPath());
                ivCroppedImage.setImageBitmap(crop);
                hasPicture=true;
                btnRetrieval.setEnabled(true);
            }

        }

    }
}
