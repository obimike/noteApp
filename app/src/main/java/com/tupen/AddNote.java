package com.tupen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
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
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class AddNote extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PICK_AUDIO = 3;

    private static final int PERMISSION_REQUEST_CODE = 4;
    private static int IMAGE_SELECTED = 0;
    DBHelper dbHelper;
    EditText title, body;
    ImageView imagePath1, imagePath2;
    TextView audioName;
    Toast toast;
    private Uri mImageUri1;
    private Uri mImageUri2;
    private Uri mAudioUri;

    Intent intent;
    private static final String TAG = "App";

    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 124);
        }

        Toolbar toolbar = findViewById(R.id.addNote_toolbar);
        setSupportActionBar(toolbar);

        // Add a back button to the toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        // initializing db
        dbHelper = new DBHelper(this);

        title = findViewById(R.id.editTextTitle);
        body = findViewById(R.id.editTextBody);
        imagePath1 = findViewById(R.id.note_image_1);
        imagePath2 = findViewById(R.id.note_image_2);
        Button addAudio = findViewById(R.id.addAudioButton);
        Button saveNote = findViewById(R.id.saveButton);
        audioName = findViewById(R.id.textAudioName);

        imagePath1.setOnClickListener(v -> {
            IMAGE_SELECTED = 1;
            openPhotoDialog();
        });

        imagePath2.setOnClickListener(v -> {
            openPhotoDialog();
        });

        addAudio.setOnClickListener(v -> {
            // Create an intent to open the file picker
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("audio/*");

            // Start the activity to select a file
            startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO);
        });

        saveNote.setOnClickListener(v -> {

            // Create a Date object with the desired date
            Date date = new GregorianCalendar().getTime();
            // Create a SimpleDateFormat object with the desired format
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy - hh:mm a", Locale.US);// Format the date and print it
            String formattedDate = sdf.format(date);
            System.out.println(formattedDate);

            String titleText = title.getText().toString().trim();
            String bodyText = body.getText().toString().trim();
            String audio, image1, image2;
            if (mImageUri1 == null) {
                image1 = "";
            } else {
                image1 = mImageUri1.toString();
            }

            if (mImageUri2 == null) {
                image2 = "";
            } else {
                image2 = mImageUri2.toString();
            }
            if (mAudioUri == null) {
                audio = "";
            } else {
                audio = mAudioUri.toString();
            }


            if (!titleText.isEmpty() && !bodyText.isEmpty() && !image1.isEmpty()) {
                long add = dbHelper.addNote(titleText, bodyText, image1, image2, audio, formattedDate);
                System.out.println(add);
                if (add > 0) {
                    Toast.makeText(this, "Note Saved.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else {
                Toast.makeText(this, "Title or Body or first Image can't be empty.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                if (IMAGE_SELECTED == 1) {
                    Log.d(TAG, "onActivityResult: image select 1 " + data.getData());
                    final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    ContentResolver resolver = this.getContentResolver();
                    resolver.takePersistableUriPermission(data.getData(), takeFlags);

                    imagePath1.setImageBitmap(bitmap);
                    mImageUri1 = mImageUri;
                    IMAGE_SELECTED = 0;

                    System.out.println(mImageUri1);
                } else {
                    final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    ContentResolver resolver = this.getContentResolver();
                    resolver.takePersistableUriPermission(data.getData(), takeFlags);


                    Log.d(TAG, "onActivityResult: image select 2 " + data.getData());


                    IMAGE_SELECTED = 0;
                    mImageUri2 = mImageUri;
                    imagePath2.setImageBitmap(bitmap);
                    System.out.println(mImageUri2);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (IMAGE_SELECTED == 1) {
                // Get the URI for the photo file
                File photoFile = new File(currentPhotoPath);
                Uri photoUri = FileProvider.getUriForFile(this, "com.tupen.fileprovider", photoFile);

                imagePath1.setImageURI(photoUri);
                mImageUri1 = photoUri;

                Log.d(TAG, "onActivityResult photoUri: image capture 1 " + photoUri);

                System.out.println(mImageUri1);
            } else {
                // Get the URI for the photo file
                File photoFile = new File(currentPhotoPath);
                Uri photoUri = FileProvider.getUriForFile(this, "com.tupen.fileprovider", photoFile);

                imagePath2.setImageURI(photoUri);
                mImageUri2 = photoUri;

                Log.d(TAG, "onActivityResult photoUri: image capture 1 " + photoUri);

                System.out.println(mImageUri2);
            }
        } else if (requestCode == PICK_AUDIO && resultCode == RESULT_OK) {
            final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
            ContentResolver resolver = this.getContentResolver();
            resolver.takePersistableUriPermission(data.getData(), takeFlags);

            Log.d(TAG, "onActivityResult: " + data.getData());

            mAudioUri = data.getData();
            audioName.setText(getFileNameFromUri(mAudioUri));
        }
    }

    // Get the Uri of an image bitmap

    public void openPhotoDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pick_image);
        dialog.setCancelable(true);

        ImageView camera = dialog.findViewById(R.id.camera);
        ImageView gallery = dialog.findViewById(R.id.gallery);

        camera.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
        // Create an intent to open the file picker
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");

        d.dismiss();
        startActivityForResult(Intent.createChooser(intent, "Select a picture"), PICK_IMAGE_REQUEST);
    }

    // open the default camera
    private void launchCamera(Dialog d) {
        try {
            dispatchTakePictureIntent(d);
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "Error: " + e);
        }
    }

    private void dispatchTakePictureIntent(Dialog d) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.tupen.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                d.dismiss();
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: ");
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
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