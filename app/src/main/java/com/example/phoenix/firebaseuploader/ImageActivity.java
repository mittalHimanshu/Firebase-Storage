package com.example.phoenix.firebaseuploader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageActivity extends AppCompatActivity {
    ListView listView1;
    ImageAdapter imageAdapter;
    ProgressDialog progressDialog;
    static StorageReference mStorage;
    Button button;
    ImageView imageView;
    static FirebaseStorage storage;
    private static final int GALLERY = 4;
    FirebaseDatabase database;
    static DatabaseReference myRef;
    StorageReference fileName;
    Uri uri;
    String fileExtension;
    File file;
    SimpleDateFormat sdf;
    static ArrayList<ImageDetails> arrayList = new ArrayList<>();
    static String uid;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        progressDialog = new ProgressDialog(this);
        uid = currentUser.getUid();
        myRef = database.getReference().child(uid);
        mStorage = FirebaseStorage.getInstance().getReference();
        listView1 = findViewById(R.id.listView);
        progressDialog = new ProgressDialog(ImageActivity.this);
        imageAdapter = new ImageAdapter();
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ImageDetails imageDetails = arrayList.get(position);
                mStorage = storage.getReferenceFromUrl("gs://fir-uploader-fca87.appspot.com");
                progressDialog.setMessage("Downloading.....");
                progressDialog.show();
                mStorage.child("Photos/"+uid+"/"+imageDetails.getName()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    ImageDetails imageDetails = dataSnapshot1.getValue(ImageDetails.class);
                    arrayList.add(imageDetails);
                }

                if (imageAdapter.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Images Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    listView1.setAdapter(imageAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY){
            progressDialog.setMessage("Uploading.....");
            uri = data.getData();
            progressDialog.show();
            file = new File(uri.getPath());
            fileExtension = GetFileExtension.GetFileExtensions(uri, ImageActivity.this);
            fileName = mStorage.child("Photos/" + uid + "/" + file.getName() + "." + fileExtension);
            fileName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(ImageActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    ImageDetails imageDetails = new ImageDetails();
                    imageDetails.setName(file.getName() + "." + fileExtension);
                    imageDetails.setUrl(taskSnapshot.getDownloadUrl().toString());
                    myRef.child(sdf.format(new Date())).setValue(imageDetails);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.uploadImage:{
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY);
                break;
            }

            case R.id.logout:{
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ImageActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ImageActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        }
        return true;
    }
}


