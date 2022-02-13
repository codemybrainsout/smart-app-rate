package com.codemybrainsout.ratingdialog

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible

/**
 * Created by ahulr on 24-10-2016.
 */

class RatingDialog(context: Context, private val builder: Builder) : AppCompatDialog(context),
    OnRatingBarChangeListener, View.OnClickListener {

    companion object {
        private const val MyPrefs = "RatingDialog"
        private const val SESSION_COUNT = "session_count"
        private const val SHOW_NEVER = "show_never"
        private const val DEFAULT_SESSION = 1
        private const val DEFAULT_THRESHOLD = 3
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(MyPrefs, Context.MODE_PRIVATE)
    private val threshold: Int = builder.threshold
    private val session: Int = builder.session

    //views
    private var textViewDialogTitle: TextView? = null
    private var textViewDialogButtonPositive: TextView? = null
    private var textViewDialogButtonNegative: TextView? = null
    private var textViewFeedbackTitle: TextView? = null
    private var textViewFeedbackSubmit: TextView? = null
    private var textViewFeedbackCancel: TextView? = null
    private var editTextFeedback: EditText? = null
    private var ratingBar: RatingBar? = null
    private var icon: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_rating)

        initViews()
        setValues()
        setTheme()
    }

    private fun initViews() {
        textViewDialogTitle = findViewById(R.id.dialog_rating_title)
        textViewDialogButtonNegative = findViewById(R.id.dialog_rating_button_negative)
        textViewDialogButtonPositive = findViewById(R.id.dialog_rating_button_positive)
        textViewFeedbackTitle = findViewById(R.id.dialog_rating_feedback_title)
        textViewFeedbackSubmit = findViewById(R.id.dialog_rating_button_feedback_submit)
        textViewFeedbackCancel = findViewById(R.id.dialog_rating_button_feedback_cancel)
        ratingBar = findViewById(R.id.dialog_rating_rating_bar)
        icon = findViewById(R.id.dialog_rating_icon)
        editTextFeedback = findViewById(R.id.dialog_rating_feedback)
    }

    private fun setValues() {

        textViewDialogTitle?.text = builder.title ?: context.getString(R.string.rating_dialog_experience)
        textViewFeedbackTitle?.text = builder.formTitle ?: context.getString(R.string.rating_dialog_feedback_title)
        editTextFeedback?.hint = builder.feedbackFormHint ?: context.getString(R.string.rating_dialog_suggestions)

        textViewDialogButtonNegative?.apply {
            setOnClickListener(this@RatingDialog)
            text = builder.negativeText ?: context.getString(R.string.rating_dialog_never)
            isVisible = session != 1
        }

        textViewDialogButtonPositive?.apply {
            setOnClickListener(this@RatingDialog)
            text = builder.positiveText ?: context.getString(R.string.rating_dialog_maybe_later)
        }

        textViewFeedbackSubmit?.apply {
            setOnClickListener(this@RatingDialog)
            text = builder.submitText ?: context.getString(R.string.rating_dialog_submit)
        }

        textViewFeedbackCancel?.apply {
            setOnClickListener(this@RatingDialog)
            text = builder.cancelText ?: context.getString(R.string.rating_dialog_cancel)
        }

        ratingBar?.apply {
            onRatingBarChangeListener = this@RatingDialog
            if (builder.ratingBarColor != 0) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    val stars = progressDrawable as LayerDrawable
                    stars.getDrawable(2).setColorFilter(builder.ratingBarColor, PorterDuff.Mode.SRC_ATOP)
                    stars.getDrawable(1).setColorFilter(builder.ratingBarColor, PorterDuff.Mode.SRC_ATOP)
                    val ratingBarBackgroundColor = if (builder.ratingBarBackgroundColor != 0) builder.ratingBarBackgroundColor else R.color.secondaryTextColor
                    stars.getDrawable(0).setColorFilter(ContextCompat.getColor(context, ratingBarBackgroundColor), PorterDuff.Mode.SRC_ATOP)
                } else {
                    val stars = progressDrawable
                    DrawableCompat.setTint(stars, builder.ratingBarColor)
                }
            }
        }

        icon?.apply {
            builder.iconRes?.let { setImageResource(it) } ?: run {
                val d = context.packageManager.getApplicationIcon(context.applicationInfo)
                setImageDrawable(if (builder.iconDrawable != null) builder.iconDrawable else d)
            }
        }

        if (builder.ratingThresholdClearedListener == null) setRatingThresholdClearedListener()
        if (builder.ratingThresholdFailedListener == null) setRatingThresholdFailedListener()
    }

    private fun setTheme() {
        if (builder.titleTextColor != 0) {
            textViewDialogTitle?.setTextColor(builder.titleTextColor)
            textViewFeedbackTitle?.setTextColor(builder.titleTextColor)
        }

        if (builder.negativeTextColor != 0) {
            textViewDialogButtonNegative?.setTextColor(builder.negativeTextColor)
            textViewFeedbackCancel?.setTextColor(builder.negativeTextColor)
        }

        if (builder.positiveTextColor != 0) {
            textViewDialogButtonPositive?.setTextColor(builder.positiveTextColor)
            textViewFeedbackSubmit?.setTextColor(builder.positiveTextColor)
        }

        if (builder.positiveBackground != 0) {
            textViewDialogButtonPositive?.setBackgroundResource(builder.positiveBackground)
            textViewFeedbackSubmit?.setBackgroundResource(builder.positiveBackground)
        }

        if (builder.negativeBackground != 0) {
            textViewDialogButtonNegative?.setBackgroundResource(builder.negativeBackground)
            textViewFeedbackCancel?.setBackgroundResource(builder.negativeBackground)
        }

        if (builder.feedbackTextColor != 0) editTextFeedback?.setTextColor(builder.feedbackTextColor)
        if (builder.hintColor != 0) editTextFeedback?.setHintTextColor(builder.hintColor)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.dialog_rating_button_negative -> onNegativeButtonClick()
            R.id.dialog_rating_button_positive -> onPositiveButtonClick()
            R.id.dialog_rating_button_feedback_submit -> onFeedbackSubmit()
            R.id.dialog_rating_button_feedback_cancel -> dismiss()
        }
    }

    private fun onNegativeButtonClick() {
        showNever()
        if (builder.negativeButtonClickListener == null) {
            dismiss()
        } else {
            builder.negativeButtonClickListener?.invoke(this)
        }
    }

    private fun onPositiveButtonClick() {
        if (builder.positiveButtonClickListener == null) {
            dismiss()
        } else {
            builder.positiveButtonClickListener?.invoke(this)
        }
    }

    private fun onFeedbackSubmit() {
        showNever()
        val feedback = editTextFeedback?.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(feedback)) {
            editTextFeedback?.shake()
            return
        }
        builder.ratingDialogFormListener?.invoke(feedback)
        dismiss()
    }

    private fun View.shake() {
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
        startAnimation(shake)
    }

    override fun onRatingChanged(ratingBar: RatingBar, v: Float, b: Boolean) {
        if (ratingBar.rating >= threshold) {
            showNever()
            builder.ratingThresholdClearedListener?.invoke(this, ratingBar.rating, ratingBar.rating >= threshold)
        } else {
            builder.ratingThresholdFailedListener?.invoke(this, ratingBar.rating, ratingBar.rating >= threshold)
        }
        builder.ratingDialogListener?.invoke(ratingBar.rating, ratingBar.rating >= threshold)
    }

    private fun setRatingThresholdClearedListener() {
        builder.ratingThresholdClearedListener = { _, _, _ ->
            openGooglePlay(context)
            dismiss()
        }
    }

    private fun setRatingThresholdFailedListener() {
        builder.ratingThresholdFailedListener = { _, _, _ ->
            showFeedbackForm()
        }
    }

    private fun openGooglePlay(context: Context) {
        if (builder.marketUrl.isNullOrBlank()) builder.marketUrl = context.getString(R.string.market_prefix) + context.packageName
        val marketUri = Uri.parse(builder.marketUrl)
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, marketUri))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.error_no_google_play), Toast.LENGTH_SHORT).show()
        }
    }

    override fun show() {
        if (checkIfSessionMatches(session)) {
            super.show()
        }
    }

    private fun showFeedbackForm() {
        findViewById<LinearLayout>(R.id.layout_rating)?.isVisible = false
        findViewById<LinearLayout>(R.id.layout_feedback)?.isVisible = true
    }

    fun incrementSessionCount() {
        var count = sharedPreferences.getInt(SESSION_COUNT, 1)
        count++
        with(sharedPreferences.edit()) {
            putInt(SESSION_COUNT, count)
            apply()
        }
    }

    fun resetCount() {
        saveCount(1)
    }

    private fun saveCount(count: Int) {
        with(sharedPreferences.edit()) {
            putInt(SESSION_COUNT, count)
            apply()
        }
    }

    private fun checkIfSessionMatches(session: Int): Boolean {
        if (session == DEFAULT_SESSION) {
            return true
        }
        if (sharedPreferences.getBoolean(SHOW_NEVER, false)) {
            return false
        }
        var count = sharedPreferences.getInt(SESSION_COUNT, 1)
        return when {
            session == count -> {
                resetCount()
                true
            }
            session > count -> {
                if (builder.sessionIncrementAutomatic) {
                    count++
                    saveCount(count)
                }
                false
            }
            else -> {
                saveCount(count)
                false
            }
        }
    }

    private fun showNever(value: Boolean = true) {
        with(sharedPreferences.edit()) {
            putBoolean(SHOW_NEVER, value)
            apply()
        }
    }

    class Builder(private val context: Context) {

        // strings
        internal var title: String? = null
        internal var positiveText: String? = null
        internal var negativeText: String? = null
        internal var marketUrl: String? = null
        internal var formTitle: String? = null
        internal var submitText: String? = null
        internal var cancelText: String? = null
        internal var feedbackFormHint: String? = null

        // colors
        internal var ratingBarColor = 0
        internal var positiveTextColor = 0
        internal var negativeTextColor = 0
        internal var titleTextColor = 0
        internal var feedbackTextColor = 0
        internal var hintColor = 0

        @ColorRes
        internal var ratingBarBackgroundColor = 0

        @DrawableRes
        internal var positiveBackground = 0

        @DrawableRes
        internal var negativeBackground = 0

        // icon
        internal var iconDrawable: Drawable? = null
        internal var iconRes: Int? = null

        // listeners
        internal var ratingThresholdClearedListener: ((dialog: RatingDialog?, rating: Float, thresholdCleared: Boolean) -> Unit)? = null
        internal var ratingThresholdFailedListener: ((dialog: RatingDialog?, rating: Float, thresholdCleared: Boolean) -> Unit)? = null
        internal var ratingDialogFormListener: ((feedback: String?) -> Unit)? = null
        internal var ratingDialogListener: ((rating: Float, thresholdCleared: Boolean) -> Unit)? = null
        internal var positiveButtonClickListener: ((dialog: Dialog) -> Unit)? = null
        internal var negativeButtonClickListener: ((dialog: Dialog) -> Unit)? = null

        // other
        internal var session: Int = DEFAULT_SESSION
        internal var threshold: Int = DEFAULT_THRESHOLD
        internal var sessionIncrementAutomatic: Boolean = true

        fun threshold(value: Int): Builder {
            threshold = value
            return this
        }

        fun session(value: Int): Builder {
            session = value
            return this
        }

        fun icon(@DrawableRes icon: Int): Builder {
            iconRes = icon
            return this
        }

        fun icon(icon: Drawable): Builder {
            iconDrawable = icon
            return this
        }

        fun title(@StringRes text: Int? = null, @ColorRes textColor: Int? = null): Builder {
            title = text?.let { context.getString(it) }
            titleTextColor = textColor?.let { ContextCompat.getColor(context, it) } ?: 0
            return this
        }

        fun positiveButton(@StringRes text: Int? = null, @ColorRes textColor: Int? = null, @DrawableRes background: Int = 0, clickListener: ((dialog: Dialog) -> Unit)? = null): Builder {
            positiveText = text?.let { context.getString(it) }
            positiveTextColor = textColor?.let { ContextCompat.getColor(context, it) } ?: 0
            positiveBackground = background
            positiveButtonClickListener = clickListener
            return this
        }

        fun negativeButton(@StringRes text: Int? = null, @ColorRes textColor: Int? = null, @DrawableRes background: Int = 0, clickListener: ((dialog: Dialog) -> Unit)? = null): Builder {
            negativeText = text?.let { context.getString(it) }
            negativeTextColor = textColor?.let { ContextCompat.getColor(context, it) } ?: 0
            negativeBackground = background
            negativeButtonClickListener = clickListener
            return this
        }

        fun ratingBarColor(@ColorRes color: Int? = null, @ColorRes backgroundColor: Int = 0): Builder {
            ratingBarColor = color?.let { ContextCompat.getColor(context, it) } ?: 0
            ratingBarBackgroundColor = backgroundColor
            return this
        }

        fun formTitle(@StringRes text: Int? = null, @ColorRes textColor: Int? = null): Builder {
            formTitle = text?.let { context.getString(it) }
            titleTextColor = textColor?.let { ContextCompat.getColor(context, it) } ?: 0
            return this
        }

        fun feedbackTextColor(@ColorRes textColor: Int): Builder {
            feedbackTextColor = textColor.let { ContextCompat.getColor(context, it) }
            return this
        }

        fun formHint(@StringRes text: Int? = null, @ColorRes textColor: Int? = null): Builder {
            feedbackFormHint = text?.let { context.getString(it) }
            hintColor = textColor?.let { ContextCompat.getColor(context, it) } ?: 0
            return this
        }

        fun formSubmitText(@StringRes text: Int? = null, @ColorRes textColor: Int? = null, @ColorRes backgroundColor: Int = 0): Builder {
            submitText = text?.let { context.getString(it) }
            positiveTextColor = textColor?.let { ContextCompat.getColor(context, it) } ?: 0
            positiveBackground = backgroundColor
            return this
        }

        fun formCancelText(@StringRes text: Int? = null, @ColorRes textColor: Int? = null, @ColorRes backgroundColor: Int = 0): Builder {
            cancelText = text?.let { context.getString(it) }
            negativeTextColor = textColor?.let { ContextCompat.getColor(context, it) } ?: 0
            negativeBackground = backgroundColor
            return this
        }

        @Deprecated(message = "Use the threshold cleared listener to set an action", replaceWith = ReplaceWith("onThresholdCleared()"))
        fun playstoreUrl(url: String): Builder {
            marketUrl = url
            return this
        }

        @Deprecated(message = "Use the threshold cleared listener to set an action", replaceWith = ReplaceWith("onThresholdCleared()"))
        fun playstoreUrl(@StringRes url: Int): Builder {
            marketUrl = url.let { context.getString(it) }
            return this
        }

        fun onThresholdCleared(listener: ((dialog: RatingDialog?, rating: Float, thresholdCleared: Boolean) -> Unit)?): Builder {
            ratingThresholdClearedListener = listener
            return this
        }

        fun onThresholdFailed(listener: ((dialog: RatingDialog?, rating: Float, thresholdCleared: Boolean) -> Unit)?): Builder {
            ratingThresholdFailedListener = listener
            return this
        }

        fun onRatingChanged(listener: ((rating: Float, thresholdCleared: Boolean) -> Unit)?): Builder {
            ratingDialogListener = listener
            return this
        }

        fun onRatingBarFormSubmit(listener: ((feedback: String?) -> Unit)?): Builder {
            ratingDialogFormListener = listener
            return this
        }

        fun incrementSessionsAutomatically(value: Boolean): Builder {
            sessionIncrementAutomatic = value
            return this
        }

        fun build(): RatingDialog {
            return RatingDialog(context, this)
        }
    }
}