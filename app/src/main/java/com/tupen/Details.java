package com.tupen;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.Objects;

public class Details extends AppCompatActivity {
    DBHelper dbHelper;
    int _id;
    // Initialize a new MediaPlayer object
    MediaPlayer mediaPlayer = new MediaPlayer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = findViewById(R.id.note_toolbar);
        setSupportActionBar(toolbar);

        // Add a back button to the toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        // initializing db
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();


        ArrayList<SlideModel> images = new ArrayList<>();
        ImageSlider imageSlider = findViewById(R.id.image_slider);


//        init vars
        _id = intent.getIntExtra("id", 0);

        TextView _date = findViewById(R.id.detail_date);
        _date.setText(intent.getStringExtra("date"));

        TextView title = findViewById(R.id.detail_title);
        title.setText(intent.getStringExtra("title"));
        TextView body = findViewById(R.id.detail_body);
        body.setText(intent.getStringExtra("body"));

        String image1 = intent.getStringExtra("image_1");
        String image2 = intent.getStringExtra("image_2");

        Log.d("App", image2);

        images.add(new SlideModel(image1, null));
        if (!image2.isEmpty()){
            images.add(new SlideModel(image2, null));
        }

        imageSlider.setImageList(images, ScaleTypes.CENTER_CROP);

        RelativeLayout audioLayout = findViewById(R.id.audio_layout);

        String audioPath = intent.getStringExtra("audio");

        if (audioPath.isEmpty()){
            audioLayout.setVisibility(View.GONE);
        }else {
            TextView audioName = findViewById(R.id.audio_file_name);
            audioName.setText(getFileNameFromUri(Uri.parse(audioPath)));

            Uri audioUri = Uri.parse(audioPath);

            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());

            try {
                mediaPlayer.setDataSource(getApplicationContext(), audioUri);
                mediaPlayer.prepare();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }



        Button play = findViewById(R.id.play_button);
        Button pause = findViewById(R.id.pause_button);
        Button stop = findViewById(R.id.stop_button);

        // Initialize a new MediaPlayer object

        SeekBar seekBar = findViewById(R.id.seek_bar);



        final TextView seekBarHint = findViewById(R.id.textView);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if (x > 0 && !mediaPlayer.isPlaying()) {
                    seekBar.setProgress(0);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });


        pause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                System.out.println(seekBar.getProgress());
                seekBar.setProgress(seekBar.getProgress());
            }
        });

        stop.setOnClickListener(v -> {
//            mediaPlayer.stop();
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                seekBar.setProgress(0);
            }
        });


        Runnable updateSeekBar = () -> {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int total = mediaPlayer.getDuration();

            while (mediaPlayer.isPlaying() && currentPosition < total) {
                try {
                    Thread.sleep(1000);
                    currentPosition = mediaPlayer.getCurrentPosition();
                } catch (Exception e) {
                    return;
                }
                seekBar.setProgress(currentPosition);

            }
        };

        play.setOnClickListener(v -> {
            seekBar.setMax(mediaPlayer.getDuration());
            // Start playing the audio file
            mediaPlayer.start();
            new Thread(updateSeekBar).start();
        });


    }


    private void deleteNote() {
        // Create an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and message for the dialog
        builder.setTitle("Delete Note");
        builder.setMessage("Are you sure want to delete this note.");

        // Set a positive button and its click listener
        builder.setPositiveButton("OK", (dialog, which) -> {
            releaseMediaPlayer();
            // Do something when the user clicks the OK button
            long delete = dbHelper.deleteNote(_id);
            Log.d("App", String.valueOf(delete));
            if (delete > 0) {
                Toast.makeText(this, "Note Deleted.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "Unable to delete Note.", Toast.LENGTH_LONG).show();
            }
        });

        // Set a negative button and its click listener
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do something when the user clicks the Cancel button
            dialog.dismiss();
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Log.d("App", "android.R.id.home");
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            Log.d("App", "Delete clicked!");
            deleteNote();
        }
        return super.onOptionsItemSelected(item);
    }

    private void releaseMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("App", "onDestroy");
        // Stop and release the MediaPlayer
        releaseMediaPlayer();
    }
}