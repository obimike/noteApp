package com.tupen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class AddNote extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PICK_AUDIO = 3;
    private static int IMAGE_SELECTED = 0;
    DBHelper dbHelper;
    EditText title, body;
    ImageView imagePath1, imagePath2;
    TextView audioName;
    Toast toast;
    private Uri mImageUri1;
    private Uri mImageUri2;
    private Uri mAudioUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Toolbar toolbar = findViewById(R.id.addNote_toolbar);
        setSupportActionBar(toolbar);

        // Add a back button to the toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        // op

        title = findViewById(R.id.editTextTitle);
        body = findViewById(R.id.editTextBody);
        imagePath1 = findViewById(R.id.note_image_1);
        imagePath2 = findViewById(R.id.note_image_2);
        Button addAudio = findViewById(R.id.addAudioButton);
        audioName = findViewById(R.id.textAudioName);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 124);
        } else {
            // permission already granted
        }


        imagePath1.setOnClickListener(v -> {
            IMAGE_SELECTED = 1;
            openPhotoDialog();
        });

        imagePath2.setOnClickListener(v -> {
            openPhotoDialog();
        });

        addAudio.setOnClickListener(v -> {
            // Create an intent to open the file picker
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");

            // Start the activity to select a file
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO);
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                if (IMAGE_SELECTED == 1) {
                    imagePath1.setImageBitmap(bitmap);
                    mImageUri1 = mImageUri;
                    IMAGE_SELECTED = 0;
                    System.out.println(mImageUri1);
                } else {
                    IMAGE_SELECTED = 0;
                    mImageUri2 = mImageUri;
                    imagePath2.setImageBitmap(bitmap);
                    System.out.println(mImageUri2);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (IMAGE_SELECTED == 1) {
                imagePath1.setImageBitmap(imageBitmap);
                mImageUri1 = getImageUri(getApplicationContext(), imageBitmap);
                System.out.println(mImageUri1);
            } else {
                imagePath2.setImageBitmap(imageBitmap);
                mImageUri2 = getImageUri(getApplicationContext(), imageBitmap);
                System.out.println(mImageUri2);
            }
        }else if (requestCode == PICK_AUDIO && resultCode == RESULT_OK) {
            mAudioUri = data.getData();
            audioName.setText(getFileNameFromUri(mAudioUri));
        }
    }

    // Get the Uri of an image bitmap
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }


    public void openPhotoDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pick_image);
        dialog.setCancelable(true);

        ImageView camera = dialog.findViewById(R.id.camera);
        ImageView gallery = dialog.findViewById(R.id.gallery);

        camera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please allow the use of camera permission.", Toast.LENGTH_LONG).show();
                } else {
                launchCamera(dialog);
            }
        });

        gallery.setOnClickListener(v -> {
            launchGallery(dialog);
        });
        //  show dialog
        dialog.show();
    }

    //  open the default gallery app
    private void launchGallery(Dialog d) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        d.dismiss();
        startActivityForResult(Intent.createChooser(intent, "Select a picture"), PICK_IMAGE_REQUEST);
    }

    // open the default camera
    private void launchCamera(Dialog d) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            d.dismiss();
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (fileName == null) {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }
        System.out.println("---------================--------: " + fileName);
        return fileName;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}