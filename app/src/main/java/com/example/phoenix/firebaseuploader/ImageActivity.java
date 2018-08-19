package com.example.phoenix.firebaseuploader;

import android.app.ProgressDialog;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageActivity extends AppCompatActivity {
    ListView listView1;
    ImageAdapter imageAdapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageAdapter = new ImageAdapter();
        listView1 = findViewById(R.id.listView);
        if (imageAdapter.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Images Empty", Toast.LENGTH_SHORT).show();
        }
        progressDialog = new ProgressDialog(this);

        listView1.setAdapter(imageAdapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ImageDetails imageDetails = MainActivity.arrayList.get(position);
                MainActivity.mStorage = MainActivity.storage.getReferenceFromUrl("gs://fir-uploader-fca87.appspot.com");
                progressDialog.setMessage("Downloading.....");
                progressDialog.show();
                MainActivity.mStorage.child("Photos/"+imageDetails.getName()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        String path= Environment.getExternalStorageDirectory() + File.separator + imageDetails.getName();
                        try {
                            FileOutputStream fos=new FileOutputStream(path);
                            fos.write(bytes);
                            fos.close();
                            progressDialog.dismiss();
                            Toast.makeText(ImageActivity.this, "Success!!!", Toast.LENGTH_SHORT).show();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ImageActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ImageActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(ImageActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
