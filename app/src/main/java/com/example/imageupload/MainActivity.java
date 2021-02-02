package com.example.imageupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    Button upload;
    String val_img="";
    int imagePicker_code=101;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image=findViewById(R.id.father_reg_add_image);
        upload=findViewById(R.id.upload);
        progress=findViewById(R.id.progress);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to open gallery/camera
                CropImage.activity().start(MainActivity.this);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

            }
        });
    }

    private void uploadImage() {
        if (image.getDrawable() == null) {
            val_img = "";
            Toast.makeText(MainActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
        } else {
            Bitmap imageBit = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream byteA = new ByteArrayOutputStream();
            int currSize=0;
            int currQuality =100;
            imageBit.compress(Bitmap.CompressFormat.JPEG, currQuality, byteA);
            if(byteA.toByteArray().length>700000){
                progress.setVisibility(View.VISIBLE);
                do {
                    if(currQuality>=0 && currQuality<=100){
                        byteA.reset();
                        imageBit.compress(Bitmap.CompressFormat.JPEG, currQuality, byteA);
                        currSize = byteA.toByteArray().length;
                        Log.e("currenctqty",currQuality+"="+currSize);
                        // limit quality by 5 percent every time
                        currQuality = currQuality-5;
                    }
                } while (currSize >= 700000);
            }
            Log.e("currenctqty=aftr",""+byteA.toByteArray().length);
            val_img = Base64.encodeToString(byteA.toByteArray(), Base64.DEFAULT);
            getResponse();
        }
    }

    private void getResponse() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api_interface.JSONURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        Api_interface api = retrofit.create(Api_interface.class);
        Call<String> call = api.add_user(val_img);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progress.setVisibility(View.GONE);
                try {
                    JSONObject obj=new JSONObject(response.body());
                    Toast.makeText(MainActivity.this, obj.get("message").toString(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                assert result != null;
                Uri uri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), uri);
                    image.setImageBitmap(bitmap); //set image from image picker

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e=result.getError();
                Toast.makeText(MainActivity.this,"error is : "+e,Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this,"Something went wrong! try again",Toast.LENGTH_SHORT).show();

            }
        }

    }
}