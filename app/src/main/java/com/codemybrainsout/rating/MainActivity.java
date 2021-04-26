package com.codemybrainsout.rating;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.codemybrainsout.ratingdialog.RatingDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RelativeLayout rlRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rlRate = (RelativeLayout) findViewById(R.id.rlRate);
        rlRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .session(1)
                .threshold(4)
                .ratingBarColor(R.color.yellow)
                .playstoreUrl("https://github.com/codemybrainsout/smart-app-rate")
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        Log.i(TAG,"Feedback:" + feedback);
                    }
                })
                .build();


        ratingDialog.show();
    }
}
