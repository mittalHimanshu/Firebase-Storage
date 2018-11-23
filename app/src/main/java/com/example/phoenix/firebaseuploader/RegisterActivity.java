package com.example.phoenix.firebaseuploader;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    static  Uri uri1;
    EditText email1, password1;
    String email, password;
    static String fileExtension1;
    File file;
    ImageView imageView;
    public static final int RequestPermissionCode = 7;
    private static final int GALLERY = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        email1 = findViewById(R.id.email);
        password1 = findViewById(R.id.password);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean readStoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (readStoragePermission && writeStoragePermission) {
                        Snackbar.make(findViewById(R.id.constraintLayout), "Thanks for granting Permissions", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(findViewById(R.id.constraintLayout), "Please grant all Permissions !!!", Snackbar.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    private void RequestMultiplePermission() {

        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]
                {
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);

    }

    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!CheckingPermissionIsEnabledOrNot())
            RequestMultiplePermission();
        else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                Toast.makeText(getApplicationContext(), "Already Logged In", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, ImageActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void registerUser(View view) {
        email = email1.getText().toString();
        password = password1.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(getApplicationContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                loginUser(email, password);
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void loginUser(String email, String password){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                
            }
        });
    }

    public void moveToLogin(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY){
            uri1 = data.getData();
            imageView.setImageURI(uri1);
            fileExtension1 = GetFileExtension.GetFileExtensions(uri1, RegisterActivity.this);
        }
    }

}
