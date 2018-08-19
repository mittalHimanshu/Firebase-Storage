package com.example.phoenix.firebaseuploader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static StorageReference mStorage;
    Button button;
    ImageView imageView;
    static FirebaseStorage storage;
    private static final int GALLERY = 4;
    ProgressDialog progressDialog;
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
        setContentView(R.layout.activity_main);
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        progressDialog = new ProgressDialog(this);
        button = findViewById(R.id.button2);
        uid = currentUser.getUid();
        myRef = database.getReference().child(uid);
        imageView = findViewById(R.id.imageView);
        mStorage = FirebaseStorage.getInstance().getReference();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY){
            progressDialog.setMessage("Uploading.....");
            uri = data.getData();
            progressDialog.show();
            file = new File(uri.getPath());
            imageView.setImageURI(uri);
            fileExtension = GetFileExtension.GetFileExtensions(uri, MainActivity.this);
            fileName = mStorage.child("Photos/" + uid + "/" + file.getName() + "." + fileExtension);
            fileName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    ImageDetails imageDetails = new ImageDetails();
                    imageDetails.setName(file.getName() + "." + fileExtension);
                    imageDetails.setUrl(taskSnapshot.getDownloadUrl().toString());
                    myRef.child(sdf.format(new Date())).setValue(imageDetails);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void showImages(View view) {
        new SendFeedback().execute();
    }

    public void logoutUser(View view) {

        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private class SendFeedback extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait.....");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            arrayList.clear();
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        ImageDetails imageDetails = dataSnapshot1.getValue(ImageDetails.class);
                        MainActivity.arrayList.add(imageDetails);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
