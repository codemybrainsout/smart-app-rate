package com.codemybrainsout.rating

import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.codemybrainsout.ratingdialog.RatingDialog

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rlRate = findViewById<RelativeLayout>(R.id.rlRate)
        rlRate.setOnClickListener { showDialog() }
    }

    private fun showCustomDialogOnSession(session: Int = 3) {
        val ratingDialog = RatingDialog.Builder(this)
            .icon(R.mipmap.ic_launcher)
            .session(session)
            .threshold(3)
            .title(text = R.string.rating_dialog_experience, textColor = R.color.primaryTextColor)
            .positiveButton(text = R.string.rating_dialog_maybe_later, textColor = R.color.colorPrimary, background = R.drawable.button_selector_positive)
            .negativeButton(text = R.string.rating_dialog_never, textColor = R.color.secondaryTextColor)
            .formTitle(R.string.submit_feedback)
            .formHint(R.string.rating_dialog_suggestions)
            .feedbackTextColor(R.color.feedbackTextColor)
            .formSubmitText(R.string.rating_dialog_submit)
            .formCancelText(R.string.rating_dialog_cancel)
            .ratingBarColor(R.color.ratingBarColor)
            .playstoreUrl("YOUR_URL")
            .onThresholdCleared { dialog, rating, thresholdCleared -> Log.i(TAG, "onThresholdCleared: $rating $thresholdCleared") }
            .onThresholdFailed { dialog, rating, thresholdCleared -> Log.i(TAG, "onThresholdFailed: $rating $thresholdCleared") }
            .onRatingChanged { rating, thresholdCleared -> Log.i(TAG, "onRatingChanged: $rating $thresholdCleared") }
            .onRatingBarFormSubmit { feedback -> Log.i(TAG, "onRatingBarFormSubmit: $feedback") }
            .build()

        ratingDialog.show()
    }

    private fun showDialog() {
        val ratingDialog: RatingDialog = RatingDialog.Builder(this)
            .threshold(3)
            .session(1)
            .onRatingBarFormSubmit { feedback -> Log.i(TAG, "onRatingBarFormSubmit: $feedback") }
            .build()

        ratingDialog.show()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}